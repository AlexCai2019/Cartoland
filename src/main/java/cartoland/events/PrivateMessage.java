package cartoland.events;

import cartoland.Cartoland;
import cartoland.utility.FileHandle;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class PrivateMessage extends ListenerAdapter
{
    private TextChannel channel;

    @Override
    public void onReady(@NotNull ReadyEvent event)
    {
        channel = event.getJDA().getChannelById(TextChannel.class, Cartoland.BOT_CHANNEL_ID);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        super.onMessageReceived(event);
        if (event.isFromType(ChannelType.PRIVATE))
        {
            String rawMessage = event.getMessage().getContentRaw();
            channel.sendMessage(rawMessage).queue();

            User author = event.getAuthor();
            String logString = author.getName() + "(" + author.getId() + ") typed \"" + rawMessage + "\" in direct message.";
            System.out.println(logString);
            FileHandle.logIntoFile(logString);
        }
    }
}
