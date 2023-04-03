package cartoland.messages;

import cartoland.utilities.IDAndEntities;
import cartoland.utilities.QuestionForumHandle;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * {@code QuestionForumMessage} is a listener that triggers when a user types anything in any post in Questions
 * forum channel. This class is in an array in {@link cartoland.events.MessageEvent}.
 *
 * @since 1.5
 * @author Alex Cai
 */
public class QuestionForumMessage implements IMessage
{
	@Override
	public boolean messageCondition(MessageReceivedEvent event)
	{
		MessageChannelUnion channel = event.getChannel();
		return channel.getType().isThread() &&
				channel.asThreadChannel().getParentChannel().getIdLong() == IDAndEntities.QUESTIONS_CHANNEL_ID;
	}

	@Override
	public void messageProcess(MessageReceivedEvent event)
	{
		Message message = event.getMessage();
		ThreadChannel forumPost = event.getChannel().asThreadChannel();
		if (QuestionForumHandle.isIdled(forumPost))
			QuestionForumHandle.unIdleForumPost(forumPost, false);

		if (QuestionForumHandle.typedResolved(message)) //是:resolved:表情符號
			QuestionForumHandle.archiveForumPost(forumPost, message);
	}
}