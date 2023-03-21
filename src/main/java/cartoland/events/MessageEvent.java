package cartoland.events;

import cartoland.messages.*;
import cartoland.utilities.IDAndEntities;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

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
		new QuestionForumMessage()
	};

	private static final int GUILD_MESSAGE = 0;
	private static final int PRIVATE_MESSAGE = 1;
	private static final int QUESTION_FORUM_MESSAGE = 2;

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event)
	{
		User author = event.getAuthor();
		if (author.isBot() || author.isSystem()) //傳訊息的是機器人或系統
			return; //不用執行

		if (!event.isFromGuild()) //不是來自群組 那肯定是來自私訊了
		{
			messageEvents[PRIVATE_MESSAGE].messageProcess(event);
			return;
		}

		messageEvents[GUILD_MESSAGE].messageProcess(event); //群組訊息

		if (!event.getChannelType().isThread()) //不是討論串 or 論壇貼文
			return;

		if (event.getChannel()
				.asThreadChannel()
				.getParentChannel()
				.getIdLong() == IDAndEntities.QUESTIONS_CHANNEL_ID) //是在問題論壇
			messageEvents[QUESTION_FORUM_MESSAGE].messageProcess(event); //問題論壇訊息
	}
}