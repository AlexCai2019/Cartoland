package cartoland.messages;

import cartoland.utilities.AnonymousHandle;
import cartoland.utilities.FileHandle;
import cartoland.utilities.ObjectAndString;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * {@code PrivateMessage} is a listener that triggers when a user types anything in the direct message to the bot. This
 * class is in an array in {@link cartoland.events.MessageEvent}.
 *
 * @since 1.0
 * @author Alex Cai
 */
public class PrivateMessage implements IMessage
{
	@Override
	public boolean messageCondition(MessageReceivedEvent event)
	{
		return !event.isFromGuild();
	}

	@Override
	public void messageProcess(MessageReceivedEvent event)
	{
		Message message = event.getMessage();
		User author = event.getAuthor();

		ObjectAndString channelAndString = AnonymousHandle.checkMemberValid(author.getIdLong());
		String errorMessage = channelAndString.string();
		if (!errorMessage.isEmpty()) //空字串代表沒有錯誤
		{
			message.reply(errorMessage).mentionRepliedUser(false).queue();
			return;
		}

		message.forwardTo((TextChannel) channelAndString.object());

		FileHandle.dmLog(author.getName(), ' ', author.getId(), ' ', message.getContentRaw());
	}
}