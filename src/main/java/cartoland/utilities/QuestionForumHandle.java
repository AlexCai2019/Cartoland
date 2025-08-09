package cartoland.utilities;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.managers.channel.concrete.ThreadChannelManager;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.awt.Color;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

public final class QuestionForumHandle //這東西坦白講重構了還是蠻💩的
{
	private static final QuestionForumHandle instance = new QuestionForumHandle();
	public static QuestionForumHandle getInstance(ThreadChannel forumPost)
	{
		instance.forumPost = forumPost;
		instance.forumManager = forumPost.getManager();
		return instance;
	}

	private static final String RESOLVED_FORMAT = "<:resolved:" + IDs.RESOLVED_EMOJI_ID + '>';
	private static final long LAST_MESSAGE_HOUR = 48L;
	public static boolean isQuestionPost(ThreadChannel forumPost)
	{
		return isQuestionPost(forumPost.getParentChannel().getIdLong());
	}
	public static boolean isQuestionPost(long parentID)
	{
		return parentID == IDs.QUESTIONS_CHANNEL_ID;
	}

	private QuestionForumHandle() {}

	private ThreadChannel forumPost;
	private ThreadChannelManager forumManager;
	private final MessageEmbed startEmbed = new EmbedBuilder()
			.setTitle("**-=發問指南=-**", "https://discord.com/channels/886936474723950603/1079081061658673253/1079081061658673253")
			.appendDescription("""
								-=發問指南=-
								
								• 請清楚說明你想做什麼，並想要什麼結果。
								• 請提及你正在使用的Minecraft版本，以及是否正在使用任何模組。
								• 討論完成後，使用 `:resolved:` %s 表情符號關閉貼文。
								
								-=Guidelines=-
								
								• Ask your question straight and clearly, tell us what you are trying to do.
								• Mention which Minecraft version you are using and any mods.
								• Remember to use `:resolved:` %s to close the post after resolved.
								""".formatted(RESOLVED_FORMAT, RESOLVED_FORMAT))
			.setColor(new Color(133, 201, 103, 255).getRGB()) //創聯的綠色 -8009369
			.build();

	public void createEvent()
	{
		DatabaseHandle.addUnresolvedQuestion(forumPost.getIdLong()); //未解決
		setResolved(false); //開始貼文
	}

	public void messageEvent(Message message)
	{
		if (forumPost.getIdLong() == message.getIdLong()) //是第一則訊息
			forumPost.sendMessageEmbeds(startEmbed).queue(); //傳送發問指南
		if (message.getContentRaw().equals(RESOLVED_FORMAT)) //輸入了resolved表情符號
			typedResolved(message);
	}

	public void reactionEvent(Member member, Message message, Emoji emoji)
	{
		if (hasPermission(member) && emoji instanceof CustomEmoji customEmoji && customEmoji.getIdLong() == IDs.RESOLVED_EMOJI_ID) //是:resolved:
			typedResolved(message); //進入處理階段
	}

	private boolean hasPermission(Member member)
	{
		return forumPost.getOwnerIdLong() == member.getIdLong() || member.hasPermission(Permission.MANAGE_THREADS); //是本人或是有權限的管理者
	}

	public void postWakeUpEvent()
	{
		setResolved(false); //既然醒來了就設定tag
		DatabaseHandle.addUnresolvedQuestion(forumPost.getIdLong()); //未解決
	}

	public void remind()
	{
		forumPost.retrieveMessageById(forumPost.getLatestMessageIdLong()).queue(lastMessage ->
		{
			User author = lastMessage.getAuthor();
			if (author.isBot() || author.isSystem() || Duration.between(lastMessage.getTimeCreated(), OffsetDateTime.now()).toHours() != LAST_MESSAGE_HOUR)
				return; //是機器人或系統 或上次有人發言不是在LAST_MESSAGE_HOUR小時前 就不用執行

			String mentionOwner = "<@" + forumPost.getOwnerId() + '>'; //注意這裡使用String型別的get id
			forumPost.sendMessage(mentionOwner + "，你的問題解決了嗎？如果已經解決了，記得使用`:resolved:` " + RESOLVED_FORMAT + " 表情符號關閉貼文。\n" +
								"如果還沒解決，可以嘗試在問題中加入更多資訊。\n" +
								mentionOwner + ", did your question got a solution? If it did, remember to close this post using `:resolved:` " + RESOLVED_FORMAT + " emoji.\n" +
								"If it didn't, try offer more information of question.")
					.queue(); //提醒開串者
		}, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));
	}

	private void typedResolved(Message message)
	{
		if (!DatabaseHandle.removeUnresolvedQuestion(forumPost.getIdLong())) //resolved失敗 代表已經resolved了 又用:resolved:訊息叫醒
			return;

		//以下是resolved成功
		message.addReaction(Emoji.fromCustom("resolved", IDs.RESOLVED_EMOJI_ID, false)).queue(); //加上表情符號
		setResolved(true); //關閉貼文
	}

	private void setResolved(boolean isResolve)
	{
		ForumChannel questionsChannel = (ForumChannel) forumPost.getParentChannel();
		ForumTag resolvedTag = questionsChannel.getAvailableTagById(IDs.RESOLVED_FORUM_TAG_ID); //resolved
		ForumTag unresolvedTag = questionsChannel.getAvailableTagById(IDs.UNRESOLVED_FORUM_TAG_ID); //unresolved

		Set<ForumTag> forumTags = new HashSet<>(forumPost.getAppliedTags()); //獲取標籤們
		if (isResolve)
		{
			forumTags.add(resolvedTag);
			forumTags.remove(unresolvedTag);
		}
		else
		{
			forumTags.add(unresolvedTag);
			forumTags.remove(resolvedTag);
		}

		if (forumTags.size() <= ForumChannel.MAX_POST_TAGS) //最多只能5個tag
		{
			forumManager.setAppliedTags(forumTags).setArchived(isResolve).queue();
			return;
		}

		//太多tag了
		ForumTag[] shrinkTags = new ForumTag[ForumChannel.MAX_POST_TAGS]; //縮小後的tag們
		if (isResolve) //resolved和unresolved只能擇一
		{
			shrinkTags[ForumChannel.MAX_POST_TAGS - 1] = resolvedTag; //resolvedTag轉移到list
			forumTags.remove(resolvedTag);
		}
		else
		{
			shrinkTags[ForumChannel.MAX_POST_TAGS - 1] = unresolvedTag; //unresolvedTag轉移到list
			forumTags.remove(unresolvedTag);
		}

		int counter = ForumChannel.MAX_POST_TAGS - 1; //任意地填滿剩下4個tag
		for (ForumTag tag : forumTags)
		{
			counter--;
			shrinkTags[counter] = tag;
			if (counter == 0)
				break; //forumTags內還有的其他元素就不管了
		}
		forumManager.setAppliedTags(shrinkTags).setArchived(isResolve).queue();
	}
}