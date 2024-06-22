package cartoland.utilities.forums;

import cartoland.utilities.IDs;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateArchivedEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

/**
 * {@code ForumsHandle} is a utility class that has functions which controls map-discuss forum and question forum
 * from create to archive. Can not be instantiated or inherited.
 * TODO: refactor
 *
 * @since 2.0
 * @author Alex Cai
 */
public abstract sealed class ForumsHandle permits DiscussForumHandle, QuestionForumHandle, EmptyHandle
{
	protected ThreadChannel forumPost;
	public static ForumsHandle getHandle(ThreadChannel forumPost)
	{
		long parentID = forumPost.getParentChannel().getIdLong();
		if (parentID == IDs.MAP_DISCUSS_CHANNEL_ID) //是地圖論壇
			return DiscussForumHandle.getInstance(forumPost);
		else if (parentID == IDs.QUESTIONS_CHANNEL_ID) //是疑難雜症
			return QuestionForumHandle.getInstance(forumPost);
		else
			return EmptyHandle.instance;
	}

	public abstract void createEvent(ChannelCreateEvent event);
	public abstract void messageEvent(MessageReceivedEvent event);
	public abstract void reactionEvent(MessageReactionAddEvent event);
	public abstract void postSleepEvent(ChannelUpdateArchivedEvent event);
	public abstract void postWakeUpEvent(ChannelUpdateArchivedEvent event);
}

final class EmptyHandle extends ForumsHandle
{
	static EmptyHandle instance = new EmptyHandle();

	@Override
	public void createEvent(ChannelCreateEvent event) {}

	@Override
	public void messageEvent(MessageReceivedEvent event) {}

	@Override
	public void reactionEvent(MessageReactionAddEvent event) {}

	@Override
	public void postSleepEvent(ChannelUpdateArchivedEvent event) {}

	@Override
	public void postWakeUpEvent(ChannelUpdateArchivedEvent event) {}
}