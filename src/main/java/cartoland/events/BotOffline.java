package cartoland.events;

import cartoland.Cartoland;
import cartoland.utility.FileHandle;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class BotOffline extends ListenerAdapter
{
    @Override
    public void onShutdown(@NotNull ShutdownEvent event)
    {
        super.onShutdown(event);
        TextChannel channel = event.getJDA().getChannelById(TextChannel.class, Cartoland.BOT_CHANNEL_ID);
        if (channel != null)
            channel.sendMessage("Cartoland bot is now offline").queue();
        System.out.println("Cartoland Bot is now offline");
        FileHandle.logIntoFile("Cartoland Bot is now offline");
        System.exit(0);
    }
}
