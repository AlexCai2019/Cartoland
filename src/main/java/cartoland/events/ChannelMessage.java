package cartoland.events;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ChannelMessage extends ListenerAdapter
{
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        super.onMessageReceived(event);

        Member member = event.getMember();
        if (member == null || member.getUser().isBot()) //獲取成員失敗 或 傳訊息的是機器人
            return; //不用執行
        String message = event.getMessage().getContentRaw(); //獲取訊息
        if (message.equalsIgnoreCase("megumin") || message.equals("惠惠") || message.equals("めぐみん"))
            event.getChannel().sendMessage("☆めぐみん大好き！☆").queue();
    }
}