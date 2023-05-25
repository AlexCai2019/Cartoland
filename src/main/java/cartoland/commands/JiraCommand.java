package cartoland.commands;

import cartoland.utilities.CommonFunctions;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
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
	private final Pattern jiraLinkRegex = Pattern.compile("https://bugs\\.mojang\\.com/browse/(?i)(MC(PE|D|L)?|REALMS)-\\d{1,6}");
	private final Pattern bugIDRegex = Pattern.compile("(?i)(MC(PE|D|L)?|REALMS)-\\d{1,6}");
	private final Pattern numberRegex = Pattern.compile("\\d{1,6}");
	private final int subStringStart = "https://bugs.mojang.com/browse/".length();
	private static final int MOJANG_RED = -1101251; //new java.awt.Color(239, 50, 61, 255).getRGB();
	private final EmbedBuilder bugEmbed = new EmbedBuilder()
			.setThumbnail("https://bugs.mojang.com/jira-favicon-hires.png")
			.setColor(MOJANG_RED);

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		long userID = event.getUser().getIdLong();
		String link = event.getOption("bug_link", CommonFunctions.getAsString);
		if (link == null)
		{
			event.reply("Impossible, this is required!").queue();
			return;
		}

		String bugID; //MC-87984
		if (jiraLinkRegex.matcher(link).matches()) //https://bugs.mojang.com/browse/MC-87984
			bugID = link.substring(subStringStart).toUpperCase(Locale.ROOT); //避免在標題上出現[mc-87984]
		else if (bugIDRegex.matcher(link).matches()) //MC-87984
			bugID = link.toUpperCase(Locale.ROOT); //避免在標題上出現[mc-87984]
		else if (numberRegex.matcher(link).matches()) //87984
			bugID = "MC-" + link;
		else
		{
			event.reply(JsonHandle.getStringFromJsonKey(userID, "jira.invalid_link")).queue();
			return;
		}
		link = "https://bugs.mojang.com/browse/" + bugID;

		event.deferReply().queue(); //延後回覆
		InteractionHook eventHook = event.getHook();

		Document document; //HTML文件

		try
		{
			document = Jsoup.connect(link).get(); //嘗試連線
		}
		catch (IOException e)
		{
			eventHook.sendMessage(JsonHandle.getStringFromJsonKey(userID, "jira.no_bug").formatted(bugID)).queue();
			return;
		}

		Element issueContent = document.getElementById("issue-content"); //這樣之後就不用總是從整個document內get element
		if (issueContent == null) //如果不存在id為issue-content的標籤
		{
			eventHook.sendMessage(JsonHandle.getStringFromJsonKey(userID, "jira.no_issue"))
					.addActionRow(Button.link(link, "Jira")).queue();
			return;
		}

		Element title = issueContent.getElementById("summary-val");
		bugEmbed.setTitle('[' + bugID + "] " + (title != null ? title.text() : ""), link).clearFields();
		bugEmbedAddField("Status", issueContent.getElementById("opsbar-transitions_more"));
		bugEmbedAddField("Resolution", issueContent.getElementById("resolution-val"));
		bugEmbedAddField("Mojang priority", issueContent.getElementById("customfield_12200-val"));
		Element affectsVersions = issueContent.getElementById("versions-field");
		bugEmbedAddField("First affects version", affectsVersions != null ? affectsVersions.child(0) : null);
		bugEmbedAddField("Fix version/s", issueContent.getElementById("fixfor-val"));
		bugEmbedAddField("Reporter", issueContent.getElementById("reporter-val"));
		eventHook.sendMessageEmbeds(bugEmbed.build()).addActionRow(Button.link(link, "Jira")).queue();
	}

	private void bugEmbedAddField(String fieldName, Element fieldValue)
	{
		//如果該HTML元素不為null 就取該元素的文字 否則放空字串 比起找不到就直接回傳embed 使用者們較能一目了然
		bugEmbed.addField(fieldName, fieldValue != null ? fieldValue.text() : "", true);
	}
}