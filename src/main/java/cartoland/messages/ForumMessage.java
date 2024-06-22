package cartoland.messages;

import cartoland.utilities.forums.ForumsHandle;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * {@code ForumMessage} is a listener that triggers when a user types anything in any post in Map-Discuss forum
 * channel and Questions
 * forum channel. This class is in an array in {@link cartoland.events.MessageEvent}.
 *
 * @since 1.5
 * @author Alex Cai
 */
public class ForumMessage implements IMessage
{
	private ThreadChannel forumPost;

	@Override
	public boolean messageCondition(MessageReceivedEvent event)
	{
		return event.getChannel() instanceof ThreadChannel thread && (forumPost = thread).getParentChannel().getType() == ChannelType.FORUM;
	}

	@Override
	public void messageProcess(MessageReceivedEvent event)
	{
		ForumsHandle.getHandle(forumPost).messageEvent(event); //找到handle並執行事件
	}
}