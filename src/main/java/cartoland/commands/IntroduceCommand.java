package cartoland.commands;

import cartoland.utilities.IDAndEntities;
import cartoland.utilities.IntroduceHandle;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @since 2.0
 * @author Alex Cai
 */
public class IntroduceCommand implements ICommand
{
	private final Map<String, ICommand> subCommands = new HashMap<>();

	public IntroduceCommand()
	{
		subCommands.put("user", new UserSubCommand());
		subCommands.put("update", new UpdateSubCommand());
	}

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		subCommands.get(event.getSubcommandName()).commandProcess(event);
	}
}

/**
 * @since 2.0
 * @author Alex Cai
 */
class UserSubCommand implements ICommand
{
	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		User user = event.getUser();
		User target = event.getOption("user", OptionMapping::getAsUser);
		if (target == null)
			target = user;

		String content = IntroduceHandle.getIntroduction(target.getIdLong());
		event.reply(content != null ? content : JsonHandle.getStringFromJsonKey(user.getIdLong(), "introduce.no_info")).queue();
	}
}

/**
 * @since 2.0
 * @author Alex Cai
 */
class UpdateSubCommand implements ICommand
{
	private final Pattern linkRegex = Pattern.compile("https://discord\\.com/channels/" + IDAndEntities.CARTOLAND_SERVER_ID + "/\\d+/\\d+");
	private static final int SUB_STRING_START = ("https://discord.com/channels/" + IDAndEntities.CARTOLAND_SERVER_ID + "/").length();

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		long userID = event.getUser().getIdLong();
		String content = event.getOption("content", OptionMapping::getAsString);
		if (content == null)
		{
			event.reply("Impossible, this is required!").queue();
			return;
		}

		if (content.equals("delete"))
		{
			IntroduceHandle.deleteIntroduction(userID);
			event.reply(JsonHandle.getStringFromJsonKey(userID, "introduce.delete")).queue();
			return;
		}

		if (linkRegex.matcher(content).matches())
		{
			String[] numbersInLink = content.substring(SUB_STRING_START).split("/");

			//從創聯中取得頻道
			MessageChannel linkChannel = IDAndEntities.cartolandServer.getChannelById(MessageChannel.class, Long.parseLong(numbersInLink[0]));
			if (linkChannel == null)
			{
				event.reply("Error: The channel might be deleted, or I don't have permission to access it.").queue();
				return;
			}

			//從頻道中取得訊息 注意ID是String 與慣例的long不同
			linkChannel.retrieveMessageById(numbersInLink[1]).queue(linkMessage ->
			{
				String rawMessage = linkMessage.getContentRaw();
				List<Attachment> attachments = linkMessage.getAttachments();
				if (!attachments.isEmpty())
					rawMessage += attachments.stream().map(Attachment::getUrl).collect(Collectors.joining("\n", "\n", ""));
				IntroduceHandle.updateIntroduction(userID, rawMessage);
			}, new ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE, e -> IntroduceHandle.updateIntroduction(userID, content)));
		}
		else
			IntroduceHandle.updateIntroduction(userID, content);

		event.reply(JsonHandle.getStringFromJsonKey(userID, "introduce.update")).queue();
	}
}