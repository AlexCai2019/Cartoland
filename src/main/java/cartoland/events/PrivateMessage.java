package cartoland.events;

import cartoland.Cartoland;
import cartoland.utility.FileHandle;
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
            String message = event.getMessage().getContentRaw();
            channel.sendMessage(message).queue();
            System.out.println(event.getAuthor().getName() + "(" + event.getAuthor().getId() + ") typed \"" + message + "\" in direct message.");
            FileHandle.logIntoFile(event.getAuthor().getName() + "(" + event.getAuthor().getId() + ") typed \"" + message + "\" in direct message.");
        }
    }
}
