package cartoland.events;

import cartoland.utilities.Algorithm;
import cartoland.utilities.CommandBlocksHandle;
import cartoland.utilities.IDAndEntities;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.regex.Pattern;

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
		"<:ping:1065915559918719006>",
		"你凡是有tag我被我記起來的對不對？我一定到現場打你，一定打你！",
		"哪裡來的小孩子，家教差成這樣。",
		"老子瘋狗的外號Maps群時期就有啦！",
		"沒被打過是不是？",
		"tag機器人是不好的行為，小朋友不要學"
	};
	private final String[] megumin =
	{
		"☆めぐみん大好き！☆",
		"☆めぐみんは最高だ！☆",
		"☆めぐみん俺の嫁！☆"
	};
	private final String[] fbi =
	{
		"https://tenor.com/view/f-bi-raid-swat-gif-11500735",
		"https://tenor.com/view/fbi-calling-tom-gif-12699976",
		"https://tenor.com/view/fbi-swat-busted-police-open-up-gif-16928811",
		"https://tenor.com/view/fbi-swat-police-entry-attack-gif-16037524",
		"https://imgur.com/gPlBEMV"
	};

	private final Message.MentionType[] botType = { Message.MentionType.USER, Message.MentionType.ROLE };

	private final Pattern meguminRegex = Pattern.compile("(?i).*megumin.*");
	private final Pattern lolRegex = Pattern.compile("(?i).*lol*");

	private final Emoji learned = Emoji.fromCustom("learned", 892406442622083143L, false);
	private final Emoji wow = Emoji.fromCustom("wow", 893499112228519996L, false);

	private final Random random = new Random();

	/**
	 * The method that inherited from {@link ListenerAdapter}, triggers when receive a message from any
	 * channel that the bot has permission to read, but only response when the channel is a text channel and
	 * the user isn't a bot.
	 *
	 * @param event Information about the message and its channel and author.
	 * @throws InsufficientPermissionException When the bot doesn't have permission to react.
	 * @since 1.0
	 * @author Alex Cai
	 */
	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event)
	{
		if (!event.isFromGuild()) //不是伺服器頻道
			return;
		User author = event.getAuthor();
		if (author.isBot() || author.isSystem()) //傳訊息的是機器人或系統
			return; //不用執行

		long userID = author.getIdLong();
		Message message = event.getMessage(); //獲取訊息
		String rawMessage = message.getContentRaw(); //獲取訊息字串
		Category category = message.getCategory();
		if (category == null) //獲取類別失敗
			return; //不用執行
		long categoryID = category.getIdLong();
		MessageChannel channel = message.getChannel();

		if (Algorithm.chance(20) && rawMessage.contains("learned")) //20%
			message.addReaction(learned).queue();
		if (Algorithm.chance(20) && rawMessage.contains("wow")) //20%
			message.addReaction(wow).queue();

		//在一般、技術討論區或公眾區域類別 且不是在機器人專區
		if (channel.getIdLong() != IDAndEntities.BOT_CHANNEL_ID && IDAndEntities.commandBlockCategories.contains(categoryID))
			CommandBlocksHandle.add(userID, rawMessage.length() + 1); //說話加等級 +1當作加上\0


		//以下是有關機器人說話的部分
		if (!IDAndEntities.canTalkCategories.contains(categoryID))
			return; //只在特定類別說話

		if (message.getMentions().isMentioned(IDAndEntities.botItself, botType)) //有人tag機器人
			message.reply(userID == IDAndEntities.AC_ID ? randomString(replyACMention) : randomString(replyMention))
					.mentionRepliedUser(false).queue();

		if (rawMessage.contains("惠惠") || meguminRegex.matcher(rawMessage).matches() || rawMessage.contains("めぐみん"))
			channel.sendMessage(randomString(megumin)).queue();

		if (lolRegex.matcher(rawMessage).matches())
			channel.sendMessage("LOL").queue();

		if (rawMessage.contains("早安"))
			channel.sendMessage("早上好中國 現在我有Bing Chilling").queue();
		if (rawMessage.contains("午安"))
			channel.sendMessage("http://chunting.me/wp-content/uploads/2018/09/IMG_5878.jpg").queue(); //午安長輩圖
		if (rawMessage.contains("晚安"))
			channel.sendMessage("那我也要睡啦").queue();
		if (rawMessage.contains("安安"))
			channel.sendMessage("安安你好幾歲住哪").queue();

		if (rawMessage.contains("聰明"))
			channel.sendMessage("https://tenor.com/view/galaxy-brain-meme-gif-25947987").queue();
		if (rawMessage.contains("賺爛"))
			channel.sendMessage("https://tenor.com/view/反正我很閒-賺爛了-gif-25311690").queue();
		if (rawMessage.contains("蘿莉") || rawMessage.contains("羅莉"))
			channel.sendMessage(randomString(fbi)).queue();
	}

	private String randomString(String[] strings)
	{
		return strings[random.nextInt(strings.length)];
	}
}