package cartoland.utilities.forums;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateArchivedEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public final class DiscussForumHandle extends ForumsHandle
{
	private static final DiscussForumHandle instance = new DiscussForumHandle();
	public static DiscussForumHandle getInstance(ThreadChannel forumPost)
	{
		instance.forumPost = forumPost;
		return instance;
	}

	@Override
	public void createEvent(ChannelCreateEvent event) {}

	@Override
	public void messageEvent(MessageReceivedEvent event)
	{
		if (forumPost.getMessageCount() == 1) //是第一則訊息
			forumPost.retrieveStartMessage().flatMap(Message::pin).queue(); //釘選訊息
	}

	@Override
	public void reactionEvent(MessageReactionAddEvent event) {}

	@Override
	public void postSleepEvent(ChannelUpdateArchivedEvent event) {}

	@Override
	public void postWakeUpEvent(ChannelUpdateArchivedEvent event) {}
}