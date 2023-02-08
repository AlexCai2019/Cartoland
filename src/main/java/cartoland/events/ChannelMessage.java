package cartoland.events;

import cartoland.utilities.IDAndEntities;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * {@code ChannelMessage} is a listener that triggers when a user types anything in any channel that the bot can
 * access. This class was registered in {@link cartoland.Cartoland#main}, with the build of JDA.
 *
 * @since 1.0
 * @author Alex Cai
 */
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
	private final MentionType[] botType = { MentionType.USER,MentionType.ROLE };

	/**
	 * When receive a message from any channel that the bot has permission to read.
	 * @param event Information about the message and its channel and author.
	 */
	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event)
	{
		if (event.isFromType(ChannelType.TEXT))
		{
			Member member = event.getMember();
			if (member == null || member.getUser().isBot()) //獲取成員失敗 或 傳訊息的是機器人
				return; //不用執行
			long userID = member.getIdLong();
			TextChannel channel = (TextChannel) event.getChannel();
			Message message = event.getMessage();
			String rawMessage = message.getContentRaw(); //獲取訊息
			if (rawMessage.contains("megumin") || rawMessage.contains("Megumin") || rawMessage.contains("惠惠") || rawMessage.contains("めぐみん"))
				channel.sendMessage("☆めぐみん大好き！☆").queue();
			if (message.getMentions().isMentioned(IDAndEntities.botItself, botType)) //有人tag機器人
				message.reply(userID == IDAndEntities.AC_ID ? replyACMention[random.nextInt(replyACMention.length)] : replyMention[random.nextInt(replyMention.length)])
						.mentionRepliedUser(false).queue();
			if (rawMessage.contains("早安"))
				channel.sendMessage("早上好中國 現在我有Bing Chilling").queue();
			if (rawMessage.contains("晚安"))
				channel.sendMessage("那我也要睡啦").queue();

			if (channel.getIdLong() != IDAndEntities.UNDERGROUND_CHANNEL_ID) //不是在地下
				JsonHandle.addCommandBlocks(userID, rawMessage.length()); //說話加等級
		}
	}
}