package cartoland.events;

import cartoland.utilities.IDAndEntities;
import cartoland.utilities.QuestionForumHandle;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateArchivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code ThreadEvent} is a listener that triggers when a user create a thread or a thread archived. For now, this only
 * affect Question forum post. This class was registered in {@link cartoland.Cartoland#main}, with the build of
 * JDA.
 *
 * @since 1.5
 * @author Alex Cai
 */
public class ThreadEvent extends ListenerAdapter
{
	@Override
	public void onChannelCreate(ChannelCreateEvent event)
	{
		if (!event.getChannelType().isThread())
			return;

		ThreadChannel threadChannel = event.getChannel().asThreadChannel();
		threadChannel.join().queue(); //加入討論串

		long threadID = threadChannel.getParentChannel().getIdLong();
		//關於問題論壇
		if (threadID == IDAndEntities.QUESTIONS_CHANNEL_ID)
			QuestionForumHandle.createForumPost(threadChannel);
	}

	@Override
	public void onChannelUpdateArchived(ChannelUpdateArchivedEvent event)
	{
		if (!event.getChannelType().isThread())
			return;

		if (Boolean.TRUE.equals(event.getNewValue())) //變成關閉
			return;

		ThreadChannel forumPost = event.getChannel().asThreadChannel();
		if (forumPost.getParentChannel().getIdLong() != IDAndEntities.QUESTIONS_CHANNEL_ID) //不在問題論壇
			return;

		List<ForumTag> tags = new ArrayList<>(forumPost.getAppliedTags());
		tags.remove(IDAndEntities.resolvedForumTag); //移除resolved
		tags.add(IDAndEntities.unresolvedForumTag); //新增unresolved
		forumPost.getManager().setAppliedTags(tags).queue(); //開啟貼文
	}
}