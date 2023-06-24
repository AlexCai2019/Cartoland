package cartoland.commands;

import cartoland.utilities.CommonFunctions;
import cartoland.utilities.IDAndEntities;
import cartoland.utilities.IntroduceHandle;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * {@code IntroduceCommand} is an execution when a user uses /introduce command. This class implements
 * {@link ICommand} interface, which is for the commands HashMap in {@link cartoland.events.CommandUsage}. This
 * class doesn't handle sub commands, but call other classes to deal with it.
 *
 * @since 2.0
 * @author Alex Cai
 */
public class IntroduceCommand implements ICommand
{
	/**
	 * {@code userSubCommand} is a lambda that handles one of the sub commands of {@code /introduce} command,
	 * which is {@code /introduce user}.
	 *
	 * @since 2.0
	 */
	private final ICommand userSubCommand = event ->
	{
		User user = event.getUser();
		User target = event.getOption("user", CommonFunctions.getAsUser);
		if (target == null) //沒有填 預設是自己
			target = user;

		String content = IntroduceHandle.getIntroduction(target.getIdLong());
		event.reply(content != null ? content : JsonHandle.getStringFromJsonKey(user.getIdLong(), "introduce.user.no_info")).queue();
	};
	private final ICommand updateSubCommand = new UpdateSubCommand();

	/**
	 * The execution of a slash command. Unlike other commands that has sub commands, since this
	 * command only has 2 sub commands, it uses a single ternary operation instead of HashMap to call the
	 * class that handles the sub command.
	 *
	 * @param event The event that carries information of the user and the command.
	 * @since 2.0
	 * @author Alex Cai
	 */
	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		String subCommandName = event.getSubcommandName();
		if (subCommandName != null)
			(subCommandName.equals("user") ? userSubCommand : updateSubCommand).commandProcess(event);
	}
}

/**
 * {@code UpdateSubCommand} is a class that handles one of the sub commands of {@code /introduce} command, which is
 * {@code /introduce update}.
 *
 * @since 2.0
 * @author Alex Cai
 */
class UpdateSubCommand implements ICommand
{
	private final Pattern linkRegex = Pattern.compile("https://discord\\.com/channels/" + IDAndEntities.CARTOLAND_SERVER_ID + "/\\d+/\\d+");
	private static final int SUB_STRING_START = ("https://discord.com/channels/" + IDAndEntities.CARTOLAND_SERVER_ID + "/").length();
	private static final String delete = "delete";

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		long userID = event.getUser().getIdLong();
		String content = event.getOption("content", CommonFunctions.getAsString);
		if (content == null)
		{
			event.reply("Impossible, this is required!").queue();
			return;
		}

		if (content.equals(delete)) //如果使用/introduce update delete
		{
			IntroduceHandle.deleteIntroduction(userID); //刪除自我介紹
			event.reply(JsonHandle.getStringFromJsonKey(userID, "introduce.update.delete")).queue();
			return;
		}

		if (linkRegex.matcher(content).matches()) //如果內容是個創聯群組連結
		{
			String[] numbersInLink = content.substring(SUB_STRING_START).split("/");

			//從創聯中取得頻道
			MessageChannel linkChannel = IDAndEntities.cartolandServer.getChannelById(MessageChannel.class, Long.parseLong(numbersInLink[0]));
			if (linkChannel == null)
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "introduce.update.no_channel")).queue();
				return;
			}

			//從頻道中取得訊息 注意ID是String 與慣例的long不同
			linkChannel.retrieveMessageById(numbersInLink[1]).queue(linkMessage ->
			{
				String rawMessage = linkMessage.getContentRaw();
				List<Message.Attachment> attachments = linkMessage.getAttachments();
				if (!attachments.isEmpty())
					rawMessage += attachments.stream().map(CommonFunctions.getUrl).collect(Collectors.joining("\n", "\n", ""));
				IntroduceHandle.updateIntroduction(linkMessage.getAuthor().getIdLong(), rawMessage);
			}, new ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE, e -> IntroduceHandle.updateIntroduction(userID, content)));
		}
		else
			IntroduceHandle.updateIntroduction(userID, content);

		event.reply(JsonHandle.getStringFromJsonKey(userID, "introduce.update.update")).queue();
	}
}