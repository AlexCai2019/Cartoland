package cartoland.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.FileUpload;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

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

	public ToolCommand()
	{
		subCommands.put("uuid_string", new UUIDStringCommand()); //tool uuid_string
		subCommands.put("uuid_array", new UUIDArrayCommand()); //tool uuid_array
		subCommands.put("color_rgb", new ColorRGB()); //tool color_rgb
		subCommands.put("color_integer", new ColorInteger()); //tool color_rgb
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
	//59c1027b-5559-4e6a-91e4-2b8b949656ce
	private final Pattern dashRegex = Pattern.compile("[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}");
	//59c1027b55594e6a91e42b8b949656ce
	private final Pattern noDashRegex = Pattern.compile("[0-9A-Fa-f]{32}");

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
		String dash = rawUUID, noDash = rawUUID;

		if (dashRegex.matcher(rawUUID).matches())
		{
			uuidStrings = rawUUID.split("-");
			noDash = String.join("", uuidStrings);
		}
		else if (noDashRegex.matcher(rawUUID).matches())
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
		else //?????????????????????UUID??????
		{
			event.reply("Incorrect syntax: please enter the UUID string in the correct format!").queue();
			return;
		}

		int[] uuidArray = new int[]
		{
			(int) Long.parseLong(uuidStrings[0], 16), //???parse???long ???cast???int ???????????????????????????
			(int) Long.parseLong(uuidStrings[1] + uuidStrings[2], 16),
			(int) Long.parseLong(uuidStrings[3] + uuidStrings[4].substring(0, 4), 16),
			(int) Long.parseLong(uuidStrings[4].substring(4), 16)
		};

		event.reply("UUID: `" + dash + "`\n" +
							"UUID (without dashes): `" + noDash + "`\n" +
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

		//????????????UUID???????????? ?????????????????????????????? ???????????????
		String[] uuidStrings = new String[5];
		String temp;
		uuidStrings[0] = String.format("%08x", uuidArray[0]);
		temp = String.format("%08x", uuidArray[1]);
		uuidStrings[1] = temp.substring(0, 4);
		uuidStrings[2] = temp.substring(4);
		temp = String.format("%08x", uuidArray[2]);
		uuidStrings[3] = temp.substring(0, 4);
		uuidStrings[4] = temp.substring(4) + String.format("%08x", uuidArray[3]);

		event.reply("UUID: `" + String.join("-", uuidStrings) + "`\n" +
							"UUID(without dash): `" + String.join("", uuidStrings) + "`\n" +
							"UUID array: `" + Arrays.toString(uuidArray) + "`").queue();
	}
}

/**
 * {@code ColorRGB} is a class that handles one of the sub commands of {@code /tool} command, which is
 * {@code /tool color_rgb}.
 *
 * @since 1.6
 * @author Alex Cai
 */
class ColorRGB implements ICommand
{
	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		Integer[] colors =
		{
			event.getOption("red", OptionMapping::getAsInt),
			event.getOption("green", OptionMapping::getAsInt),
			event.getOption("blue", OptionMapping::getAsInt)
		};

		int rgb = 0;
		int offset = 65536; //16 * 16 * 16 * 16

		for (Integer color : colors)
		{
			if (color == null)
			{
				event.reply("Impossible, this is required!").queue();
				return;
			}

			if (color < 0 || color > 255)
			{
				event.reply("You can't input an integer that is not in the range from 0 to 255!").queue();
				return;
			}

			rgb += color * offset; //?????? ?????????#0D18F7 ??????????????????13 ????????????65536 ?????????24??????256 ?????????247??????1 ?????????858359
			offset >>= 8; //offset /= 256;
		}

		event.reply("RGB: `" + Arrays.toString(colors) + "`\n" +
							"RGB(Decimal): `" + rgb + "`\n" +
							"RGB(Hexadecimal): `#" + String.format("%06X` `#%6x`", rgb, rgb)).queue();
	}
}

/**
 * {@code ColorInteger} is a class that handles one of the sub commands of {@code /tool} command, which is
 * {@code /tool color_integer}.
 *
 * @since 1.6
 * @author Alex Cai
 */
class ColorInteger implements ICommand
{
	private final Pattern decimalRegex = Pattern.compile("\\d{1,8}"); //??????16777215 ??????0
	private final Pattern hexadecimalRegex = Pattern.compile("[0-9A-Fa-f]{6}"); //FFFFFF
	private final Pattern leadingSharpHexadecimalRegex = Pattern.compile("#[0-9A-Fa-f]{6}"); //#FFFFFF

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		String rgbString = event.getOption("rgb", OptionMapping::getAsString);
		if (rgbString == null)
		{
			event.reply("Impossible, this is required!").queue();
			return;
		}

		int rgb;
		if (decimalRegex.matcher(rgbString).matches())
		{
			rgb = Integer.parseInt(rgbString);
			if (rgb > 0xFFFFFF) //16777215
				rgb = 0xFFFFFF;
		}
		else if (hexadecimalRegex.matcher(rgbString).matches())
			rgb = Integer.parseInt(rgbString, 16);
		else if (leadingSharpHexadecimalRegex.matcher(rgbString).matches())
			rgb = Integer.parseInt(rgbString.substring(1), 16); //???#FFFFFF?????????????????????#????????? ??????????????????#
		else
		{
			event.reply("Please use /tool color_integer <decimal integer>, /tool color_integer <hexadecimal integer> or /tool color_integer #<hexadecimal integer>").queue();
			return;
		}

		//{ rgb / 65536, rgb / 256 % 256, rgb % 256 }
		//?????????16777215 ??????65536????????????255, 16777215 ??????256????????????256???????????????255, ?????????256???????????????255
		int[] colors = { rgb >> 16, (rgb >> 16) & 255, rgb & 255 };

		event.reply("RGB: `" + Arrays.toString(colors) + "`\n" +
							"RGB(Decimal): `" + rgb + "`\n" +
							"RGB(Hexadecimal): `#" + String.format("%06X` `#%6x`", rgb, rgb)).queue();
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

		event.replyFiles(FileUpload.fromData(switch (packType.charAt(0))
		{
			case 'd' ->
        		"""
				{
					"pack":
					{
						"pack_format": 10,
						"description": "Your description here"
					}
				}
				""".getBytes(StandardCharsets.UTF_8);

			case 'r' ->
    			"""
				{
					"pack":
					{
						"pack_format": 12,
						"description": "Your description here"
					}
				}
				""".getBytes(StandardCharsets.UTF_8);

			default ->
					"You need to choose whether you are making a data pack or a resource pack.".getBytes(StandardCharsets.UTF_8);
		}, "pack.mcmeta")).queue();
	}
}