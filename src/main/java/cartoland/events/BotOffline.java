package cartoland.events;

import cartoland.utility.FileHandle;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class BotOffline extends ListenerAdapter
{
    @Override
    public void onShutdown(@NotNull ShutdownEvent event)
    {
        super.onShutdown(event);
        System.out.println("Cartoland Bot is now offline.");
        FileHandle.logIntoFile("Cartoland Bot is now offline.");
    }
}
