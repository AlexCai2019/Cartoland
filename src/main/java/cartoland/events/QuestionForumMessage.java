package cartoland.events;

import cartoland.utilities.IDAndEntities;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code QuestionForumMessage} is a listener that triggers when a user types anything in any forum post in
 * Questions forum channel. This class was registered in {@link cartoland.Cartoland#main}, with the build of JDA.
 *
 * @since 1.5
 * @author Alex Cai
 */
public class QuestionForumMessage extends ListenerAdapter
{
	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event)
	{
		User author = event.getAuthor();
		if (author.isBot() || author.isSystem()) //是機器人或系統
			return;

		if (!event.getChannelType().isThread()) //不是討論串 or 論壇貼文
			return;
		ThreadChannel forumPost = event.getChannel().asThreadChannel();
		if (forumPost.getParentChannel().getIdLong() != IDAndEntities.QUESTIONS_CHANNEL_ID) //不在問題論壇
			return;

		List<ForumTag> tags = forumPost.getAppliedTags();
		if (!forumPost.isArchived()) //開啟著的
		{
			if (event.getMessage().getContentRaw().equals("✅"))
			{
				tags = new ArrayList<>(tags);
				tags.remove(IDAndEntities.unresolvedForumTag);
				tags.add(IDAndEntities.resolvedForumTag);
				forumPost.getManager().setAppliedTags(tags).setArchived(true).queue(); //關閉貼文
			}
		}
		else //已關閉的
		{
			tags = new ArrayList<>(tags);
			tags.remove(IDAndEntities.resolvedForumTag);
			tags.add(IDAndEntities.unresolvedForumTag);
			forumPost.getManager().setAppliedTags(tags).queue(); //打開貼文
		}
	}

	@Override
	public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event)
	{
		;
	}
}