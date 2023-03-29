package cartoland.utilities;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.managers.channel.concrete.ThreadChannelManager;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @since 2.0
 * @author Alex Cai
 */
public class QuestionForumHandle
{
	private static final Emoji resolved = Emoji.fromCustom("resolved", 1081082902785314921L, false);
	private static final String resolvedFormat = resolved.getFormatted();
	private static final Emoji reminder_ribbon = Emoji.fromUnicode("🎗️");

	private QuestionForumHandle()
	{
		throw new AssertionError(IDAndEntities.YOU_SHALL_NOT_ACCESS);
	}

	public static boolean typedResolved(Object withReaction)
	{
		if (withReaction instanceof Message message)
			return message.getContentRaw().equals(resolvedFormat);
		else if (withReaction instanceof MessageReaction reaction)
			return reaction.getEmoji().equals(resolved);
		else
			return false;
	}

	public static void archiveForumPost(ThreadChannel forumPost, Message eventMessage)
	{
		ThreadChannelManager manager = forumPost.getManager();
		eventMessage.addReaction(resolved).queue(); //機器人會在訊息上加:resolved:
		List<ForumTag> tags = new ArrayList<>(forumPost.getAppliedTags());
		tags.remove(IDAndEntities.unresolvedForumTag); //移除unresolved
		tags.add(IDAndEntities.resolvedForumTag); //新增resolved
		manager.setAppliedTags(tags).queue();

		//移除🎗️ 並關閉貼文
		forumPost.getIterableHistory().reverse().limit(1).queue(messages ->
		{
			if (messages.size() > 0)
			{
				Message message = messages.get(0);
				if (message.getReactions().stream().anyMatch(reaction -> reaction.getEmoji().equals(reminder_ribbon)))
					message.removeReaction(reminder_ribbon).queue();
			}

			manager.setArchived(true).queue(); //關閉貼文

		}, throwable ->
		{
			throwable.printStackTrace();
			System.err.print('\u0007');
			FileHandle.log(throwable);
		});
	}

	public static void idleForumPost(ThreadChannel forumPost)
	{
		if (forumPost.isArchived() || forumPost.isLocked())
			return;

		forumPost.retrieveMessageById(forumPost.getLatestMessageIdLong()).queue(lastMessage ->
		{
			User author = lastMessage.getAuthor();
			if (author.isBot() || author.isSystem())
				return;

			if (Duration.between(lastMessage.getTimeCreated(), OffsetDateTime.now()).toHours() < 24L)
				return;

			String mentionOwner = "<@" + forumPost.getOwnerIdLong() + ">";
			forumPost.sendMessage(mentionOwner + "，你的問題解決了嗎？如果已經解決了，記得使用`:resolved:`表情符號關閉貼文。\n" +
										  "如果還沒解決，可以嘗試在問題中加入更多資訊。\n" +
										  mentionOwner + ", did your question got a solution? If it did, remember to close this post using `:resolved:` emoji.\n" +
										  "If it didn't, try offer more information of question.").queue();

			//增加🎗️
			forumPost.getIterableHistory().reverse().limit(1).queue(messages ->
			{
				if (messages.size() > 0)
					messages.get(0).addReaction(reminder_ribbon).queue();
			}, throwable ->
			{
				throwable.printStackTrace();
				System.err.print('\u0007');
				FileHandle.log(throwable);
			});
		});
	}
}