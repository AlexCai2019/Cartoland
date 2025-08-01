package cartoland.methods;

import cartoland.Cartoland;
import cartoland.utilities.IDs;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.requests.ErrorResponse;

public interface IAnalyzeCTLCLink
{
	int SUB_STRING_START = ("https://discord.com/channels/" + IDs.CARTOLAND_SERVER_ID + "/").length();
	int NO_GUILD = 1;
	int NO_CHANNEL = 2;
	int NO_MESSAGE = 3;

	default void analyze(IReplyCallback event, String link)
	{
		//從創聯中取得頻道
		Guild cartoland = Cartoland.getJDA().getGuildById(IDs.CARTOLAND_SERVER_ID);
		if (cartoland == null) //找不到創聯
		{
			whenFail(event, link, NO_GUILD);
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
			whenFail(event, link, NO_CHANNEL);
			return;
		}

		//從頻道中取得訊息 注意ID是String 與慣例的long不同
		linkChannel.retrieveMessageById(numbersInLink[1])
				.queue(message -> whenSuccess(event, message), new ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE, e -> whenFail(event, link, NO_MESSAGE)));
	}

	void whenSuccess(IReplyCallback event, Message message);
	void whenFail(IReplyCallback event, String link, int failCode);
}