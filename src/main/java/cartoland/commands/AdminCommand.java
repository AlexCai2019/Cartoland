package cartoland.commands;

import cartoland.utilities.AdminHandle;
import cartoland.utilities.CommonFunctions;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 2.1
 * @author Alex Cai
 */
public class AdminCommand implements ICommand
{
	private final Map<String, ICommand> subCommands = new HashMap<>();

	public AdminCommand()
	{
		subCommands.put("mute", new MuteSubCommand());
		subCommands.put("temp_ban", new TempBanSubCommand());
	}

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		subCommands.get(event.getSubcommandName()).commandProcess(event);
	}

	/**
	 * @since 2.1
	 * @author Alex Cai
	 */
	private static class MuteSubCommand implements ICommand
	{
		@Override
		public void commandProcess(SlashCommandInteractionEvent event)
		{
			AdminHandle.ReturnInformation returnInformation = AdminHandle.mute(
					event.getMember(),
					event.getOption("target", CommonFunctions.getAsMember),
					event.getOption("duration", CommonFunctions.getAsDouble),
					event.getOption("unit", CommonFunctions.getAsString),
					event.getOption("reason", CommonFunctions.getAsString));
			if (returnInformation != null)
				event.reply(JsonHandle.getStringFromJsonKey(event.getUser().getIdLong(), returnInformation.jsonKey()))
						.setEphemeral(returnInformation.ephemeral())
						.queue();
			else
				event.reply("Something went wrong...").queue();
		}
	}

	/**
	 * @since 2.1
	 * @author Alex Cai
	 */
	private static class TempBanSubCommand implements ICommand
	{
		@Override
		public void commandProcess(SlashCommandInteractionEvent event)
		{
			AdminHandle.ReturnInformation returnInformation = AdminHandle.tempBan(
					event.getMember(),
					event.getOption("target", CommonFunctions.getAsMember),
					event.getOption("duration", CommonFunctions.getAsDouble),
					event.getOption("unit", CommonFunctions.getAsString),
					event.getOption("reason", CommonFunctions.getAsString));
			if (returnInformation != null)
				event.reply(JsonHandle.getStringFromJsonKey(event.getUser().getIdLong(), returnInformation.jsonKey()))
						.setEphemeral(returnInformation.ephemeral())
						.queue();
			else
				event.reply("Something went wrong...").queue();
		}
	}
}