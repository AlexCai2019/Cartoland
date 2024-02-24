package cartoland.commands;

import cartoland.utilities.CommonFunctions;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Pattern;

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
	private final Pattern jiraLinkRegex = Pattern.compile("https://bugs\\.mojang\\.com/browse/(?i)(MC(PE|D|L|LG)?|REALMS)-\\d{1,6}");
	private final Pattern bugIDRegex = Pattern.compile("(?i)(MC(PE|D|L|LG)?|REALMS)-\\d{1,6}");
	private final Pattern numberRegex = Pattern.compile("\\d{1,6}"); //目前bug數還沒超過999999個 等超過了再來改
	private final int subStringStart = "https://bugs.mojang.com/browse/".length();
	private static final int MOJANG_RED = -1101251; //new java.awt.Color(239, 50, 61, 255).getRGB();

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		event.deferReply().queue(); //延後回覆
		InteractionHook hook = event.getHook();
		long userID = event.getUser().getIdLong();
		String link = event.getOption("bug_link", "87984", CommonFunctions.getAsString);

		String bugID; //將會變成像"MC-87984"那樣的bug ID
		if (jiraLinkRegex.matcher(link).matches()) //https://bugs.mojang.com/browse/MC-87984
			bugID = link.substring(subStringStart).toUpperCase(Locale.ROOT); //避免在標題上出現[mc-87984]
		else if (bugIDRegex.matcher(link).matches()) //MC-87984
			bugID = link.toUpperCase(Locale.ROOT); //避免在標題上出現[mc-87984]
		else if (numberRegex.matcher(link).matches()) //87984
			bugID = "MC-" + link;
		else
		{
			hook.sendMessage(JsonHandle.getStringFromJsonKey(userID, "jira.invalid_link")).setEphemeral(true).queue();
			return;
		}
		link = "https://bugs.mojang.com/browse/" + bugID;

		Document document; //HTML文件

		try
		{
			document = Jsoup.connect(link).get(); //嘗試連線
		}
		catch (IOException e)
		{
			hook.sendMessage(JsonHandle.getStringFromJsonKey(userID, "jira.no_bug").formatted(bugID)).setEphemeral(true).queue();
			return;
		}

		Element issueContent = document.getElementById("issue-content"); //這樣之後就不用總是從整個document內get element
		if (issueContent == null) //如果不存在id為issue-content的標籤
		{
			hook.sendMessage(JsonHandle.getStringFromJsonKey(userID, "jira.no_issue")).setEphemeral(true).queue();
			return;
		}

		EmbedBuilder bugEmbed = new EmbedBuilder()
				.setThumbnail("https://bugs.mojang.com/jira-favicon-hires.png") //縮圖為Mojang
				.setColor(MOJANG_RED) //左邊的顏色是縮圖的紅色
				.setTitle('[' + bugID + "] " + textValue(issueContent.getElementById("summary-val")), link) //embed標題是[bug ID]bug標題 點了會連結到jira頁面

		//如果該HTML元素不為null 就取該元素的文字 否則放空字串 比起找不到就直接回傳embed 使用者們較能一目了然
				.addField("Status", textValue(issueContent.getElementById("opsbar-transitions_more")), true)
				.addField("Resolution", textValue(issueContent.getElementById("resolution-val")), true)
				.addField("Mojang priority", textValue(issueContent.getElementById("customfield_12200-val")), true);

		Element allAffectsVersions = issueContent.getElementById("versions-field");
		if (allAffectsVersions != null)
		{
			Elements affectsVersions = allAffectsVersions.children();
			//此處不用getFirst()和getLast() 因為first()和last()會在沒有元素時回傳null 而不是擲出NoSuchElementException
			bugEmbed.addField("First affects version", textValue(affectsVersions.first()), true)
					.addField("Last affects version", textValue(affectsVersions.last()), true);
		}

		bugEmbed.addField("Fix version/s", textValue(issueContent.getElementById("fixfor-val")), true)

		//當field被設定為inline時 在電腦版看來 就會是三個排成一列
				.addField("Created", timeValue(issueContent.getElementById("created-val")), true)
				.addField("Updated", timeValue(issueContent.getElementById("updated-val")), true)
				.addField("Resolved", timeValue(issueContent.getElementById("resolutiondate-val")), true)

				.addField("Reporter", textValue(issueContent.getElementById("reporter-val")), true)
				.addField("Votes", textValue(issueContent.getElementById("vote-data")), true)
				.addField("Watchers", textValue(issueContent.getElementById("watcher-data")), true);

		Element projectAvatar = issueContent.getElementById("project-avatar");
		bugEmbed.setFooter(textValue(issueContent.getElementById("project-name-val")), projectAvatar == null ? null : projectAvatar.attr("src"));

		hook.sendMessage(link).setEmbeds(bugEmbed.build()).queue();
	}

	private String textValue(Element element)
	{
		return element != null ? element.text() : "";
	}

	//2015-09-03T13:30:22+0200
	private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

	private String timeValue(Element element)
	{
		if (element == null)
			return "";
		Elements timeTags = element.getElementsByTag("time"); //找尋裡面的<time>
		if (timeTags.isEmpty())
			return "";
		//取得<time>裡的datetime後 透過Formatter轉換為ZonedDateTime物件 再透過toEpochSecond()方法轉換為unix時間
		try
		{
			return "<t:" + ZonedDateTime.parse(timeTags.getFirst().attr("datetime"), dateTimeFormatter).toEpochSecond() + ":R>";
		}
		catch (DateTimeParseException e)
		{
			return "";
		}
	}
}