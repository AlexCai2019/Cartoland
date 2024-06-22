package cartoland.events;

import cartoland.utilities.Algorithm;
import cartoland.utilities.IDs;
import cartoland.utilities.QuestionForumHandle;
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
		User user = event.getUser(); //按表情的使用者
		if (user == null || user.isBot() || user.isSystem()) //是機器人或系統
			return; //不用執行
		if (Algorithm.chance(20)) //20%的機率跟著其他人按
		{
			Emoji learned = Emoji.fromCustom("learned", IDs.LEARNED_EMOJI_ID, false); //宇宙貓貓
			if (event.getReaction().getEmoji().equals(learned))
				event.retrieveMessage().flatMap(message -> message.addReaction(learned)).queue();
		}

		//有關疑難雜症
		if (event.getChannel() instanceof ThreadChannel thread && QuestionForumHandle.isQuestionPost(thread))
			QuestionForumHandle.getInstance(thread).reactionEvent(event); //加表情的事件
	}
}