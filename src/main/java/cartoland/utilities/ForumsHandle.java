package cartoland.utilities;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * {@code ForumsHandle} is a utility class that has functions which controls map-discuss forum and question forum
 * from create to archive. Can not be instantiated or inherited.
 * TODO: refactor
 *
 * @since 2.0
 * @author Alex Cai
 */
public final class ForumsHandle
{
	private ForumsHandle()
	{
		throw new AssertionError(IDs.YOU_SHALL_NOT_ACCESS);
	}

	private static final String RESOLVED_FORMAT = "<:resolved:" + Long.toUnsignedString(IDs.RESOLVED_EMOJI_ID) + '>';
	private static final int CARTOLAND_GREEN = -8009369; //new java.awt.Color(133, 201, 103, 255).getRGB();
	public static final int MAX_TAG = 5;
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
							""".formatted(RESOLVED_FORMAT, RESOLVED_FORMAT))
			.setColor(CARTOLAND_GREEN) //創聯的綠色
			.build();
	private static final String remindMessage =
    		"""
			%%s，你的問題解決了嗎？如果已經解決了，記得使用`:resolved:` %s 表情符號關閉貼文。
			如果還沒解決，可以嘗試在問題中加入更多資訊。
			%%s, did your question got a solution? If it did, remember to close this post using `:resolved:` %s emoji.
			If it didn't, try offer more information of question.
			""".formatted(RESOLVED_FORMAT, RESOLVED_FORMAT);

	private static final String IDLED_QUESTIONS_SET_FILE_NAME = "serialize/idled_questions.ser";
	private static final String HAS_START_MESSAGE_FILE_NAME = "serialize/has_start_message.ser";
	//https://stackoverflow.com/questions/41778276/casting-from-object-to-arraylist
	@SuppressWarnings({"unchecked","rawtypes"})
	private static final Set<Long> idledQuestionForumPosts = FileHandle.deserialize(IDLED_QUESTIONS_SET_FILE_NAME) instanceof HashSet set ? set : new HashSet<>();
	@SuppressWarnings({"unchecked","rawtypes"})
	private static final Set<Long> hasStartMessageForumPosts = FileHandle.deserialize(HAS_START_MESSAGE_FILE_NAME) instanceof HashSet set ? set : new HashSet<>();

	static
	{
		FileHandle.registerSerialize(IDLED_QUESTIONS_SET_FILE_NAME, idledQuestionForumPosts);
		FileHandle.registerSerialize(HAS_START_MESSAGE_FILE_NAME, hasStartMessageForumPosts);
	}

	/**
	 * This method is being used in {@link cartoland.messages.ForumMessage} in order to check if the message event is the first message in a forum post.
	 *
	 * @param forumPost The forum post that needs check.
	 * @return true if this is the first time this forum post received a message
	 * @since 2.0
	 * @author Alex Cai
	 */
	public static boolean isFirstMessage(ThreadChannel forumPost)
	{
		return !hasStartMessageForumPosts.contains(forumPost.getIdLong());
	}

	public static void startStuff(ThreadChannel forumPost)
	{
		long parentChannelID = forumPost.getParentChannel().getIdLong(); //貼文所在的論壇頻道ID
		if (parentChannelID == IDs.MAP_DISCUSS_CHANNEL_ID) //是地圖專版
			forumPost.retrieveStartMessage().queue(message -> message.pin().queue()); //釘選第一則訊息
		else if (parentChannelID == IDs.QUESTIONS_CHANNEL_ID) //是問題論壇
			forumPost.sendMessageEmbeds(startEmbed).queue(); //傳送發問指南
		hasStartMessageForumPosts.add(forumPost.getIdLong());
	}

	public static void createForumPost(ThreadChannel forumPost)
	{
		if (forumPost.getLatestMessageIdLong() != 0) //有初始訊息
			startStuff(forumPost);//釘選第一則訊息 或是傳送發問指南

		ForumChannel parentChannel = forumPost.getParentChannel().asForumChannel(); //貼文所在的論壇頻道
		if (parentChannel.getIdLong() != IDs.QUESTIONS_CHANNEL_ID) //不是問題論壇
			return;

		//這以下是和發問專區(問題論壇)有關的
		ForumTag resolvedForumTag = parentChannel.getAvailableTagById(IDs.RESOLVED_FORUM_TAG_ID); //已解決
		ForumTag unresolvedForumTag = parentChannel.getAvailableTagById(IDs.UNRESOLVED_FORUM_TAG_ID); //未解決

		Set<ForumTag> tags = new HashSet<>(forumPost.getAppliedTags());
		tags.remove(resolvedForumTag); //避免使用者自己加resolved
		if (tags.contains(unresolvedForumTag)) //如果使用者有加unresolved
		{
			forumPost.getManager().setAppliedTags(tags).queue(); //直接送出
			return;
		}

		//如果使用者沒有自己加unresolved
		tags.add(unresolvedForumTag); //直接加上去 反正前面有檢測過了 況且這是set 不會有重複的情況
		forumPost.getManager()
				.setAppliedTags(tags.size() <= ForumsHandle.MAX_TAG ? tags : new ArrayList<>(tags).subList(0, ForumsHandle.MAX_TAG)) //最多只能5個tag
				.queue(); //貼文狀態為未解決
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted") //IntelliJ IDEA 閉嘴
	public static boolean typedResolved(Object withReaction)
	{
		return switch (withReaction)
		{
			case Message message -> RESOLVED_FORMAT.equals(message.getContentRaw());
			case MessageReaction reaction -> reaction.getEmoji().equals(Emoji.fromCustom("resolved", IDs.RESOLVED_EMOJI_ID, false));
			default -> false;
		};
	}

	public static void archiveForumPost(ThreadChannel forumPost, Message eventMessage)
	{
		eventMessage.addReaction(Emoji.fromCustom("resolved", IDs.RESOLVED_EMOJI_ID, false)).queue(); //機器人會在訊息上加:resolved:
		ForumChannel questionsChannel = forumPost.getParentChannel().asForumChannel(); //問題論壇
		ForumTag resolvedForumTag = questionsChannel.getAvailableTagById(IDs.RESOLVED_FORUM_TAG_ID); //已解決
		ForumTag unresolvedForumTag = questionsChannel.getAvailableTagById(IDs.UNRESOLVED_FORUM_TAG_ID); //未解決

		Set<ForumTag> tags = new HashSet<>(forumPost.getAppliedTags());
		tags.remove(unresolvedForumTag); //移除unresolved
		tags.add(resolvedForumTag); //新增resolved
		forumPost.getManager().setAppliedTags(tags).queue();
		idledQuestionForumPosts.remove(forumPost.getIdLong());

		//移除🎗️ 並關閉貼文
		unIdleQuestionForumPost(forumPost, true);
	}

	public static void tryIdleQuestionForumPost(ThreadChannel forumPost)
	{
		long forumPostID = forumPost.getIdLong();
		if (forumPost.isArchived() || forumPost.isLocked() || forumPost.getParentChannel().getIdLong() != IDs.QUESTIONS_CHANNEL_ID) //已經關閉 或已經鎖起來了 或不是問題論壇
		{
			idledQuestionForumPosts.remove(forumPostID);
			return;
		}

		forumPost.retrieveMessageById(forumPost.getLatestMessageIdLong()).queue(lastMessage ->
		{
			User author = lastMessage.getAuthor();
			if (author.isBot() || author.isSystem()) //是機器人或系統
				return; //不用執行

			if (Duration.between(lastMessage.getTimeCreated(), OffsetDateTime.now()).toHours() < LAST_MESSAGE_HOUR) //LAST_MESSAGE_HOUR小時內有人發言
				return;

			String mentionOwner = "<@" + forumPost.getOwnerId() + ">"; //注意這裡使用String型別的get id
			forumPost.sendMessage(String.format(remindMessage, mentionOwner, mentionOwner)).queue(); //提醒開串者

			idledQuestionForumPosts.add(forumPost.getIdLong()); //記錄這個貼文正在idle

			//增加🎗️
			forumPost.retrieveStartMessage().queue(message -> message.addReaction(Emoji.fromUnicode("🎗️")).queue());
		}, new ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE, e ->
		{
			String mentionOwner = "<@" + forumPost.getOwnerId() + ">"; //注意這裡使用String型別的get id
			forumPost.sendMessage(String.format(remindMessage, mentionOwner, mentionOwner)).queue();
		}));
	}

	public static void unIdleQuestionForumPost(ThreadChannel forumPost, boolean archive)
	{
		//如果 貼文已被關閉 或 貼文已鎖定 或 貼文不屬於疑難雜症
		if (forumPost.isArchived() || forumPost.isLocked() || forumPost.getParentChannel().getIdLong() != IDs.QUESTIONS_CHANNEL_ID)
			return;

		forumPost.retrieveStartMessage().queue(message ->
		{
			Emoji reminder_ribbon = Emoji.fromUnicode("🎗️");
			if (message.getReactions().stream().anyMatch(reaction -> reaction.getEmoji().equals(reminder_ribbon))) //如果第一則訊息有🎗️
				message.removeReaction(reminder_ribbon).queue(); //移除🎗️

			idledQuestionForumPosts.remove(forumPost.getIdLong()); //將貼文從idle列表中移除

			if (archive)
				forumPost.getManager().setArchived(true).queue(); //關閉貼文
		});
	}

	public static boolean questionForumPostIsIdled(ThreadChannel forumPost)
	{
		return forumPost.getParentChannel().getIdLong() == IDs.QUESTIONS_CHANNEL_ID && idledQuestionForumPosts.contains(forumPost.getIdLong());
	}
}