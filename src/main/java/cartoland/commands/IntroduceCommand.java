package cartoland.commands;

import cartoland.utilities.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.util.List;
import java.util.Map;

/**
 * {@code IntroduceCommand} is an execution when a user uses /introduce command. This class implements
 * {@link ICommand} interface, which is for the commands HashMap in {@link cartoland.events.CommandUsage}. This
 * class doesn't handle sub commands, but call other classes to deal with it.
 *
 * @since 2.0
 * @author Alex Cai
 */
public class IntroduceCommand extends HasSubcommands
{
	private static final String INTRODUCTION_FILE_NAME = "serialize/introduction.ser";

	@SuppressWarnings("unchecked")
	private static final Map<Long, String> introduction = CastToInstance.modifiableMap(FileHandle.deserialize(INTRODUCTION_FILE_NAME));

	public static final String USER = "user";

	public static final String UPDATE = "update";

	public static final String DELETE = "delete";

	static
	{
		FileHandle.registerSerialize(INTRODUCTION_FILE_NAME, introduction); //註冊串聯化
	}

	public IntroduceCommand()
	{
		super(3);

		subcommands.put("user", event ->
		{
			User user = event.getUser();
			User target = event.getOption("user", user, OptionMapping::getAsUser); //沒有填 預設是自己

			event.reply(introduction.getOrDefault(target.getIdLong(), JsonHandle.getString(user.getIdLong(), "introduce.user.no_info")))
					.setEphemeral(true)
					.queue();
		});
		subcommands.put("update", new UpdateSubCommand());
		subcommands.put("delete", event ->
		{
			long userID = event.getUser().getIdLong();
			event.reply(JsonHandle.getString(userID, "introduce.update.delete")).queue();
			introduction.remove(userID); //刪除自我介紹
		});
	}

	/**
	 * Update the user introduction. Whenever user typed anything in the elf-intro channel, the message will
	 * be store into {@link #introduction}.
	 *
	 * @param userID The ID of the user that are going to update his/her introduction.
	 * @param content The content of the introduction that the user want to replace the old one.
	 * @since 2.0
	 * @author Alex Cai
	 */
	public static void updateIntroduction(long userID, String content)
	{
		introduction.put(userID, content);
	}

	/**
	 * {@code UpdateSubCommand} is a class that handles one of the subcommands of {@code /introduce} command, which
	 * is {@code /introduce update}.
	 *
	 * @since 2.0
	 * @author Alex Cai
	 */
	private static class UpdateSubCommand implements ICommand
	{
		private static final int SUB_STRING_START = ("https://discord.com/channels/" + IDs.CARTOLAND_SERVER_ID + "/").length();

		@Override
		public void commandProcess(SlashCommandInteractionEvent event)
		{
			long userID = event.getUser().getIdLong();
			String content = event.getOption("content", "", OptionMapping::getAsString);
			if (content.isEmpty()) //空的代表刪除
			{
				event.reply(JsonHandle.getString(userID, "introduce.update.delete")).queue();
				introduction.remove(userID); //刪除自我介紹
				return;
			}

			if (!RegularExpressions.CARTOLAND_MESSAGE_LINK_REGEX.matcher(content).matches()) //如果內容不是創聯群組連結
			{
				event.reply(JsonHandle.getString(userID, "introduce.update.update")).queue();
				updateIntroduction(userID, content);
				return;
			}

			//以下就是處理創聯群組連結的部分
			String[] numbersInLink = content.substring(SUB_STRING_START).split("/");

			//從創聯中取得頻道
			Guild cartoland, eventGuild = event.getGuild(); //先假設指令在創聯中執行 這樣可以省去一次getGuildById
			if (eventGuild == null || eventGuild.getIdLong() != IDs.CARTOLAND_SERVER_ID) //結果不是在創聯
			{
				cartoland = event.getJDA().getGuildById(IDs.CARTOLAND_SERVER_ID); //定位創聯
				if (cartoland == null) //找不到創聯
				{
					event.reply("Can't get Cartoland server").queue();
					return; //結束
				}
			}
			else
				cartoland = eventGuild;

			//獲取訊息內的頻道 注意ID是String 與慣例的long不同
			GuildMessageChannel linkChannel = cartoland.getChannelById(GuildMessageChannel.class, numbersInLink[0]);
			if (linkChannel == null) //找不到訊息內的頻道
			{
				event.reply(JsonHandle.getString(userID, "introduce.update.no_channel")).queue();
				return;
			}

			//從頻道中取得訊息 注意ID是String 與慣例的long不同
			linkChannel.retrieveMessageById(numbersInLink[1]).queue(linkMessage ->
			{
				event.reply(JsonHandle.getString(userID, "introduce.update.update")).queue(); //越早回覆越好 以免超過三秒
				List<Message.Attachment> attachments = linkMessage.getAttachments(); //附件
				String introductionString; //介紹文字
				if (attachments.isEmpty()) //如果沒有附件 這是比較常見的情形
					introductionString = linkMessage.getContentRaw(); //直接等於訊息內容
				else //有附件 改用StringBuilder處理
				{
					StringBuilder introduceBuilder = new StringBuilder(linkMessage.getContentRaw()); //訊息內容
					for (Message.Attachment attachment : attachments)
						introduceBuilder.append('\n').append(attachment.getUrl()); //一一獲取附件的連結
					introductionString = introduceBuilder.toString();
				}
				updateIntroduction(linkMessage.getAuthor().getIdLong(), introductionString); //更新介紹
			}, new ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE, e -> //找不到訊息
			{
				event.reply(JsonHandle.getString(userID, "introduce.update.no_message")).queue();
				updateIntroduction(userID, content); //更新介紹 直接把連結放進內容中
			}));
		}
	}
}