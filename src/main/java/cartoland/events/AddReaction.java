package cartoland.events;

import cartoland.utilities.IDs;
import cartoland.utilities.QuestionForumHandle;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * {@code AddReaction} is a listener that triggers when a user added reaction to a message. This class was registered in
 * {@link cartoland.Cartoland#main(String[])}, with the build of JDA.
 *
 * @since 2.0
 * @author Alex Cai
 */
public class AddReaction extends ListenerAdapter
{
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event)
	{
		Member member = event.getMember();
		if (member == null) //按表情的成員
			return;
		User user = member.getUser();
		if (user.isBot() || user.isSystem()) //是機器人或系統
			return; //不用執行

		Emoji emoji = event.getEmoji();
		Emoji learned = Emoji.fromCustom("learned", IDs.LEARNED_EMOJI_ID, false); //宇宙貓貓
		if (emoji.equals(learned))
			event.retrieveMessage().flatMap(message -> message.addReaction(learned)).queue();

		//有關疑難雜症
		if (event.getChannel() instanceof ThreadChannel thread && QuestionForumHandle.isQuestionPost(thread))
			event.retrieveMessage().queue(message -> QuestionForumHandle.getInstance(thread).reactionEvent(member, message, emoji)); //加表情的事件
	}
}