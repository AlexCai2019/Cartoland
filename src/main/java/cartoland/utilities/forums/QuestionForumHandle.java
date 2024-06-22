package cartoland.utilities.forums;

import cartoland.utilities.IDs;
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
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateArchivedEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.managers.channel.concrete.ThreadChannelManager;

import java.awt.Color;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

public final class QuestionForumHandle extends ForumsHandle
{
	private static final QuestionForumHandle instance = new QuestionForumHandle();
	public static QuestionForumHandle getInstance(ThreadChannel forumPost)
	{
		instance.forumPost = forumPost;
		return instance;
	}

	private static final String RESOLVED_FORMAT = "<:resolved:" + IDs.RESOLVED_EMOJI_ID + '>';
	private static final long LAST_MESSAGE_HOUR = 48L;
	private static final String REMIND_MESSAGE =
			"%s，你的問題解決了嗎？如果已經解決了，記得使用`:resolved:` " + RESOLVED_FORMAT + " 表情符號關閉貼文。\n" +
			"如果還沒解決，可以嘗試在問題中加入更多資訊。\n" +
			"%s, did your question got a solution? If it did, remember to close this post using `:resolved:` " + RESOLVED_FORMAT+ " emoji.\n" +
			"If it didn't, try offer more information of question.";

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
	private final Set<Long> unresolvedPosts = new HashSet<>();

	@Override
	public void createEvent(ChannelCreateEvent event)
	{
		TwoTags twoTags = getTwoTags();

		Set<ForumTag> tags = new HashSet<>(forumPost.getAppliedTags());
		tags.remove(twoTags.resolved); //避免使用者自己加resolved
		if (tags.contains(twoTags.unresolved)) //如果使用者有加unresolved
		{
			forumPost.getManager().setAppliedTags(tags).queue(); //直接送出
			return;
		}

		//如果使用者沒有自己加unresolved
		if (tags.size() == ForumChannel.MAX_POST_TAGS) //最多只能4個tag 要留一個位置給unresolved
			tags.remove(tags.iterator().next());
		tags.add(twoTags.unresolved); //直接加上去 反正前面有檢測過了 況且這是set 不會有重複的情況
		forumPost.getManager().setAppliedTags(tags).queue(); //貼文狀態為未解決
	}

	@Override
	public void messageEvent(MessageReceivedEvent event)
	{
		if (forumPost.getMessageCount() == 1) //是第一則訊息
			forumPost.sendMessageEmbeds(startEmbed).queue(); //傳送發問指南
		Message message = event.getMessage();
		if (message.getContentRaw().equals(RESOLVED_FORMAT))
			typedResolved(message);
	}

	@Override
	public void reactionEvent(MessageReactionAddEvent event)
	{
		Member member = event.getMember();
		if (member != null && (forumPost.getOwnerIdLong() == member.getIdLong() || member.hasPermission(Permission.MANAGE_THREADS)) && //必須是本人或是有權限的管理者
				event.getReaction().getEmoji() instanceof CustomEmoji customEmoji && customEmoji.getIdLong() == IDs.RESOLVED_EMOJI_ID) //是:resolved:
			event.retrieveMessage().queue(this::typedResolved); //進入處理階段
	}

	@Override
	public void postSleepEvent(ChannelUpdateArchivedEvent event) {}

	@Override
	public void postWakeUpEvent(ChannelUpdateArchivedEvent event)
	{
		TwoTags twoTags = getTwoTags();
		Set<ForumTag> tags = new HashSet<>(forumPost.getAppliedTags()); //本貼文目前擁有的tag getAppliedTags()回傳的是不可變動的list
		tags.remove(twoTags.resolved); //移除resolved
		tags.add(twoTags.unresolved); //新增unresolved 因為是set所以不用擔心重複
		forumPost.getManager().setAppliedTags(tags).queue(); //貼文狀態為未解決
	}

	public void remind()
	{
		forumPost.retrieveMessageById(forumPost.getLatestMessageIdLong()).queue(lastMessage ->
		{
			User author = lastMessage.getAuthor();
			if (author.isBot() || author.isSystem()) //是機器人或系統
				return; //不用執行

			if (Duration.between(lastMessage.getTimeCreated(), OffsetDateTime.now()).toHours() != LAST_MESSAGE_HOUR) //LAST_MESSAGE_HOUR小時內有人發言
				return;

			String mentionOwner = "<@" + forumPost.getOwnerId() + ">"; //注意這裡使用String型別的get id
			forumPost.sendMessage(String.format(REMIND_MESSAGE, mentionOwner, mentionOwner)).queue(); //提醒開串者
		});
	}

	private void typedResolved(Message message)
	{
		if (!unresolvedPosts.remove(forumPost.getIdLong()))
			return; //已經resolved了

		message.addReaction(Emoji.fromCustom("resolved", IDs.RESOLVED_EMOJI_ID, false)).queue();

		TwoTags twoTags = getTwoTags();
		Set<ForumTag> forumTags = new HashSet<>(forumPost.getAppliedTags()); //獲取標籤們
		forumTags.remove(twoTags.unresolved);
		forumTags.add(twoTags.resolved);
		ThreadChannelManager forumManager = forumPost.getManager();
		forumManager.setAppliedTags(forumTags).queue();

		forumManager.setArchived(true).queue();
	}

	private TwoTags getTwoTags()
	{
		ForumChannel questionsChannel = (ForumChannel) forumPost.getParentChannel();
		return new TwoTags //問題論壇的resolved和unresolved
		(
			questionsChannel.getAvailableTagById(IDs.RESOLVED_FORUM_TAG_ID), //resolved
			questionsChannel.getAvailableTagById(IDs.UNRESOLVED_FORUM_TAG_ID) //unresolved
		); //已解決和未解決
	}

	record TwoTags(ForumTag resolved, ForumTag unresolved) {}
}