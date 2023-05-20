package cartoland.events;

import cartoland.utilities.Algorithm;
import cartoland.utilities.IDAndEntities;
import cartoland.utilities.QuestionForumHandle;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * @since 2.0
 * @author Alex Cai
 */
public class AddReaction extends ListenerAdapter
{
	private final Emoji learned = Emoji.fromCustom("learned", 892406442622083143L, false);

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event)
	{
		Member member = event.getMember();
		if (member == null)
			return;
		User user = member.getUser();
		if (user.isBot() || user.isSystem()) //是機器人或系統
			return;
		if (Algorithm.chance(10) && event.getReaction().getEmoji().equals(learned))
			event.retrieveMessage().queue(message -> message.addReaction(learned).queue());
		if (QuestionForumHandle.notTypedResolved(event.getReaction())) //不是resolved
			return;
		if (!event.getChannelType().isThread()) //不是討論串 or 論壇貼文
			return;
		ThreadChannel forumPost = event.getChannel().asThreadChannel();
		if (forumPost.getParentChannel().getIdLong() != IDAndEntities.QUESTIONS_CHANNEL_ID) //不在問題論壇
			return;
		if (forumPost.isArchived()) //關閉著的
			return;
		if (user.getIdLong() != forumPost.getOwnerIdLong() && !member.hasPermission(Permission.MANAGE_THREADS))
			return;

		event.retrieveMessage().queue(message -> QuestionForumHandle.archiveForumPost(forumPost, message));
	}
}