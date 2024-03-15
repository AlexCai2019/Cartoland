package cartoland.commands;

import cartoland.utilities.CommonFunctions;
import cartoland.utilities.IDs;
import cartoland.utilities.JsonHandle;
import cartoland.utilities.RegularExpressions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.ErrorResponse;

/**
 * {@code QuoteCommand} is an execution when a user uses /transfer command. This class implements {@link ICommand}
 * interface, which is for the commands HashMap in {@link cartoland.events.CommandUsage}.
 *
 * @since 1.6
 * @author Alex Cai
 */
public class QuoteCommand implements ICommand
{
	private static final int SUB_STRING_START = ("https://discord.com/channels/" + IDs.CARTOLAND_SERVER_ID + "/").length();

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		User user = event.getUser();
		long userID = user.getIdLong();
		String link = event.getOption("link", "", CommonFunctions.getAsString);

		if (!RegularExpressions.CARTOLAND_MESSAGE_LINK_REGEX.matcher(link).matches()) //不是一個有效的訊息連結 或不在創聯
		{
			event.reply(JsonHandle.getString(userID, "quote.invalid_link")).setEphemeral(true).queue();
			return;
		}

		String[] numbersInLink = link.substring(SUB_STRING_START).split("/"); //從字串中取得數字
		//舉例 https://discord.com/channels/886936474723950603/886936474723950611/891666028986253322
		//numbersInLink[0] = "886936474723950611";
		//numbersInLink[1] = "891666028986253322";

		//從創聯中取得頻道
		Guild cartoland, eventGuild = event.getGuild(); //先假設指令在創聯中執行 這樣可以省去一次getGuildById
		if (eventGuild == null || eventGuild.getIdLong() != IDs.CARTOLAND_SERVER_ID) //結果不是在創聯
		{
			cartoland = event.getJDA().getGuildById(IDs.CARTOLAND_SERVER_ID); //定位創聯
			if (cartoland == null) //找不到創聯
			{
				event.reply("Can't get Cartoland server").setEphemeral(true).queue();
				return; //結束
			}
		}
		else
			cartoland = eventGuild;

		//獲取訊息內的頻道 注意ID是String 與慣例的long不同
		GuildMessageChannel linkChannel = cartoland.getChannelById(GuildMessageChannel.class, numbersInLink[0]);
		if (linkChannel == null)
		{
			event.reply(JsonHandle.getString(userID, "quote.no_channel")).setEphemeral(true).queue();
			return;
		}
		//從頻道中取得訊息 注意ID是String 與慣例的long不同
		linkChannel.retrieveMessageById(numbersInLink[1]).queue(linkMessage ->
		{
			User linkAuthor = linkMessage.getAuthor(); //連結訊息的發送者

			EmbedBuilder embedBuilder = new EmbedBuilder()
					.setAuthor(linkAuthor.getEffectiveName(), null, linkAuthor.getEffectiveAvatarUrl())
					.setDescription(linkMessage.getContentRaw()) //訊息的內容
					.setTimestamp(linkMessage.getTimeCreated()) //連結訊息的發送時間
					.setFooter(linkChannel.getName(), null); //訊息的發送頻道

			//選擇連結訊息內的第一張圖片作為embed的圖片
			for (Message.Attachment attachment : linkMessage.getAttachments())
			{
				if (attachment.isImage())
				{
					embedBuilder.setImage(attachment.getUrl());
					break;
				}
			}

			(event.getOption("mention_author", false, CommonFunctions.getAsBoolean) ? //是否提及訊息作者
					event.reply(JsonHandle.getString(userID, "quote.mention",
							user.getEffectiveName(), linkAuthor.getAsMention())).addEmbeds(embedBuilder.build()) : //提及訊息作者
					event.replyEmbeds(embedBuilder.build())) //不提及訊息作者
				.addActionRow(Button.link(link, JsonHandle.getString(userID, "quote.jump_message"))).queue();
		}, new ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE, e -> event.reply(JsonHandle.getString(userID, "quote.no_message")).setEphemeral(true).queue()));
	}
}