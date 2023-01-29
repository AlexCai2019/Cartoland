package cartoland.events;

import cartoland.utilities.IDAndEntities;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ChannelMessage extends ListenerAdapter
{
    private final Random random = new Random();
    private final String[] replyACMention =
    {
        "父親，您找我有事嗎？",
        "向父親請安，父親您好嗎？",
        "爹，您好呀。",
        "爸爸，我今天也有認真工作。",
        "爸爸，我表現得好嗎？",
        "聽候您差遣。"
    };
    private final String[] replyMention =
    {
        "你媽沒教你不要亂tag人嗎？",
        "勸你小心點，我認識這群群主。",
        "tag我都小雞雞。",
        "不要耍智障好不好。",
        "你看看，就是有像你這種臭俗辣。",
        "吃屎比較快。",
        "妳是怎麼樣？",
        "在那叫什麼？",
        "我知道我很帥，不用一直tag我",
        "tag我該女裝負責吧。",
        "沒梗的人才會整天tag機器人。",
        "你只會tag機器人嗎？",
        "再吵，就把你放到亞馬遜上面賣。",
        "你除了tag機器人沒別的事情好做嗎？",
        "有病要去看醫生。",
        "<:ping:1065915559918719006>"
    };

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        Member member = event.getMember();
        if (member == null || member.getUser().isBot()) //獲取成員失敗 或 傳訊息的是機器人
            return; //不用執行
        Message message = event.getMessage();
        String rawMessage = message.getContentRaw(); //獲取訊息
        if (rawMessage.contains("megumin") || rawMessage.contains("Megumin") || rawMessage.contains("惠惠") || rawMessage.contains("めぐみん"))
            event.getChannel().sendMessage("☆めぐみん大好き！☆").queue();
        if (message.getMentions().isMentioned(IDAndEntities.botItself, MentionType.USER)) //有人tag機器人
            message.reply(member.getIdLong() == IDAndEntities.AC_ID ? replyACMention[random.nextInt(replyACMention.length)] : replyMention[random.nextInt(replyMention.length)])
                    .mentionRepliedUser(false).queue();
    }
}