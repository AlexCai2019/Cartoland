package cartoland.events;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ChannelMessage extends ListenerAdapter
{
    private final Random random = new Random();
    private final String[] replyMention =
    {
        "你媽沒教你不要亂tag人嗎？",
        "勸你小心點，我認識這群群主。",
        "tag我都小雞雞。",
        "不要耍智障好不好。",
        "你看看，就是有像你這種臭俗辣。"
    };

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        super.onMessageReceived(event);

        Member member = event.getMember();
        if (member == null || member.getUser().isBot()) //獲取成員失敗 或 傳訊息的是機器人
            return; //不用執行
        String message = event.getMessage().getContentRaw(); //獲取訊息
        if (message.equalsIgnoreCase("megumin") || message.contains("惠惠") || message.contains("めぐみん"))
            event.getChannel().sendMessage("☆めぐみん大好き！☆").queue();
        if (message.contains("<@919939882196033596>")) //有人tag機器人
            event.getMessage().reply(replyMention[random.nextInt(replyMention.length)]).mentionRepliedUser(false).queue();
    }
}