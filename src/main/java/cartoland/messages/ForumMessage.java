package cartoland.messages;

import cartoland.utilities.IDs;
import cartoland.utilities.QuestionForumHandle;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Set;

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
	private final Set<Long> pinFirstMessage = Set.of(IDs.MAP_DISCUSS_CHANNEL_ID, IDs.CREATION_CHANNEL_ID, IDs.RESOURCE_CHANNEL_ID);

	@Override
	public boolean messageCondition(MessageReceivedEvent event)
	{
		return event.getChannel() instanceof ThreadChannel thread && (forumPost = thread).getParentChannel().getType() == ChannelType.FORUM;
	}

	@Override
	public void messageProcess(MessageReceivedEvent event)
	{
		long parentID = forumPost.getParentChannel().getIdLong();
		if (pinFirstMessage.contains(parentID)) //是地圖論壇、作品展示或素材頻道
		{
			Message message = event.getMessage();
			if (forumPost.getIdLong() == message.getIdLong()) //是第一則訊息
				message.pin().queue(); //釘選訊息
		}
		else if (QuestionForumHandle.isQuestionPost(parentID)) //是疑難雜症
			QuestionForumHandle.getInstance(forumPost).messageEvent(event); //找到handle並執行事件
	}
}