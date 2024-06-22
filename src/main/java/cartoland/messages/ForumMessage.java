package cartoland.messages;

import cartoland.utilities.IDs;
import cartoland.utilities.QuestionForumHandle;
import net.dv8tion.jda.api.entities.Message;
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
		if (forumPost.getParentChannel().getIdLong() == IDs.MAP_DISCUSS_CHANNEL_ID) //是地圖論壇
		{
			if (forumPost.getMessageCount() == 1) //是第一則訊息
				forumPost.retrieveStartMessage().flatMap(Message::pin).queue(); //釘選訊息
		}
		else if (QuestionForumHandle.isQuestionPost(forumPost)) //是疑難雜症
			QuestionForumHandle.getInstance(forumPost).messageEvent(event); //找到handle並執行事件
	}
}