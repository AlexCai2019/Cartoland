package cartoland.commands;

import cartoland.utilities.IDAndEntities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.time.OffsetDateTime;
import java.util.regex.Pattern;

/**
 * @since 1.6
 * @author Alex Cai
 */
public class QuoteCommand implements ICommand
{
	private final Pattern linkRegex = Pattern.compile("https://discord\\.com/channels/\\d+/\\d+/\\d+");

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		String link = event.getOption("link", OptionMapping::getAsString);
		if (link == null)
		{
			event.reply("Impossible, this is required!").queue();
			return;
		}

		if (!linkRegex.matcher(link).matches()) //不是一個有效的訊息連結
		{
			event.reply("Please input a valid server message link.").queue();
			return;
		}

		String linkSubString = link.substring(29);
		String[] numbersInLink = linkSubString.split("/");

		long guildID = Long.parseLong(numbersInLink[0]);
		if (guildID != IDAndEntities.CARTOLAND_SERVER_ID) //不在創聯
		{
			event.reply("You can't paste links that are not from Cartoland!").queue();
			return;
		}

		Guild linkGuild = IDAndEntities.jda.getGuildById(guildID);
		if (linkGuild == null)
		{
			event.reply("Impossible, this is the ID of Cartoland!").queue();
			return;
		}

		MessageChannel linkChannel = linkGuild.getChannelById(MessageChannel.class, numbersInLink[1]);
		if (linkChannel == null)
		{
			event.reply("The channel might be deleted or I don't have permission to access.").queue();
			return;
		}

		linkChannel.retrieveMessageById(numbersInLink[2]).queue(linkMessage ->
		{
			User linkAuthor = linkMessage.getAuthor();
			OffsetDateTime createdDateTime = linkMessage.getTimeCreated();

			EmbedBuilder embedBuilder = new EmbedBuilder()
					.setAuthor(linkAuthor.getAsTag(), link, linkAuthor.getAvatarUrl())
					.appendDescription(linkMessage.getContentRaw())
					.setTimestamp(createdDateTime)
					.setFooter(linkChannel.getName());

			linkMessage.getAttachments()
					.stream()
					.filter(Message.Attachment::isImage)
					.findFirst()
					.ifPresent(firstAttachment -> embedBuilder.setImage(firstAttachment.getUrl()));

			event.replyEmbeds(embedBuilder.build()).queue();
		}, new ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE, e ->
				event.reply("The message might be deleted or I don't have permission to access.").queue()));
	}
}