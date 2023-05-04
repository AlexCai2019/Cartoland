package cartoland.commands;

import cartoland.utilities.IDAndEntities;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.EmbedBuilder;
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
	private static final int SUB_STRING_START = ("https://discord.com/channels/" + IDAndEntities.CARTOLAND_SERVER_ID + "/").length();
	private final EmbedBuilder embedBuilder = new EmbedBuilder();

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

		String[] numbersInLink = link.substring(SUB_STRING_START).split("/");

		//從創聯中取得頻道
		MessageChannel linkChannel = IDAndEntities.cartolandServer.getChannelById(MessageChannel.class, Long.parseLong(numbersInLink[0]));
		if (linkChannel == null)
		{
			event.reply("Error: The channel might be deleted, or I don't have permission to access it.").queue();
			return;
		}

		//從頻道中取得訊息 注意ID是String 與慣例的long不同
		linkChannel.retrieveMessageById(numbersInLink[1]).queue(linkMessage ->
		{
			User linkAuthor = linkMessage.getAuthor(); //連結訊息的發送者

			embedBuilder.setAuthor(linkAuthor.getAsTag(), linkAuthor.getEffectiveAvatarUrl(), linkAuthor.getEffectiveAvatarUrl())
					.setDescription(linkMessage.getContentRaw()) //訊息的內容
					.setTimestamp(linkMessage.getTimeCreated()) //連結訊息的發送時間
					.setFooter(linkChannel.getName(), null); //訊息的發送頻道

			//選擇連結訊息內的第一張圖片作為embed的圖片
			//不用add field 沒必要那麼麻煩
			linkMessage.getAttachments()
					.stream()
					.filter(Message.Attachment::isImage)
					.findFirst()
					.ifPresentOrElse(firstAttachment -> embedBuilder.setImage(firstAttachment.getUrl()), () -> embedBuilder.setImage(null));

			event.replyEmbeds(embedBuilder.build())
					.addActionRow(Button.link(link, JsonHandle.getStringFromJsonKey(event.getUser().getIdLong(), "quote.jump_message"))).queue();
		}, new ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE, e ->
				event.reply("Error: The message might be deleted, or I don't have permission to access it.").queue()));
	}
}