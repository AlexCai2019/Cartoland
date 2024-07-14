package cartoland.commands;

import cartoland.utilities.CommonFunctions;
import cartoland.utilities.JsonHandle;
import cartoland.utilities.RegularExpressions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.Color;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Matcher;

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
	private static final int DESCRIPTION_CHARACTERS = 200;

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		event.deferReply().queue(); //延後回覆
		InteractionHook hook = event.getHook();
		long userID = event.getUser().getIdLong();
		String inputLink = event.getOption("bug_link", "87984", CommonFunctions.getAsString);

		String bugID = findBugID(inputLink); //將會變成像"MC-87984"那樣的bug ID
		if (bugID.isEmpty())
		{
			hook.sendMessage(JsonHandle.getString(userID, "jira.invalid_link")).setEphemeral(true).queue();
			return;
		}
		String link = "https://bugs.mojang.com/browse/" + bugID;

		Document document; //HTML文件
		try
		{
			document = Jsoup.connect(link).get(); //嘗試連線
		}
		catch (IOException e)
		{
			hook.sendMessage(JsonHandle.getString(userID, "jira.no_bug", bugID)).setEphemeral(true).queue();
			return;
		}

		Element issueContent = document.getElementById("issue-content"); //這樣之後就不用總是從整個document內get element
		if (issueContent == null) //如果不存在id為issue-content的標籤
		{
			hook.sendMessage(JsonHandle.getString(userID, "jira.no_issue", link)).setEphemeral(true).queue();
			return;
		}

		EmbedBuilder bugEmbed = new EmbedBuilder()
				.setThumbnail("https://bugs.mojang.com/jira-favicon-hires.png") //縮圖為Mojang
				.setColor(MOJANG_RED) //左邊的顏色是縮圖的紅色
				.setTitle('[' + bugID + "] " + textValue(issueContent.getElementById("summary-val")), link); //embed標題是[bug ID]bug標題 點了會連結到jira頁面

		String description = textValue(issueContent.getElementById("description-val")).strip(); //bug描述
		int descriptionLength = description.length(); //小於等於DESCRIPTION_CHARACTERS就全文放下
		bugEmbed.appendDescription(descriptionLength <= DESCRIPTION_CHARACTERS ? description : new StringBuilder(description).replace(DESCRIPTION_CHARACTERS - 1, descriptionLength, "…"))

		//如果該HTML元素不為null 就取該元素的文字 否則放空字串 比起找不到就直接回傳embed 使用者們較能一目了然
				.addField("Status", textValue(issueContent.getElementById("opsbar-transitions_more")), true)
				.addField("Resolution", textValue(issueContent.getElementById("resolution-val")), true)
				.addField("Mojang priority", textValue(issueContent.getElementById("customfield_12200-val")), true);

		Element versionsField = issueContent.getElementById("versions-field");
		Element allAffectsVersions = versionsField != null ? versionsField : new Element("span");

		//此處不用getFirst()和getLast() 因為first()和last()會在沒有元素時回傳null 而不是擲出NoSuchElementException
		bugEmbed.addField("First affects version", textValue(allAffectsVersions.firstElementChild()), true)
				.addField("Last affects version", textValue(allAffectsVersions.lastElementChild()), true)
				.addField("Fix version/s", textValue(issueContent.getElementById("fixfor-val")), true)

		//當field被設定為inline時 在電腦版看來 就會是三個排成一列
				.addField("Created", timeValue(issueContent.getElementById("created-val")), true)
				.addField("Updated", timeValue(issueContent.getElementById("updated-val")), true)
				.addField("Resolved", timeValue(issueContent.getElementById("resolutiondate-val")), true)

				.addField("Checked", timeValue(issueContent.getElementById("customfield_10701-val")), true)
				.addField("Votes", textValue(issueContent.getElementById("vote-data")), true)
				.addField("Watchers", textValue(issueContent.getElementById("watcher-data")), true)

				.setFooter(textValue(issueContent.getElementById("project-name-val")),
						attributeValue(issueContent.getElementById("project-avatar"), "src", null));

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
			return "<t:" + ZonedDateTime.parse(attributeValue(timeTags.getFirst(), "datetime", "1970-01-01T00:00:00+0000"), dateTimeFormatter).toEpochSecond() + ":R>";
		}
		catch (DateTimeParseException e)
		{
			return "";
		}
	}

	private String attributeValue(Element element, String attributeKey, String defaultValue)
	{
		return element != null ? element.attr(attributeKey) : defaultValue;
	}
}