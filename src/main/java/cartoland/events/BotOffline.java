package cartoland.events;

import cartoland.utilities.FileHandle;
import cartoland.utilities.IDAndEntities;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * {@code BotOffline} is a listener that triggers when this bot went offline normally. It won't trigger if this bot was
 * shutdown by accident, such as killed by ^C. This class was registered in {@link cartoland.Cartoland#main}, with
 * the build of JDA. This class help synchronize JSONObjects and their files.
 *
 * @since 1.0
 * @author Alex Cai
 */
public class BotOffline extends ListenerAdapter
{
	/**
	 * The method that inherited from {@link ListenerAdapter}. When the bot go offline normally, it will log
	 * "Cartoland Bot is now offline." to terminal & log file, and write JSONObject into users.json &
	 * command_blocks.json.
	 *
	 * @param event Information about the shutdown.
	 * @since 1.0
	 * @author Alex Cai
	 */
	@Override
	public void onShutdown(@NotNull ShutdownEvent event)
	{
		//https://stackoverflow.com/questions/34202701
		IDAndEntities.threeAMHandle.cancel(true);
		IDAndEntities.threeAMService.shutdown();

		String logString = "Cartoland Bot is now offline.";
		System.out.println(logString);
		FileHandle.log(logString);
		FileHandle.closeLog();
		JsonHandle.synchronizeFiles();
	}
}