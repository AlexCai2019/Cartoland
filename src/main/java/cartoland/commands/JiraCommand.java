package cartoland.commands;

import cartoland.utilities.CommonFunctions;
import cartoland.utilities.JsonHandle;
import cartoland.utilities.RegularExpressions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.awt.Color;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * {@code JiraCommand} is an execution when a user uses /jira command. This class implements {@link ICommand} interface,
 * which is for the commands HashMap in {@link cartoland.events.CommandUsage}. This class doesn't handle sub
 * commands, but call other classes to deal with it.
 *
 * @since 2.0
 * @author Alex Cai
 */
public class JiraCommand implements ICommand
{
	private static final int MOJANG_RED = new Color(239, 50, 61, 255).getRGB(); //-1101251

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		event.deferReply().queue(); //延後回覆
		InteractionHook hook = event.getHook();
		long userID = event.getUser().getIdLong();
		String inputLink = event.getOption("bug_link", "87984", CommonFunctions.getAsString);

		String theBug = findBugID(inputLink); //將會變成像"MC-87984"那樣的bug ID
		if (theBug.isEmpty())
		{
			hook.sendMessage(JsonHandle.getString(userID, "jira.invalid_link")).setEphemeral(true).queue();
			return;
		}

		String[] bugSplit = theBug.split("-");
		String bugProject = bugSplit[0]; //MC、MCPE等等

		HttpURLConnection conn;

		try
		{
			URL url = new URI("https://bugs.mojang.com/api/jql-search-post").toURL();
			conn = (HttpURLConnection) url.openConnection();

			//設定請求方法與標頭
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);

			//寫入請求
			try (OutputStream os = conn.getOutputStream())
			{
				byte[] input = JsonHandle.bugPostAsString(theBug, bugProject).getBytes(StandardCharsets.UTF_8);
				os.write(input, 0, input.length);
			}
		}
		catch (URISyntaxException | IOException e)
		{
			hook.sendMessage(JsonHandle.getString(userID, "jira.invalid_link")).setEphemeral(true).queue();
			return; //失敗就結束
		}

		StringBuilder response = new StringBuilder(); //用於接收資訊
		try
		{
			int responseCode = conn.getResponseCode();
			if (responseCode != 200 && responseCode != 201) //狀態失敗
			{
				hook.sendMessage(JsonHandle.getString(userID, "jira.no_bug", theBug)).setEphemeral(true).queue();
				return;
			}

			//讀取回傳結果
			try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)))
			{
				while (true)
				{
					String responseLine = in.readLine();
					if (responseLine == null)
						break;
					response.append(responseLine);
				}
			}
			conn.disconnect();
		}
		catch (IOException e)
		{
			hook.sendMessage(JsonHandle.getString(userID, "jira.no_bug", theBug)).setEphemeral(true).queue();
			return;
		}

		Map<String, Object> bugInfo = JsonHandle.getBugInformation(response.toString()); //獲取大部分資訊

		String link = "https://bugs.mojang.com/browse/" + theBug;

		EmbedBuilder bugEmbed = new EmbedBuilder()
				.setThumbnail("https://bugs.mojang.com/jira-favicon-hires.png") //縮圖為Mojang
				.setColor(MOJANG_RED) //左邊的顏色是縮圖的紅色
				.setTitle('[' + theBug + "] " + bugInfo.get("summary"), link); //embed標題是[bug ID]bug標題 點了會連結到jira頁面

		if (bugInfo.isEmpty()) //如果是空的
		{
			hook.sendMessage(link).setEmbeds(bugEmbed.build()).queue();
			return; //直接結束
		}

		//當field被設定為inline時 在電腦版看來 就會是三個排成一列
		String status = maybeMapGet(bugInfo.get("status"), "name");
		bugEmbed.addField("Status", status, true)
				.addField("Resolution", maybeMapGet(bugInfo.get("resolution"), "name"), true)
				.addField("Mojang priority", maybeMapGet(bugInfo.get("customfield_10049"), "value"), true);

		//影響的版本
		String affectsVersions = bugInfo.get("versions") instanceof List<?> versions ? switch (versions.size())
		{
			case 0 -> "";
			case 1 -> maybeMapGet(versions.getFirst(), "name");
			default -> maybeMapGet(versions.getFirst(), "name") + '~' + maybeMapGet(versions.getLast(), "name");
		} : "";
		bugEmbed.addField("Affects versions", affectsVersions, true);

		String fixVersionsString;
		if (bugInfo.get("fixVersions") instanceof List<?> fixVersions)
		{
			switch (fixVersions.size())
			{
				case 0:
					fixVersionsString = "None";
					break;
				case 1:
					fixVersionsString = maybeMapGet(fixVersions.getFirst(), "name");
					break;
				default:
					StringBuilder builder = new StringBuilder();
					for (Object version : fixVersions) //所有的修正版本
						builder.append(maybeMapGet(version, "name")).append(',');
					builder.setLength(builder.length() - 1);
					fixVersionsString = builder.toString();
					break;
			}
		}
		else
			fixVersionsString = "None";
		bugEmbed.addField("Fix version/s", fixVersionsString, true);

		if ("Resolved".equals(status)) //bug已解決
		{
			ZonedDateTime resolvedTime = timeValue(bugInfo.get("resolutiondate"));
			bugEmbed.addField("Resolved", resolvedTime == null ? "None" : "<t:" + resolvedTime.toEpochSecond() + ":R>", true);
		}
		else
			bugEmbed.addField("", "", true);

		bugEmbed.setFooter("").setTimestamp(timeValue(bugInfo.get("created")));

		hook.sendMessage(link).setEmbeds(bugEmbed.build()).queue();
	}

	private String findBugID(String inputLink)
	{
		if (RegularExpressions.BUG_ID_REGEX.matcher(inputLink).matches()) //MC-87984
			return inputLink.toUpperCase(Locale.ROOT); //避免在標題上出現[mc-87984]

		if (RegularExpressions.BUG_NUMBER_REGEX.matcher(inputLink).matches()) //87984
			return "MC-" + inputLink;

		//https://stackoverflow.com/questions/4662215/how-to-extract-a-substring-using-regex
		Matcher browseMatcher = RegularExpressions.JIRA_BROWSE_LINK_REGEX.matcher(inputLink);
		if (browseMatcher.find()) //https://bugs.mojang.com/browse/MC-87984
			return browseMatcher.group(1).toUpperCase(Locale.ROOT); //避免在標題上出現[mc-87984]

		Matcher projectMatcher = RegularExpressions.JIRA_PROJECT_LINK_REGEX.matcher(inputLink);
		if (projectMatcher.find()) //https://bugs.mojang.com/projects/MC/issues/MC-87984
			return projectMatcher.group(1).toUpperCase(Locale.ROOT); //避免在標題上出現[mc-87984]

		return "";
	}

	private String maybeMapGet(Object maybeMap, String key)
	{
		return maybeMap instanceof Map<?,?> map && map.get(key) instanceof CharSequence cs ? cs.toString() : "";
	}

	//2015-09-03T13:30:22+0200
	private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"); //1970-01-01T00:00:00+0000

	private ZonedDateTime timeValue(Object time)
	{
		String timeString = time instanceof String s ? s : "1970-01-01T00:00:00+0000";
		//取得<time>裡的datetime後 透過Formatter轉換為ZonedDateTime物件 再透過toEpochSecond()方法轉換為unix時間
		try
		{
			return ZonedDateTime.parse(timeString, dateTimeFormatter);
		}
		catch (DateTimeParseException e)
		{
			return null;
		}
	}
}