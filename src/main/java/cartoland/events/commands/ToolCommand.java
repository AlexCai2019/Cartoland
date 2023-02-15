package cartoland.events.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.Arrays;
import java.util.HashMap;

/**
 * {@code ToolCommand} is an execution when a user uses /tool command. This class doesn't handle sub command, but
 * call other classes to deal with it.
 *
 * @since 1.4
 * @author Alex Cai
 */
public class ToolCommand implements ICommand
{
	private final HashMap<String, ICommand> subCommands = new HashMap<>();

	ToolCommand()
	{
		subCommands.put("uuid_string", new UUIDStringCommand()); //tool uuid_string
		subCommands.put("uuid_array", new UUIDArrayCommand()); //tool uuid_array
		subCommands.put("pack_mcmeta", new PackMcmetaCommand()); //tool pack_mcmeta
	}

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		String subCommandName = event.getSubcommandName();
		if (subCommandName == null)
		{
			event.reply("Impossible, this is required!").queue();
			return;
		}

		ICommand subCommandExecution = subCommands.get(subCommandName);
		if (subCommandExecution != null)
			subCommandExecution.commandProcess(event);
	}
}

/**
 * {@code ToolUUIDStringCommand} is a class that handles one of the sub commands of {@code /tool} command, which is
 * {@code /tool uuid_string}.
 *
 * @since 1.4
 * @see ToolCommand
 * @see UUIDArrayCommand
 * @author Alex Cai
 */
class UUIDStringCommand implements ICommand
{
	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		String rawUUID = event.getOption("raw_uuid", OptionMapping::getAsString);
		if (rawUUID == null)
		{
			event.reply("Impossible, this is required!").queue();
			return;
		}

		String[] uuidStrings;
		String dash = rawUUID;
		String noDash = rawUUID;

		//59c1027b-5559-4e6a-91e4-2b8b949656ce
		if (rawUUID.matches("[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}"))
		{
			uuidStrings = rawUUID.split("-");
			noDash = String.join("", uuidStrings);
		}
		else if (rawUUID.matches("[0-9A-Fa-f]{32}")) //59c1027b55594e6a91e42b8b949656ce
		{
			uuidStrings = new String[]
			{
				rawUUID.substring(0, 8), //8
				rawUUID.substring(8, 12), //4
				rawUUID.substring(12, 16), //4
				rawUUID.substring(16, 20), //4
				rawUUID.substring(20) //12
			};
			dash = String.join("-", uuidStrings);
		}
		else //不是一個合法的UUID字串
		{
			event.reply("Please enter a UUID string by right format!").queue();
			return;
		}

		int[] uuidArray = new int[]
		{
			(int) Long.parseLong(uuidStrings[0], 16), //先parse成long 再cast成int 這樣才能溢位成負數
			(int) Long.parseLong(uuidStrings[1] + uuidStrings[2], 16),
			(int) Long.parseLong(uuidStrings[3] + uuidStrings[4].substring(0, 4), 16),
			(int) Long.parseLong(uuidStrings[4].substring(4), 16)
		};

		event.reply("UUID: `" + dash + "`\n" +
							"UUID(without dash): `" + noDash + "`\n" +
							"UUID array: `" + Arrays.toString(uuidArray) + "`").queue();
	}
}

/**
 * {@code ToolUUIDArrayCommand} is a class that handles one of the sub commands of {@code /tool} command, which is
 * {@code /tool uuid_array}.
 *
 * @since 1.4
 * @see ToolCommand
 * @see UUIDStringCommand
 * @author Alex Cai
 */
class UUIDArrayCommand implements ICommand
{
	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		Integer[] uuidArray = new Integer[4];
		for (int i = 0; i < 4; i++)
		{
			uuidArray[i] = event.getOption(String.valueOf(i), OptionMapping::getAsInt);
			if (uuidArray[i] == null)
			{
				event.reply("Impossible, this is required!").queue();
				return;
			}
		}

		//因為四個UUID是必填項 所以不須偵測是否存在 直接進程式
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

/**
 * {@code PackMcmetaCommand} is a class that handles one of the sub commands of {@code /tool} command, which is
 * {@code /tool pack_mcmeta}.
 *
 * @since 1.4
 * @see ToolCommand
 * @author Alex Cai
 */
class PackMcmetaCommand implements ICommand
{
	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		String packType = event.getOption("pack_type", OptionMapping::getAsString);
		if (packType == null)
		{
			event.reply("Impossible, this is required!").queue();
			return;
		}

		event.reply(switch (packType.charAt(0))
		{
			case 'd' ->
        		"""
				```json
				{
					"pack":
					{
						"pack_format": 10,
						"description": "Your description here"
					}
				}
				```
				""";

			case 'r' ->
    			"""
				```json
				{
					"pack":
					{
						"pack_format": 12,
						"description": "Your description here"
					}
				}
				```
				""";

			default -> "You need to choose whether data pack or resource pack.";
		}).queue();
	}
}