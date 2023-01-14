package cartoland.events;

import cartoland.Cartoland;
import cartoland.utility.FileHandle;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class BotOnline extends ListenerAdapter
{
    @Override
    public void onReady(@NotNull ReadyEvent event)
    {
        super.onReady(event);
        TextChannel channel = event.getJDA().getChannelById(TextChannel.class, Cartoland.BOT_CHANNEL_ID); //創聯的機器人頻道
        if (channel != null)
        {
            channel.sendMessage("Cartoland Bot is now online").queue();
            System.out.println("Cartoland Bot is now online");
            FileHandle.logIntoFile("Cartoland Bot is now online");
        }
        else
        {
            System.err.println("Can't find Bot Channel");
            FileHandle.logIntoFile("Can't find Bot Channel");
        }
    }
}
