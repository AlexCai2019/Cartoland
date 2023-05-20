package cartoland.messages;

import cartoland.utilities.Algorithm;
import cartoland.utilities.IDAndEntities;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @since 2.0
 * @author Alex Cai
 */
public class BotCanTalkChannelMessage implements IMessage
{
	private final String[] replyACMention =
	{
		"父親，您找我有事嗎？",
		"向父親請安，父親您好嗎？",
		"爹，您好呀。",
		"聽候您差遣。"
	};
	private final String[] replyMegaMention =
	{
		"臣向陛下請安",
		"陛下萬福金安",
		"陛下今日可好？"
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

	private final MentionType[] botType = { MentionType.USER, MentionType.ROLE };

	private final Pattern meguminRegex = Pattern.compile("(?i).*megumin.*"); //containsIgnoreCase
	private final Pattern lolRegex = Pattern.compile("(?i).*lol.*"); //containsIgnoreCase

	private final Set<Long> canTalkCategories = new HashSet<>(3);

	public BotCanTalkChannelMessage()
	{
		canTalkCategories.add(IDAndEntities.GENERAL_CATEGORY_ID);
		canTalkCategories.add(IDAndEntities.PUBLIC_AREA_CATEGORY_ID);
		canTalkCategories.add(IDAndEntities.DANGEROUS_CATEGORY_ID);
	}

	@Override
	public boolean messageCondition(MessageReceivedEvent event)
	{
		if (!event.isFromGuild())
			return true; //是私訊
		Category category = event.getMessage().getCategory();
		//獲取類別失敗就不執行後面那個
		return category != null && canTalkCategories.contains(category.getIdLong()); //只在特定類別說話
	}

	@Override
	public void messageProcess(MessageReceivedEvent event)
	{
		Message message = event.getMessage(); //獲取訊息
		String rawMessage = message.getContentRaw(); //獲取訊息字串
		MessageChannel channel = message.getChannel();

		if (message.getMentions().isMentioned(IDAndEntities.botItself, botType)) //有人tag機器人
		{
			long userID = event.getAuthor().getIdLong();
			String replyString;

			//不要再想著用switch了 Java的switch不支援long
			if (userID == IDAndEntities.AC_ID)
				replyString = Algorithm.randomElement(replyACMention);
			else if (userID == IDAndEntities.MEGA_ID)
				replyString = Algorithm.randomElement(replyMegaMention);
			else
				replyString = Algorithm.randomElement(replyMention);

			message.reply(replyString).mentionRepliedUser(false).queue();
		}

		if (rawMessage.contains("惠惠") || meguminRegex.matcher(rawMessage).matches() || rawMessage.contains("めぐみん"))
			channel.sendMessage(Algorithm.randomElement(megumin)).queue();

		if (lolRegex.matcher(rawMessage).matches())
			channel.sendMessage("LOL").queue();

		if (rawMessage.contains("早安"))
			channel.sendMessage("早上好中國 現在我有Bing Chilling").queue();
		if (rawMessage.contains("午安"))
			channel.sendMessage("午安你好，歡迎來到" + IDAndEntities.cartolandServer.getName()).queue(); //午安長輩圖
		if (rawMessage.contains("晚安"))
			channel.sendMessage("那我也要睡啦").queue();
		if (rawMessage.contains("安安"))
			channel.sendMessage("安安你好幾歲住哪").queue();

		if (rawMessage.contains("聰明"))
			channel.sendMessage("https://tenor.com/view/galaxy-brain-meme-gif-25947987").queue();
		if (rawMessage.contains("賺爛"))
			channel.sendMessage("https://tenor.com/view/反正我很閒-賺爛了-gif-25311690").queue();
		if (rawMessage.contains("蘿莉") || rawMessage.contains("羅莉"))
			channel.sendMessage(Algorithm.randomElement(fbi)).queue();
	}
}