package cartoland.events;

import cartoland.utilities.Algorithm;
import cartoland.utilities.IDs;
import cartoland.utilities.ForumsHandle;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * {@code AddReaction} is a listener that triggers when a user added reaction to a message. This class was registered in
 * {@link cartoland.Cartoland#main}, with the build of JDA.
 *
 * @since 2.0
 * @author Alex Cai
 */
public class AddReaction extends ListenerAdapter
{
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event)
	{
		Member member = event.getMember(); //按表情的成員
		if (member == null) //找不到成員
			return; //結束
		User user = member.getUser(); //案表情的使用者
		if (user.isBot() || user.isSystem()) //是機器人或系統
			return; //不用執行
		Emoji learned = Emoji.fromCustom("learned", 892406442622083143L, false); //宇宙貓貓
		if (Algorithm.chance(20) && event.getReaction().getEmoji().equals(learned)) //20%的機率跟著其他人按
			event.retrieveMessage().queue(message -> message.addReaction(learned).queue());

		//這以下是和問題論壇的resolved有關
		if (!ForumsHandle.typedResolved(event.getReaction())) //不是resolved
			return;
		if (!event.getChannelType().isThread()) //不是討論串 or 論壇貼文
			return;
		ThreadChannel forumPost = (ThreadChannel) event.getChannel();
		if (forumPost.getParentChannel().getIdLong() != IDs.QUESTIONS_CHANNEL_ID) //不在問題論壇
			return;
		if (forumPost.isArchived()) //關閉著的
			return;
		if (user.getIdLong() != forumPost.getOwnerIdLong() && !member.hasPermission(Permission.MANAGE_THREADS))
			return;

		event.retrieveMessage().queue(message -> ForumsHandle.archiveForumPost(forumPost, message));
	}
}