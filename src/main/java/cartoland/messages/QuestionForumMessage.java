package cartoland.messages;

import cartoland.utilities.QuestionForumHandle;
import net.dv8tion.jda.api.entities.Message;
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
	public void messageProcess(MessageReceivedEvent event)
	{
		Message message = event.getMessage();
		if (message.getContentRaw().equals(QuestionForumHandle.resolvedFormat)) //是:resolved:表情符號
			QuestionForumHandle.archiveForumPost(event.getChannel().asThreadChannel(), message);
	}
}