package cartoland.messages;

import cartoland.utilities.IDs;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

/**
 * {@code GuildMessage} is a listener that triggers when a user types anything in any channel that the bot can access.
 * This class is in an array in {@link cartoland.events.MessageEvent}.
 *
 * @since 1.0
 * @author Alex Cai
 */
public class GuildMessage implements IMessage
{
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
	}
}