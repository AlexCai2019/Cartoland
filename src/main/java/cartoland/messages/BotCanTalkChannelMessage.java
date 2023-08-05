package cartoland.messages;

import cartoland.Cartoland;
import cartoland.utilities.Algorithm;
import cartoland.utilities.IDs;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * {@code BotCanTalkChannelMessage} is a listener that triggers when a user types anything in any channel that the
 * bot can talk. This class is in an array in {@link cartoland.events.MessageEvent}.
 *
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
		"聽候您差遣。",
		"父親，無論您需要什麼幫助，我都會全力以赴！", //由 brick-bk 新增
		"父親，您辛苦了，工作忙碌之餘，也請您別忘了多休息保護眼睛！"//Add by Champsing
	};
	private final String[] replyMegaMention =
	{
		"臣向陛下請安",
		"陛下萬福金安",
		"陛下今日可好？",
		"願陛下萬壽無疆，國泰民安，天下太平。", //由 brick-bk 新增
		"祝陛下萬歲，萬歲，萬萬歲！", //由 brick-bk 新增
		//Add by Champsing
		"微臣向皇上請安",
		"皇上龍體安康",
		"微臣參見陛下",
		"皇上威震四海、聲赫五洲",
	};
	private final String[] replyMention =
	{
		"你媽沒教你不要亂tag人嗎？",
		"勸你小心點，我認識這群群主。",
		"tag我都小雞雞。",
		"不要耍智障好不好。",
		"你看看，就是有像你這種臭俗辣。",
		"吃屎比較快。",
		"你是怎麼樣？",
		"在那叫什麼？",
		"https://imgur.com/n1rEBO9", //在那叫什麼
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
		"tag機器人是不好的行為，小朋友不要學。",
		"上帝把智慧撒向人間的時候你撐了把傘嗎？",
		"https://imgur.com/xxZVQvB", //你到別的地方去耍笨好不好
		"小米格們也都別忘了Pick Me!\n在GitHub 點個星星 按個 讚。",
		"如果大家喜歡這種機器人的話，別忘了點擊GitHub上面那個，大大～的星星！讓我知道。",
		"你再tag我啊，再tag啊，沒被禁言過是不是？", //由 brick-bk 新增，經 Alex Cai 大幅修改
		"豎子，不足與謀。"//死小孩，沒話跟你講。 Added by Champsing
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
		"https://imgur.com/GLElBwY", //電話在那裡
		"https://imgur.com/Aax1R2U", //我要走向電話
		"https://imgur.com/gPlBEMV" //我越來越接近電話了
	};

	private final Message.MentionType[] botType = { Message.MentionType.USER, Message.MentionType.ROLE };

	private final Pattern meguminRegex = Pattern.compile("(?i).*megumin.*"); //containsIgnoreCase

	private final Set<Long> canTalkCategories = new HashSet<>();

	private final Map<String, String[]> keywords = new HashMap<>(4);

	public BotCanTalkChannelMessage()
	{
		canTalkCategories.add(IDs.GENERAL_CATEGORY_ID);
		canTalkCategories.add(IDs.FORUM_CATEGORY_ID);
		canTalkCategories.add(IDs.VOICE_CATEGORY_ID);
		canTalkCategories.add(IDs.DANGEROUS_CATEGORY_ID);

		keywords.put("早安", new String[]{ "早上好中國 現在我有 Bing Chilling","早上好創聯" });
		keywords.put("午安", new String[]{ "午安你好，記得天下沒有白吃的午餐" }); //後面那句由 brick-bk 新增
		keywords.put("晚安", new String[]{ "那我也要睡啦","https://tenor.com/view/food-goodnight-gif-18740706","https://tenor.com/view/goodnight-gif-8996096" });
		keywords.put("安安", new String[]{ "安安你好幾歲住哪","安安各位大家好","https://static.wikia.nocookie.net/theamazingworldofgumball/images/1/10/Season_3_Anais.png/" });
	}

	@Override
	public boolean messageCondition(MessageReceivedEvent event)
	{
		ChannelType channelType = event.getChannelType();
		if (!channelType.isGuild()) //是私訊
			return true; //私訊可以說話

		Category category = channelType.isThread() ? //討論串無法被歸類在有類別的頻道 但是它的原始頻道算
				event.getChannel()
					.asThreadChannel()
					.getParentChannel()
					.asStandardGuildChannel()
					.getParentCategory()
				: event.getMessage().getCategory(); //嘗試從訊息獲取類別
		return category != null && canTalkCategories.contains(category.getIdLong()); //只在特定類別說話
	}

	@Override
	public void messageProcess(MessageReceivedEvent event)
	{
		Message message = event.getMessage(); //獲取訊息
		String rawMessage = message.getContentRaw(); //獲取訊息字串
		MessageChannel channel = message.getChannel();
		User author = event.getAuthor();

		if (message.getMentions().isMentioned(Cartoland.getJDA().getSelfUser(), botType)) //有人tag機器人
		{
			long userID = author.getIdLong();
			String replyString;

			//不要再想著用switch了 Java的switch不支援long
			if (userID == IDs.AC_ID) //是AC
				replyString = Algorithm.randomElement(replyACMention);
			else if (userID == IDs.MEGA_ID) //是米格
				replyString = Algorithm.randomElement(replyMegaMention);
			else //都不是
			{
				long channelID = channel.getIdLong();
				//如果頻道在機器人或地下 就正常地回傳replyMention 反之就說 蛤，我地盤在#bot專區｜bots啦
				replyString = (channelID == IDs.BOT_CHANNEL_ID || channelID == IDs.UNDERGROUND_CHANNEL_ID) ?
						Algorithm.randomElement(replyMention) : "蛤，我地盤在<#" + IDs.BOT_CHANNEL_ID + ">啦";
			}

			message.reply(replyString).mentionRepliedUser(false).queue();
		}

		int rawMessageLength = rawMessage.length(); //訊息的字數

		if (rawMessageLength <= 1) //只打一個字或是沒有字 (不過沒有字是怎麼送出的?)
			return; //沒有必要執行下面那些檢測

		if (rawMessageLength == 3) //用字串長度去最佳化 注意這其中若出現了長度不是3的字串 那這個條件就要修改
		{
			if (rawMessage.equalsIgnoreCase("lol"))
			{
				channel.sendMessage("LOL").queue();
				return; //在這之下的if們 全都不可能通過
			}

			if (rawMessage.equalsIgnoreCase("owo"))
			{
				channel.sendMessage("OwO").queue();
				return; //在這之下的if們 全都不可能通過
			}
		}

		if (rawMessageLength == 2) //用字串長度去最佳化 注意keywords的keys若出現2以外的長度 那這個條件就要修改
		{
			String[] sendStrings = keywords.get(rawMessage); //尋找完全相同的字串
			if (sendStrings != null) //如果找到了
			{
				channel.sendMessage(Algorithm.randomElement(sendStrings)).queue();
				return; //keywords內的字串 沒有一個包含了下面的內容 所以底下的可直接不執行
			}
		}

		if (rawMessage.contains("惠惠") || (rawMessageLength >= 7 && meguminRegex.matcher(rawMessage).matches()) || rawMessage.contains("めぐみん"))
			channel.sendMessage(Algorithm.randomElement(megumin)).queue();
		if (rawMessage.contains("鬼島交通"))//Added by Champsing
			channel.sendMessage("https://memeprod.sgp1.digitaloceanspaces.com/user-wtf/1651071890313.jpg").queue();
		if (rawMessage.contains("聰明"))
			channel.sendMessage("https://tenor.com/view/galaxy-brain-meme-gif-25947987").queue();
		if (rawMessage.contains("賺爛"))
			channel.sendMessage("https://tenor.com/view/反正我很閒-賺爛了-gif-25311690").queue();
		if (rawMessage.contains("蘿莉") || rawMessage.contains("羅莉"))
			channel.sendMessage(Algorithm.randomElement(fbi)).queue();
		if (rawMessage.contains("無情"))
			channel.sendMessage("太無情了" + author.getEffectiveName() + "，你真的太無情了！").queue();
		if (rawMessage.contains("閃現"))
			channel.sendMessage("這什麼到底什麼閃現齁齁齁齁齁").queue();
		if (rawMessage.contains("興奮"))
			channel.sendMessage("https://tenor.com/view/excited-gif-8604873").queue();
	}
}