package cartoland.commands;

import cartoland.utilities.FileHandle;
import cartoland.utilities.IDAndEntities;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class IntroduceCommand implements ICommand
{
	private static final String INTRODUCTION_FILE_NAME = "introduction.ser";
	static final Map<Long, String> introduction = (FileHandle.deserialize(INTRODUCTION_FILE_NAME) instanceof Map map) ? map : new HashMap<>();
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

	public static void serializeIntroduction()
	{
		FileHandle.serialize(INTRODUCTION_FILE_NAME, introduction);
	}
}

class UserSubCommand implements ICommand
{
	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		User target = event.getOption("user", OptionMapping::getAsUser);
		if (target == null)
		{
			event.reply("Impossible, this is required!").queue();
			return;
		}

		String content = IntroduceCommand.introduction.get(target.getIdLong());
		event.reply((content != null) ? content : "I don't have any information about this user.").queue();
	}
}

class UpdateSubCommand implements ICommand
{
	private final Pattern linkRegex = Pattern.compile("https://discord\\.com/channels/" + IDAndEntities.CARTOLAND_SERVER_ID + "/\\d+/\\d+");
	private static final int SUB_STRING_START = ("https://discord.com/channels/" + IDAndEntities.CARTOLAND_SERVER_ID + "/").length();

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		String content = event.getOption("content", OptionMapping::getAsString);
		if (content == null)
		{
			event.reply("Impossible, this is required!").queue();
			return;
		}

		if (content.equals("delete"))
		{
			IntroduceCommand.introduction.remove(event.getUser().getIdLong());
			event.reply("Deleted success").queue();
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
				IntroduceCommand.introduction.put(event.getUser().getIdLong(), linkMessage.getContentRaw()));
		}
		else
			IntroduceCommand.introduction.put(event.getUser().getIdLong(), content);

		event.reply("Updated success").queue();
	}
}