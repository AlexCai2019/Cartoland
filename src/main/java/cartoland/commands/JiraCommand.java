package cartoland.commands;

import cartoland.utilities.OptionFunctions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * @since 2.0
 * @author Alex Cai
 */
public class JiraCommand implements ICommand
{
	private final Pattern jiraLinkRegex = Pattern.compile("https://bugs\\.mojang\\.com/browse/(?i)MC(PE)?-\\d{1,6}");
	private final Pattern bugIDRegex = Pattern.compile("(?i)MC(PE)?-\\d{1,6}");
	private final Pattern numberRegex = Pattern.compile("\\d{1,6}");
	private final int subStringStart = "https://bugs.mojang.com/browse/".length();
	private static final int MOJANG_RED = -1101251; //new java.awt.Color(239, 50, 61, 255).getRGB();
	private final EmbedBuilder bugEmbed = new EmbedBuilder()
			.setThumbnail("https://bugs.mojang.com/jira-favicon-hires.png")
			.setColor(MOJANG_RED);

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		String link = event.getOption("bug_link", OptionFunctions.getAsString);
		if (link == null)
		{
			event.reply("Impossible, this is required!").queue();
			return;
		}

		final String finalLink; //lambda要用
		final String finalBugID;
		if (jiraLinkRegex.matcher(link).matches()) //是bug的連結
		{
			finalLink = link;
			finalBugID = link.substring(subStringStart);
		}
		else if (bugIDRegex.matcher(link).matches()) //MC-123456
		{
			finalLink = "https://bugs.mojang.com/browse/" + link;
			finalBugID = link;
		}
		else if (numberRegex.matcher(link).matches()) //純數字
		{
			finalLink = "https://bugs.mojang.com/browse/MC-" + link;
			finalBugID = "MC-" + link;
		}
		else
		{
			event.reply("Please enter a valid Minecraft bug link or ID").queue();
			return;
		}

		event.deferReply().queue(interactionHook ->
		{
			Document document;

			try
			{
				document = Jsoup.connect(finalLink).get();
			}
			catch (IOException e)
			{
				interactionHook.sendMessage("There's no bug report for " + finalBugID).queue();
				return;
			}

			Element issueContent = document.getElementById("issue-content"); //這樣之後就不用總是從整個document內get element
			if (issueContent == null)
			{
				interactionHook.sendMessage("Can't get issue content")
						.addActionRow(Button.link(finalLink, "Jira"))
						.queue();
				return;
			}

			Element title = issueContent.getElementById("summary-val");
			bugEmbed.setTitle('[' + finalBugID + "] " + (title != null ? title.text() : ""), finalLink).clearFields();
			bugEmbedAddField("Status", issueContent.getElementById("opsbar-transitions_more"));
			bugEmbedAddField("Resolution", issueContent.getElementById("resolution-val"));
			bugEmbedAddField("Mojang priority", issueContent.getElementById("customfield_12200-val"));
			Element affectsVersions = issueContent.getElementById("versions-field");
			bugEmbedAddField("First affects version", affectsVersions != null ? affectsVersions.child(0) : null);
			bugEmbedAddField("Fix version/s", issueContent.getElementById("fixfor-val"));
			bugEmbedAddField("Reporter", issueContent.getElementById("reporter-val"));
			interactionHook.sendMessageEmbeds(bugEmbed.build()).addActionRow(Button.link(finalLink, "Jira")).queue();
		});
	}

	private void bugEmbedAddField(String fieldName, Element fieldValue)
	{
		bugEmbed.addField(fieldName, fieldValue != null ? fieldValue.text() : "", true);
	}
}