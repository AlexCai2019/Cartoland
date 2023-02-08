package cartoland.events;

import cartoland.utilities.FileHandle;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * @since 1.0
 * @author Alex Cai
 */
public class BotOffline extends ListenerAdapter
{
	@Override
	public void onShutdown(@NotNull ShutdownEvent event)
	{
		String logString = "Cartoland Bot is now offline.";
		System.out.println(logString);
		FileHandle.log(logString);
		FileHandle.synchronizeFile("users.json");
		FileHandle.synchronizeFile("command_blocks.json");
	}
}