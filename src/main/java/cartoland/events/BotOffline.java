package cartoland.events;

import cartoland.utilities.JsonHandle;
import cartoland.utilities.FileHandle;
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
	 * The method that inherited from {@link ListenerAdapter}, log "Cartoland Bot is now offline." to terminal and log file and write JSONObject into users.json and
	 * command_blocks.json.
	 *
	 * @param event Information about the shutdown.
	 */
	@Override
	public void onShutdown(@NotNull ShutdownEvent event)
	{
		String logString = "Cartoland Bot is now offline.";
		System.out.println(logString);
		FileHandle.log(logString);
		FileHandle.synchronizeFile(JsonHandle.USERS_JSON);
		FileHandle.synchronizeFile(JsonHandle.COMMAND_BLOCKS_JSON);
	}
}