package cartoland.commands;

import cartoland.methods.IAnalyzeCTLCLink;
import cartoland.utilities.JsonHandle;
import cartoland.utilities.RegularExpressions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code QuoteCommand} is an execution when a user uses /transfer command. This class implements {@link ICommand}
 * interface, which is for the commands HashMap in {@link cartoland.events.CommandUsage}.
 *
 * @since 1.6
 * @author Alex Cai
 */
public class QuoteCommand implements ICommand, IAnalyzeCTLCLink
{
	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		String link = event.getOption("link", "", OptionMapping::getAsString);
		if (RegularExpressions.CARTOLAND_MESSAGE_LINK_REGEX.matcher(link).matches()) //必須是有效的創聯訊息連結
		{
			event.deferReply().queue(); //延後回覆
			analyze(event, link);
		}
		else
			event.reply(JsonHandle.getString(event.getUser().getIdLong(), "quote.invalid_link")).setEphemeral(true).queue();
	}

	@Override
	public void whenSuccess(IReplyCallback event, Message message)
	{
		quoteMessage(event, message);
	}

	@Override
	public void whenFail(IReplyCallback event, String link, int failCode)
	{
		String jsonKey = switch (failCode)
		{
			case NO_GUILD -> "quote.invalid_link";
			case NO_CHANNEL -> "quote.no_channel";
			default -> "quote.no_message";
		};
		event.getHook().sendMessage(JsonHandle.getString(event.getUser().getIdLong(), jsonKey)).setEphemeral(true).queue(); //回覆
	}

	public static void quoteMessage(IReplyCallback event, Message message)
	{
		User user = event.getUser(); //使用指令的使用者
		User author = message.getAuthor(); //連結訊息的發送者
		String messageTitle = author.getEffectiveName();
		String messageLink = message.getJumpUrl();
		List<MessageEmbed> embeds = new ArrayList<>(); //要被送出的所有embed們
		EmbedBuilder messageEmbed = new EmbedBuilder()
				.setTitle(messageTitle, messageLink)
				.setAuthor(author.getName(), null, author.getEffectiveAvatarUrl())
				.appendDescription(message.getContentRaw()) //訊息的內容
				.setTimestamp(message.getTimeCreated()) //連結訊息的發送時間
				.setFooter(message.getChannel().getName(), null); //訊息的發送頻道

		List<Message.Attachment> attachments = message.getAttachments(); //訊息的附件

		List<Message.Attachment> images;
		if (attachments.isEmpty() || (images = attachments.stream().filter(Message.Attachment::isImage).toList()).isEmpty()) //沒有任何附件或圖片
			embeds.add(messageEmbed.build());
		else //有圖片
		{
			embeds.add(messageEmbed.setImage(images.getFirst().getUrl()).build()); //第一個要放訊息embed
			for (int i = 1, size = Math.min(images.size(), Message.MAX_EMBED_COUNT); i < size; i++) //剩下的要開新embed, 注意總數不能超過10個
				embeds.add(new EmbedBuilder().setTitle(messageTitle, messageLink).setImage(images.get(i).getUrl()).build());
		}

		//提及訊息作者 vs 不提及訊息作者
		WebhookMessageCreateAction<Message> messageCreateAction;
		if (event instanceof SlashCommandInteractionEvent commandEvent && commandEvent.getOption("mention_author", Boolean.FALSE, OptionMapping::getAsBoolean))
			messageCreateAction = event.getHook().sendMessage(JsonHandle.getString(user.getIdLong(), "quote.mention", user.getEffectiveName(), author.getAsMention())).addEmbeds(embeds);
		else
			messageCreateAction = event.getHook().sendMessageEmbeds(embeds); //不標註作者
		messageCreateAction.addComponents(ActionRow.of(Button.link(messageLink, JsonHandle.getString(user.getIdLong(), "quote.jump_message")))).queue(); //連結按鈕
	}
}