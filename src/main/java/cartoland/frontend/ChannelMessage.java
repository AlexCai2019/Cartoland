package cartoland.frontend;

import cartoland.backend.MessageHandle;
import cartoland.backend.AdminHandle;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ChannelMessage extends ListenerAdapter
{
    private final MessageHandle messageHandle = new MessageHandle(); //處理核心
    private final AdminHandle adminHandle = new AdminHandle(); //控制核心

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        super.onMessageReceived(event);

        Member member = event.getMember();
        if (member == null || member.getUser().isBot()) //獲取成員失敗 或 傳訊息的是機器人
            return; //不用執行
        MessageChannel channel = event.getChannel(); //獲取頻道
        String message = event.getMessage().getContentRaw().toLowerCase(); //獲取訊息
        if (message.contains("megumin") || message.contains("惠惠") || message.contains("めぐみん"))
            channel.sendMessage("☆めぐみん大好き！☆").queue();

        String[] messages = message.split(" ");
        String ret = messageHandle.commandProcess(member.getId(), messages); //輸入指令
        if (ret != null) //是輸入指令
            channel.sendMessage(ret).queue();

        if (member.getIdLong() == 355953951469731842L) //是我自己
        {
            ret = adminHandle.commandProcess(Long.toString(355953951469731842L), messages);
            if (ret != null)
                channel.sendMessage(ret).queue();
        }
    }
}