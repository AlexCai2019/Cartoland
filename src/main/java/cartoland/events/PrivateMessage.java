package cartoland.events;

import cartoland.Cartoland;
import cartoland.utility.FileHandle;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class PrivateMessage extends ListenerAdapter
{
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        if (event.isFromType(ChannelType.PRIVATE))
        {
            Message message = event.getMessage();
            String rawMessage = message.getContentRaw();
            String attachments = "\n" + message.getAttachments().stream().map(Message.Attachment::getUrl).collect(Collectors.joining("\n"));
            Cartoland.undergroundChannel.sendMessage(rawMessage + attachments).queue(); //私訊轉到地下聊天室

            User author = event.getAuthor();
            FileHandle.logIntoFile(author.getName() + "(" + author.getId() + ") typed \"" + rawMessage + attachments + "\" in direct message.");
        }
    }
}
