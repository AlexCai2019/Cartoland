package cartoland.messages;

import cartoland.Cartoland;
import cartoland.utilities.Algorithm;
import cartoland.utilities.IDs;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
		"父親，無論您需要什麼幫助，我都會全力以赴！" //由 brick-bk 新增
	};
	private final String[] replyMegaMention =
	{
		"臣向陛下請安",
		"陛下萬福金安",
		"陛下今日可好？",
		"願陛下萬壽無疆，國泰民安，天下太平。", //由 brick-bk 新增
		"祝陛下萬歲，萬歲，萬萬歲！" //由 brick-bk 新增
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
		"請問您有什麼事嗎？是不是太閒？", //由 brick-bk 新增
		"你再tag我啊，再tag啊，沒被禁言過是不是？", //由 brick-bk 新增，經 Alex Cai 大幅修改
		"你是不是tag我？快承認！要是說謊，鼻子就會變長的！", //由 brick-bk 新增
		"聽說有人tag我？你知道是誰嗎？", //由 brick-bk 新增
		"為什麼要召喚我，打斷我蓋地圖？" //由 brick-bk 新增
	};

	private final Message.MentionType[] botType = { Message.MentionType.USER, Message.MentionType.ROLE };

	private final Set<Long> canTalkCategories = new HashSet<>(4);

	private final Map<String, String> keywords = new HashMap<>(6);

	private final Map<Character, ContainsCheck> containsChecksMap = new HashMap<>(10);

	public BotCanTalkChannelMessage()
	{
		canTalkCategories.add(IDs.GENERAL_CATEGORY_ID);
		canTalkCategories.add(IDs.FORUM_CATEGORY_ID);
		canTalkCategories.add(IDs.VOICE_CATEGORY_ID);
		canTalkCategories.add(IDs.DANGEROUS_CATEGORY_ID);

		keywords.put("早安", "早上好中國 現在我有 Bing Chilling");
		keywords.put("午安", "午安你好，記得天下沒有白吃的午餐"); //後面那句由 brick-bk 新增
		keywords.put("晚安", "那我也要睡啦");
		keywords.put("安安", "安安你好幾歲住哪");
		keywords.put("轉生", "您好，您的目標是lv7轉生！"); //由 brick-bk 新增
		keywords.put("美麗星期天", "https://i.imgur.com/0nK3tcV.jpg"); //由 brick-bk 新增

		//key為要被檢查包含的首字
		String[] megumin =
		{
			"☆めぐみん大好き！☆",
			"☆めぐみんは最高だ！☆",
			"☆めぐみん俺の嫁！☆"
		};
		containsChecksMap.put('惠', new ContainsCheck("惠惠", megumin));
		ContainsCheck meguminContains = new MeguminContains("megumin", megumin);
		containsChecksMap.put('M', meguminContains);
		containsChecksMap.put('m', meguminContains);
		containsChecksMap.put('め', new ContainsCheck("めぐみん", megumin));

		containsChecksMap.put('聰', new ContainsCheck("聰明", "https://tenor.com/view/galaxy-brain-meme-gif-25947987"));
		containsChecksMap.put('賺', new ContainsCheck("賺爛", "https://tenor.com/view/反正我很閒-賺爛了-gif-25311690"));

		String[] fbi =
		{
			"https://tenor.com/view/f-bi-raid-swat-gif-11500735",
			"https://tenor.com/view/fbi-calling-tom-gif-12699976",
			"https://tenor.com/view/fbi-swat-busted-police-open-up-gif-16928811",
			"https://tenor.com/view/fbi-swat-police-entry-attack-gif-16037524",
			"https://imgur.com/GLElBwY", //電話在那裡
			"https://imgur.com/Aax1R2U", //我要走向電話
			"https://imgur.com/gPlBEMV" //我越來越接近電話了
		};
		containsChecksMap.put('蘿', new ContainsCheck("蘿莉", fbi));
		containsChecksMap.put('羅', new ContainsCheck("羅莉", fbi));

		containsChecksMap.put('閃', new ContainsCheck("閃現", "這什麼到底什麼閃現齁齁齁齁齁"));
		containsChecksMap.put('興', new ContainsCheck("興奮", "https://tenor.com/view/excited-gif-8604873"));
	}

	@Override
	public boolean messageCondition(MessageReceivedEvent event)
	{
		if (!event.isFromGuild()) //是私訊
			return true; //私訊可以說話
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

		if (rawMessage.length() <= 1) //只打一個字或是沒有字
			return; //沒有必要執行下面那些檢測

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

		String sendString = keywords.get(rawMessage); //尋找完全相同的字串
		if (sendString != null) //如果找到了
		{
			channel.sendMessage(sendString).queue();
			return; //keywords內的字串 沒有一個包含了下面的內容 所以底下的可直接不執行
		}

		if (rawMessage.contains("無情")) //因為使用了區域變數 所以無法被合併進containsChecksMap內
			channel.sendMessage("太無情了" + author.getEffectiveName() + "，你真的太無情了！").queue();

		ContainsCheck containsCheck; //重複使用這個變數
		int rawMessageLength = rawMessage.length(); //開始走訪迴圈
		for (int i = 0; i < rawMessageLength; i++)
		{
			char c = rawMessage.charAt(i);
			containsCheck = containsChecksMap.get(c); //尋找是否有目標首字等於這個字
			if (containsCheck == null) //如果不曾包含這個字
				continue; //下面一位
			if (containsCheck.metBefore) //已經遇過了 Set的contains是O(1)
				continue; //下一輪迴圈
			if (!containsCheck.contains(rawMessage, i)) //經查不包含
				continue; //下一個目標
			channel.sendMessage(containsCheck.sendString()).queue();
			containsCheck.metBefore = true; //已經遇過了
		}
	}

	private static class ContainsCheck
	{
		protected final String compareString;
		private final String[] sendStrings;
		private boolean metBefore = false;

		private ContainsCheck(String compareString, String... sendStrings)
		{
			this.compareString = compareString;
			this.sendStrings = sendStrings;
		}

		protected boolean contains(String fullString, int startIndex)
		{
			int compareLength = compareString.length();
			//假設要比對的是fullString[3] ~ fullString[5]
			//compareLength是3 而fullString.length()是6的情況下
			//3(start index) + 3(compare length) = 6
			if (startIndex + compareLength > fullString.length())
				return false;
			for (int index = 0; index < compareLength; index++)
				if (fullString.charAt(startIndex + index) != compareString.charAt(index))
					return false;
			return true;
		}

		private String sendString()
		{
			return Algorithm.randomElement(sendStrings);
		}
	}

	private static class MeguminContains extends ContainsCheck
	{
		private MeguminContains(String compareString, String... sendStrings)
		{
			super(compareString, sendStrings);
		}

		@Override
		protected boolean contains(String fullString, int startIndex)
		{
			int compareLength = compareString.length();
			//假設要比對的是fullString[3] ~ fullString[5]
			//compareLength是3 而fullString.length()是6的情況下
			//3(start index) + 3(compare length) = 6
			if (startIndex + compareLength > fullString.length())
				return false;
			return fullString.substring(startIndex, startIndex + compareLength).equalsIgnoreCase(compareString);
		}
	}
}