package cartoland.utilities;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @since 2.0
 * @author Alex Cai
 */
public class QuestionForumHandle
{
	private static final Emoji resolved = Emoji.fromCustom("resolved", 1081082902785314921L, false);
	private static final String resolvedFormat = resolved.getFormatted();
	private static final Emoji reminder_ribbon = Emoji.fromUnicode("🎗️");
	private static final int CARTOLAND_GREEN = -8009369; //new java.awt.Color(133, 201, 103, 255).getRGB();
	private static final int MAX_TAG = 5;
	private static final long LAST_MESSAGE_HOUR = 48L;
	private static final MessageEmbed startEmbed = new EmbedBuilder()
			.setTitle("**-=發問指南=-**", "https://discord.com/channels/886936474723950603/1079081061658673253/1079081061658673253")
			.setDescription("""
							-=發問指南=-
														
							• 請清楚說明你想做什麼，並想要什麼結果。
							• 請提及你正在使用的Minecraft版本，以及是否正在使用任何模組。
							• 討論完成後，使用 `:resolved:` %s 表情符號關閉貼文。
														
							-=Guidelines=-
							       
							• Ask your question straight and clearly, tell us what you are trying to do.
							• Mention which Minecraft version you are using and any mods.
							• Remember to use `:resolved:` %s to close the post after resolved.
							""".formatted(resolvedFormat, resolvedFormat))
			.setColor(CARTOLAND_GREEN) //創聯的綠色
			.build();
	private static final String remindMessage =
    		"""
			%%s，你的問題解決了嗎？如果已經解決了，記得使用`:resolved:` %s 表情符號關閉貼文。
			如果還沒解決，可以嘗試在問題中加入更多資訊。
			%%s, did your question got a solution? If it did, remember to close this post using `:resolved:` %s emoji.
			If it didn't, try offer more information of question.
			""".formatted(resolvedFormat, resolvedFormat);

	private static final String IDLED_QUESTIONS_SET_FILE_NAME = "idled_questions.ser";
	//https://stackoverflow.com/questions/41778276/casting-from-object-to-arraylist
	private static final Set<Long> idledForumPosts = FileHandle.deserialize(IDLED_QUESTIONS_SET_FILE_NAME) instanceof Set<?> set ?
			set.stream().map(element -> (Long)element).collect(Collectors.toSet()) : new HashSet<>();

	private QuestionForumHandle()
	{
		throw new AssertionError(IDAndEntities.YOU_SHALL_NOT_ACCESS);
	}

	public static void serializeIdlesSet()
	{
		FileHandle.serialize(IDLED_QUESTIONS_SET_FILE_NAME, idledForumPosts);
	}

	public static void createForumPost(ThreadChannel forumPost)
	{
		forumPost.sendMessageEmbeds(startEmbed).queue(); //傳送發問指南

		List<ForumTag> tags = new ArrayList<>(forumPost.getAppliedTags());
		tags.remove(IDAndEntities.resolvedForumTag); //避免使用者自己加resolved
		if (tags.contains(IDAndEntities.unresolvedForumTag)) //如果使用者有加unresolved
		{
			forumPost.getManager().setAppliedTags(tags).queue(); //直接送出
			return;
		}

		if (tags.size() >= MAX_TAG) //不可以超過MAX_TAG個tag
			tags.remove(MAX_TAG - 1); //移除最後一個 空出位置給unresolved
		tags.add(IDAndEntities.unresolvedForumTag);
		forumPost.getManager().setAppliedTags(tags).queue();
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
		eventMessage.addReaction(resolved).queue(); //機器人會在訊息上加:resolved:
		List<ForumTag> tags = new ArrayList<>(forumPost.getAppliedTags());
		tags.remove(IDAndEntities.unresolvedForumTag); //移除unresolved
		tags.add(IDAndEntities.resolvedForumTag); //新增resolved
		forumPost.getManager().setAppliedTags(tags).queue();
		idledForumPosts.remove(forumPost.getIdLong());

		//移除🎗️ 並關閉貼文
		unIdleForumPost(forumPost, true);
	}

	public static void tryIdleForumPost(ThreadChannel forumPost)
	{
		if (forumPost.isArchived() || forumPost.isLocked())
			return;

		forumPost.retrieveMessageById(forumPost.getLatestMessageIdLong()).queue(lastMessage ->
		{
			User author = lastMessage.getAuthor();
			if (author.isBot() || author.isSystem())
				return;

			if (Duration.between(lastMessage.getTimeCreated(), OffsetDateTime.now()).toHours() < LAST_MESSAGE_HOUR) //LAST_MESSAGE_HOUR小時內有人發言
				return;

			String mentionOwner = "<@" + forumPost.getOwnerIdLong() + ">";
			forumPost.sendMessage(String.format(remindMessage, mentionOwner, mentionOwner)).queue();

			idledForumPosts.add(forumPost.getIdLong());

			//增加🎗️
			forumPost.getIterableHistory().reverse().limit(1).queue(messages ->
			{
				if (messages.size() > 0)
					messages.get(0).addReaction(reminder_ribbon).queue();
			});
		});
	}

	public static void unIdleForumPost(ThreadChannel forumPost, boolean archive)
	{
		if (forumPost.isArchived() || forumPost.isLocked())
			return;

		forumPost.getIterableHistory().reverse().limit(1).queue(messages ->
		{
			if (messages.size() > 0)
			{
				Message message = messages.get(0);
				if (message.getReactions().stream().anyMatch(reaction -> reaction.getEmoji().equals(reminder_ribbon)))
					message.removeReaction(reminder_ribbon).queue();
			}

			idledForumPosts.remove(forumPost.getIdLong());

			if (archive)
				forumPost.getManager().setArchived(true).queue(); //關閉貼文
		});
	}

	public static boolean isIdled(ThreadChannel forumPost)
	{
		return idledForumPosts.contains(forumPost.getIdLong());
	}
}