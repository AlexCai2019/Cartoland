package cartoland.messages;

import cartoland.utilities.Algorithm;
import cartoland.utilities.IDs;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

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
		"皇上威震四海、聲赫五洲"
	};
	private final String[] replyMention =
	{
		"你媽沒教你不要亂tag人嗎？",
		"你媽知道你都在Discord亂tag人嗎？",
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
		"<:ping:" + IDs.PING_EMOJI_ID + '>',
		"你凡是有tag我被我記起來的對不對？我一定到現場打你，一定打你！",
		"沒關係你們再繼續亂tag沒關係，逮到一個、揍一個，一定、一定把你鼻樑打歪。",
		"哪裡來的小孩子，家教差成這樣。",
		"老子瘋狗的外號Maps群時期就有啦！",
		"沒被打過是不是？",
		"tag機器人是不好的行為，小朋友不要學。",
		"上帝把智慧撒向人間的時候你撐了把傘嗎？",
		"https://imgur.com/lU4M8J2", //你到別的地方去耍笨好不好
		"你再tag我啊，再tag啊，沒被禁言過是不是？", //由 brick-bk 新增，經 Alex Cai 大幅修改
		"豎子，不足與謀。"//死小孩，沒話跟你講。 Added by Champsing
	};
	private final String[] replySilentMention =
	{
		"你以為加了`@silent`我就不知道了嗎？",
		"@silent <:ping:" + IDs.PING_EMOJI_ID + '>',
		"做壞事是不用打廣告的，因其自當傳千里。"
	};

	private final Set<Long> canTalkChannels = Set.of(IDs.ZH_CHAT_CHANNEL_ID, IDs.EN_CHAT_CHANNEL_ID, IDs.VOICE_TEXT_CHANNEL_ID, IDs.UNDERGROUND_CHANNEL_ID);

	private final Map<String, String[]> keywords =
		Map.of("早安", new String[]{ "早上好中國 現在我有 Bing Chilling","早上好創聯 現在我有 Bing Chilling","道聲「早安」\n卻又讓我做了夢\n自然而然的生活方式不是很好嗎？" },
			"午安", new String[]{ "午安你好，記得天下沒有白吃的午餐" }, //後面那句由 brick-bk 新增
			"晚安", new String[]{ "那我也要睡啦","https://tenor.com/view/food-goodnight-gif-18740706","https://tenor.com/view/kfc-fried-chicken-kentucky-fried-chicken-fast-food-gif-26996460","https://tenor.com/view/burger-butter-cooking-gif-3340446" },
			"安安", new String[]{ "安安你好幾歲住哪","安安各位大家好","https://static.wikia.nocookie.net/theamazingworldofgumball/images/1/10/Season_3_Anais.png/" });

	@Override
	public boolean messageCondition(MessageReceivedEvent event)
	{
		if (!event.isFromGuild()) //是私訊
			return true; //私訊可以說話

		if (canTalkChannels.contains(event.getChannel().getIdLong())) //訊息的頻道
			return true; //在特定的頻道可以說話

		Category category = event.getMessage().getCategory(); //嘗試從訊息獲取類別
		return category != null && category.getIdLong() == IDs.OFF_TOPIC_CATEGORY_ID; //雜談類別可以說話
	}

	@Override
	public void messageProcess(MessageReceivedEvent event)
	{
		Message message = event.getMessage(); //獲取訊息
		String rawMessage = message.getContentRaw(); //獲取訊息字串
		MessageChannel channel = message.getChannel();
		User author = event.getAuthor();

		if (message.getMentions().isMentioned(event.getJDA().getSelfUser(), Message.MentionType.USER, Message.MentionType.ROLE)) //有人tag機器人
		{
			long userID = author.getIdLong();

			//不要再想著用switch了 Java的switch不支援long
			if (userID == IDs.AC_ID) //是AC
				message.reply(Algorithm.randomElement(replyACMention)).mentionRepliedUser(false).queue();
			else if (userID == IDs.MEGA_ID) //是米格
				message.reply(Algorithm.randomElement(replyMegaMention)).mentionRepliedUser(false).queue();
			else //是其他人
			{
				long channelID = channel.getIdLong();
				//如果頻道在機器人或地下 就正常地回傳replyMention 如果是@silent訊息就回覆replySilentMention
				if (channelID == IDs.BOT_CHANNEL_ID || channelID == IDs.UNDERGROUND_CHANNEL_ID)
					message.reply(Algorithm.randomElement(message.isSuppressedNotifications() ? replySilentMention : replyMention))
							.mentionRepliedUser(false)
							.queue();
				else //在其他地方ping就固定加一個ping的emoji
					message.addReaction(Emoji.fromCustom("ping", IDs.PING_EMOJI_ID, false)).queue();
			}
		}

		switch (rawMessage.length()) //訊息的字數
		{
			case 2: //用字串長度去最佳化 注意keywords的keys若出現2以外的長度 那這個條件就要修改
				String[] sendStrings = keywords.get(rawMessage); //尋找完全相同的字串
				if (sendStrings != null) //如果找到了
				{
					channel.sendMessage(Algorithm.randomElement(sendStrings)).queue();
					return; //keywords內的字串 沒有一個包含了下面的內容 所以底下的可直接不執行
				}
				break;

			case 3: //用字串長度去最佳化
				if ("lol".equalsIgnoreCase(rawMessage))
					channel.sendMessage("LOL").queue();
				else if ("omg".equalsIgnoreCase(rawMessage))
					channel.sendMessage("OMG").queue();
				else if ("owo".equalsIgnoreCase(rawMessage))
					channel.sendMessage("OwO").queue();
				else if ("ouo".equalsIgnoreCase(rawMessage))
					channel.sendMessage("OuO").queue();
				break;

			case 4: //用字串長度去最佳化
				if ("oeur".equalsIgnoreCase(rawMessage) || "芋圓柚子".equals(rawMessage))
					channel.sendMessage(
					"""
						阿神的超神奇馬桶可以激發他的無限靈感
						阿謙和阿神的關係到現在還是非常的不明
						可愛的小夏狂搶麥最後生氣的都是巧克力
						誰說阿晋拿下眼鏡之後傲嬌屬性就會轉移
						阿晋泡麵加上狗子便當
						再加一顆梅子就可以吃
						全全的傳說傳了好幾年
						半半要不要再進化一次呢
						梅子空姐的廣播跳下飛機後再聽一次
						丹丹的最強絕技就是永遠保持於狀況外
						""").queue();
				else if ("鬼島交通".equals(rawMessage)) //Added by Champsing
					channel.sendMessage("https://memeprod.sgp1.digitaloceanspaces.com/user-wtf/1651071890313.jpg").queue();
				break;
		}
	}
}