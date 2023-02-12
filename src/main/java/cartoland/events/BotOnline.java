package cartoland.events;

import cartoland.utilities.FileHandle;
import cartoland.utilities.IDAndEntities;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * {@code BotOnline} is a listener that triggers when this bot was online. This class was registered in
 * {@link cartoland.Cartoland#main}, with the build of JDA. This class helps all the entities (except {@link IDAndEntities#jda}) in
 * {@link IDAndEntities} get their instances.
 *
 * @since 1.0
 * @author Alex Cai
 */
public class BotOnline extends ListenerAdapter
{
	/**
	 * The method that inherited from {@link ListenerAdapter}, triggers when the bot was online. It will send
	 * online message to bot channel.
	 *
	 * @param event The event that carries information.
	 */
	@Override
	public void onReady(@NotNull ReadyEvent event)
	{
		String logString;
		IDAndEntities.botChannel = IDAndEntities.jda.getChannelById(TextChannel.class, IDAndEntities.BOT_CHANNEL_ID); //創聯的機器人頻道
		if (IDAndEntities.botChannel == null)
		{
			logString = "Can't find Bot Channel.";
			System.err.println(logString);
			System.err.print('\u0007');
			FileHandle.log(logString);
			System.exit(1);
		}

		IDAndEntities.undergroundChannel = IDAndEntities.jda.getChannelById(TextChannel.class, IDAndEntities.UNDERGROUND_CHANNEL_ID); //地下聊天室
		if (IDAndEntities.undergroundChannel == null)
		{
			logString = "Can't find Underground Channel.";
			System.err.println(logString);
			System.err.print('\u0007');
			FileHandle.log(logString);
			System.exit(1);
		}

		IDAndEntities.botItself = IDAndEntities.jda.getSelfUser();

		logString = "Cartoland Bot is now online.";
		IDAndEntities.botChannel.sendMessage(logString).queue();
		System.out.println(logString);
		FileHandle.log(logString);
	}
}