package cartoland.commands;

import cartoland.utilities.OptionFunctions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
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
	private final EmbedBuilder bugEmbed = new EmbedBuilder().setThumbnail("https://bugs.mojang.com/jira-favicon-hires.png");

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
						.addActionRow(Button.link("Jira", finalLink))
						.queue();
				return;
			}

			Element title = issueContent.getElementById("summary-val");
			if (title == null)
			{
				interactionHook.sendMessage("Unable to get the title of this bug")
						.addActionRow(Button.link("Jira", finalLink))
						.queue();
				return;
			}
			bugEmbed.setTitle('[' + finalBugID + "] " + title.text(), finalLink)
					.clearFields();

			Element status = issueContent.getElementById("opsbar-transitions_more");
			if (status == null)
			{
				sendBugEmbeds(interactionHook, finalLink);
				return;
			}
			bugEmbed.addField("Status", status.text(), true);

			Element resolution = issueContent.getElementById("resolution-val");
			if (resolution == null)
			{
				sendBugEmbeds(interactionHook, finalLink);
				return;
			}
			bugEmbed.addField("Resolution", resolution.text(), true);

			Element priority = issueContent.getElementById("customfield_12200-val");
			if (priority == null)
			{
				sendBugEmbeds(interactionHook, finalLink);
				return;
			}
			bugEmbed.addField("Mojang priority", priority.text(), true);

			Element affectsVersions = issueContent.getElementById("versions-field");
			if (affectsVersions == null)
			{
				sendBugEmbeds(interactionHook, finalLink);
				return;
			}

			Element firstVersion = affectsVersions.children().first();
			if (firstVersion == null)
			{
				sendBugEmbeds(interactionHook, finalLink);
				return;
			}
			bugEmbed.addField("First affects version", firstVersion.text(), true);

			Element fixVersion = issueContent.getElementById("fixfor-val");
			if (fixVersion == null)
			{
				sendBugEmbeds(interactionHook, finalLink);
				return;
			}
			bugEmbed.addField("Fix version/s", fixVersion.text(), true);

			Element reporter = issueContent.getElementById("reporter-val");
			if (reporter == null)
			{
				sendBugEmbeds(interactionHook, finalLink);
				return;
			}
			bugEmbed.addField("Reporter", reporter.text(), true);

			sendBugEmbeds(interactionHook, finalLink);
		});
	}

	private void sendBugEmbeds(InteractionHook interactionHook, String bugLink)
	{
		interactionHook.sendMessageEmbeds(bugEmbed.build())
				.addActionRow(Button.link("Jira", bugLink))
				.queue();
	}
}