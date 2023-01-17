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
        String logString;
        if (channel != null)
        {
            logString = "Cartoland Bot is now online.";
            channel.sendMessage(logString).queue();
            System.out.println(logString);
            FileHandle.logIntoFile(logString);
        }
        else
        {
            logString = "Can't find Bot Channel.";
            System.err.println(logString);
            FileHandle.logIntoFile(logString);
        }
    }
}
