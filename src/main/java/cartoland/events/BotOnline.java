package cartoland.events;

import cartoland.Cartoland;
import cartoland.utility.FileHandle;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class BotOnline extends ListenerAdapter
{
    @Override
    public void onReady(@NotNull ReadyEvent event)
    {
        JDA jda = event.getJDA();
        Cartoland.botChannel = jda.getChannelById(TextChannel.class, Cartoland.BOT_CHANNEL_ID); //創聯的機器人頻道
        String logString;
        if (Cartoland.botChannel != null)
        {
            logString = "Cartoland Bot is now online.";
            Cartoland.botChannel.sendMessage(logString).queue();
        }
        else
        {
            logString = "Can't find Bot Channel.";
            System.err.println(logString);
        }
        FileHandle.logIntoFile(logString);

        Cartoland.undergroundChannel = jda.getChannelById(TextChannel.class, Cartoland.UNDERGROUND_CHANNEL_ID); //地下聊天室
        if (Cartoland.undergroundChannel == null)
        {
            logString = "Can't find Underground Channel.";
            System.err.println(logString);
            FileHandle.logIntoFile(logString);
            System.exit(-1);
        }
    }
}
