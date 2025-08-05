package cartoland.methods;

import cartoland.Cartoland;
import cartoland.utilities.IDs;
import cartoland.utilities.RegularExpressions;
import cartoland.utilities.ReturnResult;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.requests.ErrorResponse;

public interface IAnalyzeCTLCLink
{
	int SUB_STRING_START = ("https://discord.com/channels/" + IDs.CARTOLAND_SERVER_ID + "/").length();
	String INVALID_LINK = "invalid_link";
	String NO_GUILD = "no_guild";
	String NO_CHANNEL = "no_channel";
	String NO_MESSAGE = "no_message";

	default void analyze(IReplyCallback event, String link)
	{
		//分析連結字串 獲得訊息
		//建議不要override這個方法 你會搞砸它然後害綠村民死翹翹

		if (!RegularExpressions.CARTOLAND_MESSAGE_LINK_REGEX.matcher(link).matches()) //必須是有效的創聯訊息連結
		{
			afterAnalyze(event, link, ReturnResult.fail(INVALID_LINK)); //連結不通過regex
			return;
		}

		//從創聯中取得頻道
		Guild cartoland = Cartoland.getJDA().getGuildById(IDs.CARTOLAND_SERVER_ID);
		if (cartoland == null) //找不到創聯
		{
			afterAnalyze(event, link, ReturnResult.fail(NO_GUILD));
			return;
		}

		//獲取頻道和訊息的ID
		String[] numbersInLink = link.substring(SUB_STRING_START).split("/");
		//舉例 https://discord.com/channels/886936474723950603/886936474723950611/891666028986253322
		//numbersInLink[0] = "886936474723950611";
		//numbersInLink[1] = "891666028986253322";

		//獲取訊息內的頻道 注意ID是String 與慣例的long不同
		GuildMessageChannel linkChannel = cartoland.getChannelById(GuildMessageChannel.class, numbersInLink[0]);
		if (linkChannel == null) //找不到訊息內的頻道
		{
			afterAnalyze(event, link, ReturnResult.fail(NO_CHANNEL));
			return;
		}

		//從頻道中取得訊息 注意ID是String 與慣例的long不同
		linkChannel.retrieveMessageById(numbersInLink[1])
				.queue(message -> afterAnalyze(event, link, ReturnResult.success(message)),
						new ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE, e -> afterAnalyze(event, link, ReturnResult.fail(NO_MESSAGE))));
	}

	void afterAnalyze(IReplyCallback event, String link, ReturnResult<Message> result);
}