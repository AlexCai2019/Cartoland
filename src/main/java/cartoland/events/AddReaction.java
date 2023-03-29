package cartoland.events;

import cartoland.utilities.IDAndEntities;
import cartoland.utilities.QuestionForumHandle;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * @since 2.0
 * @author Alex Cai
 */
public class AddReaction extends ListenerAdapter
{
	@Override
	public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event)
	{
		if (!QuestionForumHandle.typedResolved(event.getReaction())) //不是resolved
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

		event.retrieveMessage().queue(message -> QuestionForumHandle.archiveForumPost(forumPost, message));
	}
}