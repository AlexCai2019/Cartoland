package cartoland.events;

import cartoland.utilities.FileHandle;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static cartoland.utilities.IDAndEntities.*;

/**
 * {@code BotOnline} is a listener that triggers when this bot was online. This class was registered in
 * {@link cartoland.Cartoland#main}, with the build of JDA. This class helps all the entities (except
 * {@link cartoland.utilities.IDAndEntities#jda}) in {@link cartoland.utilities.IDAndEntities} get their instances.
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
		cartolandServer = jda.getGuildById(CARTOLAND_SERVER_ID); //創聯
		if (cartolandServer == null)
			problemOccurred("Can't find Cartoland Server");

		lobbyChannel = cartolandServer.getTextChannelById(LOBBY_CHANNEL_ID); //創聯的大廳頻道
		if (lobbyChannel == null)
			problemOccurred("Can't find Lobby Channel.");

		botChannel = cartolandServer.getTextChannelById(BOT_CHANNEL_ID); //創聯的機器人頻道
		if (botChannel == null)
			problemOccurred("Can't find Bot Channel.");

		undergroundChannel = cartolandServer.getTextChannelById(UNDERGROUND_CHANNEL_ID); //創聯的地下聊天室
		if (undergroundChannel == null)
			problemOccurred("Can't find Underground Channel.");

		memberRole = cartolandServer.getRoleById(MEMBER_ROLE_ID); //會員身分組
		if (memberRole == null)
			problemOccurred("Can't find Member Role.");

		nsfwRole = cartolandServer.getRoleById(NSFW_ROLE_ID); //地下身分組
		if (nsfwRole == null)
			problemOccurred("Can't find NSFW Role.");

		botItself = jda.getSelfUser(); //機器人自己

		ohBoy3AM();

		String logString = "Cartoland Bot is now online.";
		botChannel.sendMessage(logString).queue();
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
		jda.shutdownNow();
	}

	//https://stackoverflow.com/questions/65984126
	private void ohBoy3AM()
	{
		LocalTime now = LocalTime.now();
		LocalTime threeAM = now.withHour(3).withMinute(0).withSecond(0);

		if (now.compareTo(threeAM) > 0)
			threeAM = threeAM.plusHours(24);

		long secondsUntil3AM = Duration.between(now, threeAM).getSeconds();

		threeAMService = Executors.newScheduledThreadPool(1);
		threeAMHandle = threeAMService.scheduleAtFixedRate(
				() -> undergroundChannel.sendMessage("https://imgur.com/EGO35hf").queue(),
				secondsUntil3AM, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
	}
}