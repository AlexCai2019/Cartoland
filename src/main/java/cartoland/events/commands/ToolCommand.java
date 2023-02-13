package cartoland.events.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.Arrays;
import java.util.HashMap;

public class ToolCommand implements ICommand
{
	private final HashMap<String, ICommand> subCommands = new HashMap<>();

	ToolCommand(CommandUsage commandCore)
	{
		subCommands.put("uuid_string", new ToolUUIDStringCommand(commandCore)); //tool uuid_string
		subCommands.put("uuid_array", new ToolUUIDArrayCommand(commandCore)); //tool uuid_array
	}

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		String subCommandName = event.getSubcommandName();
		if (subCommandName == null)
		{
			event.reply("Wait, this is impossible!").queue();
			return;
		}

		ICommand subCommandExecution = subCommands.get(subCommandName);
		if (subCommandExecution != null)
			subCommandExecution.commandProcess(event);
	}
}

class ToolUUIDStringCommand implements ICommand
{
	private final CommandUsage commandCore;

	ToolUUIDStringCommand(CommandUsage commandCore)
	{
		this.commandCore = commandCore;
	}

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		OptionMapping argument = commandCore.optionsStream.filter(optionMapping -> optionMapping.getName().equals("raw_uuid")).findAny().orElse(null);
		if (argument == null)
		{
			event.reply("Wait, the option is required!").queue();
			return;
		}

		String rawUUID = argument.getAsString();
		String[] uuidStrings;
		int[] uuidArray;

		//59c1027b-5559-4e6a-91e4-2b8b949656ce
		if (rawUUID.matches("[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}"))
			uuidStrings = rawUUID.split("-");
		else if (rawUUID.matches("[0-9A-Fa-f]{32}")) //59c1027b55594e6a91e42b8b949656ce
			uuidStrings = new String[]
			{
				rawUUID.substring(0, 8), //8個
				rawUUID.substring(8, 12), //4
				rawUUID.substring(12, 16), //4
				rawUUID.substring(16, 20), //4
				rawUUID.substring(20) //12
			};
		else //不是一個合法的UUID字串
		{
			event.reply("Please enter a UUID string by right format!").queue();
			return;
		}

		uuidArray = new int[]
		{
			(int)Long.parseLong(uuidStrings[0], 16), //先parse成long 再cast成int 這樣才能溢位成負數
			(int)Long.parseLong(uuidStrings[1] + uuidStrings[2], 16),
			(int)Long.parseLong(uuidStrings[3] + uuidStrings[4].substring(0, 4), 16),
			(int)Long.parseLong(uuidStrings[4].substring(4), 16)
		};

		event.reply("UUID: `" + String.join("-", uuidStrings) + "`\n" +
							"UUID(without dash): `" + String.join("", uuidStrings) + "`\n" +
							"UUID array: `" + Arrays.toString(uuidArray) + "`").queue();
	}
}

class ToolUUIDArrayCommand implements ICommand
{
	private final CommandUsage commandCore;

	ToolUUIDArrayCommand(CommandUsage commandCore)
	{
		this.commandCore = commandCore;
	}

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		Integer[] uuidArray = new Integer[4];
		for (int i = 0; i < 4; i++)
		{
			int finalI = i;
			commandCore.optionsStream
					.filter(optionMapping -> optionMapping.getName().equals(String.valueOf(finalI)))
					.findAny()
					.ifPresentOrElse(optionMapping -> uuidArray[0] = optionMapping.getAsInt(), () -> uuidArray[finalI] = null);
		}

		if (uuidArray[0] == null || uuidArray[1] == null || uuidArray[2] == null || uuidArray[3] == null)
		{
			event.reply("Wait, the option is required!").queue();
			return;
		}

		String[] uuidStrings = new String[5];
		String temp;
		uuidStrings[0] = Integer.toHexString(uuidArray[0]);
		temp = Integer.toHexString(uuidArray[1]);
		uuidStrings[1] = temp.substring(0, 4);
		uuidStrings[2] = temp.substring(4);
		temp = Integer.toHexString(uuidArray[2]);
		uuidStrings[3] = temp.substring(0, 4);
		uuidStrings[4] = temp.substring(4) + Integer.toHexString(uuidArray[3]);

		event.reply("UUID: `" + String.join("-", uuidStrings) + "`\n" +
							"UUID(without dash): `" + String.join("", uuidStrings) + "`\n" +
							"UUID array: `" + Arrays.toString(uuidArray) + "`").queue();
	}
}