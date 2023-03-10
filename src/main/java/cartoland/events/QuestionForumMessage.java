package cartoland.events;

import cartoland.utilities.IDAndEntities;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code QuestionForumMessage} is a listener that triggers when a user types anything or add reaction in any post
 * in Questions forum channel. This class was registered in {@link cartoland.Cartoland#main}, with the build of
 * JDA.
 *
 * @since 1.5
 * @author Alex Cai
 */
public class QuestionForumMessage extends ListenerAdapter
{
	private final Emoji resolved = Emoji.fromCustom("resolved", 1081082902785314921L, false);
	private final String resolvedFormat = resolved.getFormatted();

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event)
	{
		if (!event.getChannelType().isThread()) //不是討論串 or 論壇貼文
			return;
		ThreadChannel forumPost = event.getChannel().asThreadChannel();
		if (forumPost.getParentChannel().getIdLong() != IDAndEntities.QUESTIONS_CHANNEL_ID) //不在問題論壇
			return;

		//以下是問題論壇的部分
		User author = event.getAuthor();
		if (author.isBot() || author.isSystem()) //是機器人或系統
			return;

		List<ForumTag> tags = forumPost.getAppliedTags();
		if (!forumPost.isArchived()) //開啟著的
		{
			Message message = event.getMessage();
			if (!message.getContentRaw().equals(resolvedFormat)) //不是:resolved:表情符號
				return;

			message.addReaction(resolved).queue(); //機器人會加:resolved:
			tags = new ArrayList<>(tags);
			tags.remove(IDAndEntities.unresolvedForumTag); //移除unresolved
			tags.add(IDAndEntities.resolvedForumTag); //新增resolved
			forumPost.getManager().setAppliedTags(tags).setArchived(true).queue(); //關閉貼文
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
		if (!event.getReaction().getEmoji().getFormatted().equals(resolvedFormat)) //不是resolved
			return;
		User user = event.getUser();
		if (user == null || user.isBot() || user.isSystem()) //是機器人或系統
			return;
		if (!event.getChannelType().isThread()) //不是討論串 or 論壇貼文
			return;
		ThreadChannel forumPost = event.getChannel().asThreadChannel();
		if (forumPost.getParentChannel().getIdLong() != IDAndEntities.QUESTIONS_CHANNEL_ID) //不在問題論壇
			return;
		if (forumPost.isArchived()) //關閉著的
			return;

		forumPost.retrieveMessageById(event.getMessageId()).queue(message -> message.addReaction(resolved).queue()); //機器人會加:resolved:
		List<ForumTag> tags = new ArrayList<>(forumPost.getAppliedTags());
		tags.remove(IDAndEntities.unresolvedForumTag); //移除unresolved
		tags.add(IDAndEntities.resolvedForumTag); //新增resolved
		forumPost.getManager().setAppliedTags(tags).setArchived(true).queue(); //關閉貼文
	}
}