package cartoland.messages;

import cartoland.utilities.CommandBlocksHandle;
import cartoland.utilities.IDs;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

import java.util.Set;

/**
 * {@code GuildMessage} is a listener that triggers when a user types anything in any channel that the bot can access.
 * This class is in an array in {@link cartoland.events.MessageEvent}.
 *
 * @since 1.0
 * @author Alex Cai
 */
public class GuildMessage implements IMessage
{
	private final Set<Long> commandBlockCategories = Set.of(IDs.GENERAL_CATEGORY_ID, IDs.TECH_TALK_CATEGORY_ID, IDs.FORUM_CATEGORY_ID, IDs.VOICE_CATEGORY_ID);

	/**
	 * The method that implements from {@link IMessage}, check if the message event need to process.
	 *
	 * @param event Information about the message and its channel and author.
	 * @return If the message need to process.
	 * @since 2.0
	 * @author Alex Cai
	 */
	@Override
	public boolean messageCondition(MessageReceivedEvent event)
	{
		return event.isFromGuild();
	}

	/**
	 * The method that implements from {@link IMessage}, triggers when receive a message from any channel that
	 * the bot has permission to read, but only response when the channel is a text channel and the user isn't
	 * a bot.
	 *
	 * @param event Information about the message and its channel and author.
	 * @throws InsufficientPermissionException When the bot doesn't have permission to react.
	 * @since 1.0
	 * @author Alex Cai
	 */
	@Override
	public void messageProcess(MessageReceivedEvent event)
	{
		Message message = event.getMessage(); //獲取訊息
		String rawMessage = message.getContentRaw(); //獲取訊息字串

		if (rawMessage.contains("learned"))
			message.addReaction(Emoji.fromCustom("learned", IDs.LEARNED_EMOJI_ID, false)).queue();
		if (rawMessage.contains("wow"))
			message.addReaction(Emoji.fromCustom("wow", IDs.WOW_EMOJI_ID, false)).queue();
		if (rawMessage.contains("貓們"))
		{
			message.addReaction(Emoji.fromCustom("learned", IDs.LEARNED_EMOJI_ID, false)).queue();
			message.addReaction(Emoji.fromUnicode("🛐")).queue();
		}

		Category category = message.getCategory(); //嘗試從訊息獲取類別
		//在一般、技術討論區、創作展示或公眾區域類別 且不是在機器人專區
		if (message.getChannel().getIdLong() != IDs.BOT_CHANNEL_ID && category != null && commandBlockCategories.contains(category.getIdLong()))
			CommandBlocksHandle.getLotteryData(message.getAuthor().getIdLong())
					.addBlocks(rawMessage.length() + 1 + message.getAttachments().size() + message.getStickers().size()); //說話加等級 +1當作加上\0 附加一個檔案或貼圖算1個
	}
}