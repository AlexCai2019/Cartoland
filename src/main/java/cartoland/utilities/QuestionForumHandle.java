package cartoland.utilities;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
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
	public static final String resolvedFormat = resolved.getFormatted();
	//private static final Emoji reminder_ribbon = Emoji.fromUnicode("🎗️");
	//private static final String reminder_ribbonFormat = reminder_ribbon.getFormatted();

	private QuestionForumHandle()
	{
		throw new AssertionError(IDAndEntities.YOU_SHALL_NOT_ACCESS);
	}

	public static void archiveForumPost(ThreadChannel forumPost, Message eventMessage)
	{
		List<ForumTag> tags = forumPost.getAppliedTags();
		eventMessage.addReaction(resolved).queue(); //機器人會在訊息上加:resolved:

		//移除🎗️
		//UnsupportedOperationException
//		forumPost.retrieveParentMessage().queue(message ->
//		{
//			if (message.getReactions().stream().anyMatch(messageReaction -> messageReaction.getEmoji().getFormatted().equals(reminder_ribbonFormat)))
//				message.removeReaction(reminder_ribbon, IDAndEntities.botItself).queue();
//		}, throwable ->
//		{
//			throwable.printStackTrace();
//			System.err.print('\u0007');
//			FileHandle.log(throwable);
//		});

		tags = new ArrayList<>(tags);
		tags.remove(IDAndEntities.unresolvedForumTag); //移除unresolved
		tags.add(IDAndEntities.resolvedForumTag); //新增resolved
		forumPost.getManager().setAppliedTags(tags).setArchived(true).queue(); //關閉貼文
	}

	public static boolean forumPostShouldIdle(ThreadChannel forumPost)
	{
		if (forumPost.isArchived())
			return false;

		final boolean[] result = { false }; //lambda 要用
		forumPost.retrieveMessageById(forumPost.getLatestMessageIdLong()).queue(lastMessage ->
		{
			Member messageCreatorMember = lastMessage.getMember();
			if (messageCreatorMember == null)
			{
				result[0] = false;
				return;
			}

			User messageCreatorUser = messageCreatorMember.getUser();
			if (messageCreatorUser.isBot() || messageCreatorUser.isSystem())
			{
				result[0] = false;
				return;
			}

			result[0] = Duration.between(lastMessage.getTimeCreated(), OffsetDateTime.now()).toHours() >= 24L;
		});

		return result[0];
	}

	public static void idleForumPost(ThreadChannel forumPost)
	{
		Member owner = forumPost.getOwner();
		if (owner == null)
			return;

		String mentionOwner = owner.getAsMention();
		forumPost.sendMessage(mentionOwner + "，你的問題解決了嗎？如果已經解決了，記得使用`:resolved:`表情符號關閉貼文。\n" +
									  "如果還沒解決，可以嘗試在問題中加入更多資訊。\n" +
									  mentionOwner + ", did your question got a solution? If it did, remember to close this post using `:resolved:` emoji.\n" +
									  "If it didn't, try offer more information of question.").queue();
		//UnsupportedOperationException
//		forumPost.retrieveParentMessage().queue(message -> message.addReaction(reminder_ribbon).queue(), throwable ->
//		{
//			throwable.printStackTrace();
//			System.err.print('\u0007');
//			FileHandle.log(throwable);
//		});
	}
}