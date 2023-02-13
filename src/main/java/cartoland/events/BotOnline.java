package cartoland.events;

import cartoland.utilities.FileHandle;
import cartoland.utilities.IDAndEntities;
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
		IDAndEntities.cartolandServer = IDAndEntities.jda.getGuildById(IDAndEntities.CARTOLAND_SERVER_ID); //創聯
		if (IDAndEntities.cartolandServer == null)
			problemOccurred("Can't find Cartoland Server");

		IDAndEntities.lobbyChannel = IDAndEntities.cartolandServer.getTextChannelById(IDAndEntities.LOBBY_CHANNEL_ID); //創聯的大廳頻道
		if (IDAndEntities.lobbyChannel == null)
			problemOccurred("Can't find Lobby Channel.");

		IDAndEntities.botChannel = IDAndEntities.cartolandServer.getTextChannelById(IDAndEntities.BOT_CHANNEL_ID); //創聯的機器人頻道
		if (IDAndEntities.botChannel == null)
			problemOccurred("Can't find Bot Channel.");

		IDAndEntities.undergroundChannel = IDAndEntities.cartolandServer.getTextChannelById(IDAndEntities.UNDERGROUND_CHANNEL_ID); //地下聊天室
		if (IDAndEntities.undergroundChannel == null)
			problemOccurred("Can't find Underground Channel.");

		IDAndEntities.memberRole = IDAndEntities.cartolandServer.getRoleById(IDAndEntities.MEMBER_ROLE_ID); //會員身分組
		if (IDAndEntities.memberRole == null)
			problemOccurred("Can't find Member Role.");

		IDAndEntities.botItself = IDAndEntities.jda.getSelfUser();

		String logString = "Cartoland Bot is now online.";
		IDAndEntities.botChannel.sendMessage(logString).queue();
		System.out.println(logString);
		FileHandle.log(logString);
	}

	/**
	 * When an error occurred, an entity is null.
	 *
	 * @param logString The content that will print to standard error stream and log file.
	 */
	private void problemOccurred(String logString)
	{
		System.err.println(logString);
		System.err.print('\u0007');
		FileHandle.log(logString);
		System.exit(1);
	}
}