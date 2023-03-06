package cartoland.commands;

import cartoland.utilities.IDAndEntities;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.util.regex.Pattern;

/**
 * {@code QuoteCommand} is an execution when a user uses /transfer command. This class implements {@link ICommand}
 * interface, which is for the commands HashMap in {@link cartoland.events.CommandUsage}.
 *
 * @since 1.6
 * @author Alex Cai
 */
public class QuoteCommand implements ICommand
{
	private final Pattern linkRegex = Pattern.compile("https://discord\\.com/channels/" + IDAndEntities.CARTOLAND_SERVER_ID + "/\\d+/\\d+");

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		String link = event.getOption("link", OptionMapping::getAsString);
		if (link == null)
		{
			event.reply("Impossible, this is required!").queue();
			return;
		}

		if (!linkRegex.matcher(link).matches()) //不是一個有效的訊息連結 或不在創聯
		{
			event.reply("Invalid input: Please input a message link from Cartoland.").queue();
			return;
		}

		String linkSubString = link.substring(29);
		String[] numbersInLink = linkSubString.split("/");

		Guild linkGuild = IDAndEntities.jda.getGuildById(numbersInLink[0]);
		if (linkGuild == null)
		{
			event.reply("Impossible, this is the ID of Cartoland!").queue();
			return;
		}

		MessageChannel linkChannel = linkGuild.getChannelById(MessageChannel.class, numbersInLink[1]);
		if (linkChannel == null)
		{
			event.reply("Error: The channel might be deleted, or I don't have permission to access it.").queue();
			return;
		}

		linkChannel.retrieveMessageById(numbersInLink[2]).queue(linkMessage ->
		{
			User linkAuthor = linkMessage.getAuthor(); //連結訊息的發送者

			EmbedBuilder embedBuilder = new EmbedBuilder()
					.setAuthor(linkAuthor.getAsTag(), linkAuthor.getAvatarUrl(), linkAuthor.getAvatarUrl())
					.appendDescription(linkMessage.getContentRaw()) //訊息的內容
					.setTimestamp(linkMessage.getTimeCreated()) //連結訊息的發送時間
					.setFooter(linkChannel.getName(), null); //訊息的發送頻道

			//選擇連結訊息內的第一張圖片作為embed的圖片
			//不用add field 沒必要那麼麻煩
			linkMessage.getAttachments()
					.stream()
					.filter(Message.Attachment::isImage)
					.findFirst()
					.ifPresent(firstAttachment -> embedBuilder.setImage(firstAttachment.getUrl()));

			event.replyEmbeds(embedBuilder.build())
					.addActionRow(Button.link(link, JsonHandle.getJsonKey(event.getUser().getIdLong(), "quote.jump_message"))).queue();
		}, new ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE, e ->
				event.reply("Error: The message might be deleted, or I don't have permission to access it.").queue()));
	}
}