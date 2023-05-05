package cartoland.events;

import cartoland.messages.*;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * {@code MessageEvent} is a listener that triggers when a user types anything. This class was registered in
 * {@link cartoland.Cartoland#main}, with the build of JDA.
 *
 * @since 2.0
 * @author Alex Cai
 */
public class MessageEvent extends ListenerAdapter
{
	private final IMessage[] messageEvents =
	{
		new GuildMessage(),
		new PrivateMessage(),
		new BotCanTalkChannelMessage(),
		new QuestionForumMessage(),
		new IntroduceMessage()
	};

	@Override
	public void onMessageReceived(MessageReceivedEvent event)
	{
		User author = event.getAuthor();
		if (author.isBot() || author.isSystem()) //傳訊息的是機器人或系統
			return; //不用執行

		for (IMessage messageEvent : messageEvents)
			if (messageEvent.messageCondition(event)) //讓類別自己檢測是否通過
				messageEvent.messageProcess(event); //執行訊息事件
	}
}