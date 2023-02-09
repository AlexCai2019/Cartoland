package cartoland.events;

import cartoland.utilities.IDAndEntities;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * {@code ChannelMessage} is a listener that triggers when a user types anything in any channel that the bot can
 * access. This class was registered in {@link cartoland.Cartoland#main}, with the build of JDA.
 *
 * @since 1.0
 * @author Alex Cai
 */
public class ChannelMessage extends ListenerAdapter
{
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
		"再吵，就把你丟到亞馬遜上面賣。",
		"你除了tag機器人外沒別的事情好做嗎？",
		"有病要去看醫生。",
		"你凡是有tag我被我記起來的對不對？我一定到現場打你，一定打你！",
		"老子瘋狗的外號Maps群時期就有啦！",
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
			Message message = event.getMessage();
			String rawMessage = message.getContentRaw(); //獲取訊息
			long userID = member.getIdLong();
			Category category = message.getCategory();
			if (category == null) //獲取類別失敗
				return; //不用執行
			long categoryID = category.getIdLong();
			TextChannel channel = (TextChannel) message.getChannel();
			long channelID = channel.getIdLong();

			if (rawMessage.contains("megumin") || rawMessage.contains("Megumin") || rawMessage.contains("惠惠") || rawMessage.contains("めぐみん"))
				channel.sendMessage("☆めぐみん大好き！☆").queue();

			if (message.getMentions().isMentioned(IDAndEntities.botItself, botType)) //有人tag機器人
				message.reply(userID == IDAndEntities.AC_ID ? replyACMention[IDAndEntities.random.nextInt(replyACMention.length)] : replyMention[IDAndEntities.random.nextInt(replyMention.length)])
						.mentionRepliedUser(false).queue();

			if (rawMessage.contains("早安"))
				channel.sendMessage("早上好中國 現在我有Bing Chilling").queue();
			if (rawMessage.contains("晚安"))
				channel.sendMessage("那我也要睡啦").queue();

			if ((categoryID == IDAndEntities.GENERAL_CATEGORY_ID || categoryID == IDAndEntities.TECH_TALK_CATEGORY_ID) && channelID != IDAndEntities.BOT_CHANNEL_ID) //在一般或技術討論區類別 且不是在機器人專區
				JsonHandle.addCommandBlocks(userID, rawMessage.length()); //說話加等級
		}
	}
}