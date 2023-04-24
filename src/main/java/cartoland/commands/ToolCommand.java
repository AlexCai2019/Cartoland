package cartoland.commands;

import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
	private final Map<String, ICommand> subCommands = new HashMap<>();

	public ToolCommand()
	{
		subCommands.put("uuid_string", new UUIDStringCommand()); //tool uuid_string
		subCommands.put("uuid_array", new UUIDArrayCommand()); //tool uuid_array
		subCommands.put("color_rgba", new ColorRGBA()); //tool color_rgba
		subCommands.put("color_integer", new ColorInteger()); //tool color_rgb
		subCommands.put("pack_mcmeta", new PackMcmetaCommand()); //tool pack_mcmeta
	}

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		subCommands.get(event.getSubcommandName()).commandProcess(event);
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
		else //不是一個合法的UUID字串
		{
			event.reply(JsonHandle.getStringFromJsonKey(event.getUser().getIdLong(), "tool.uuid_string.invalid_string")).queue();
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
							"UUID (" + JsonHandle.getStringFromJsonKey(event.getUser().getIdLong(), "tool.uuid_string.without_dash") + "): `" + noDash + "`\n" +
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
		uuidStrings[0] = String.format("%08x", uuidArray[0]);
		temp = String.format("%08x", uuidArray[1]);
		uuidStrings[1] = temp.substring(0, 4);
		uuidStrings[2] = temp.substring(4);
		temp = String.format("%08x", uuidArray[2]);
		uuidStrings[3] = temp.substring(0, 4);
		uuidStrings[4] = temp.substring(4) + String.format("%08x", uuidArray[3]);

		event.reply("UUID: `" + String.join("-", uuidStrings) + "`\n" +
							"UUID(" + JsonHandle.getStringFromJsonKey(event.getUser().getIdLong(), "tool.uuid_array.without_dash") + "): `" + String.join("", uuidStrings) + "`\n" +
							"UUID array: `" + Arrays.toString(uuidArray) + "`").queue();
	}
}

/**
 * {@code ColorRGBA} is a class that handles one of the sub commands of {@code /tool} command, which is
 * {@code /tool color_rgba}.
 *
 * @since 1.6
 * @author Alex Cai
 */
class ColorRGBA implements ICommand
{
	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		Integer[] rgbaColors = new Integer[4];
		Integer[] argbColors = new Integer[4];

		rgbaColors[0] = argbColors[1] = event.getOption("red", OptionMapping::getAsInt);
		rgbaColors[1] = argbColors[2] = event.getOption("green", OptionMapping::getAsInt);
		rgbaColors[2] = argbColors[3] = event.getOption("blue", OptionMapping::getAsInt);
		rgbaColors[3] = argbColors[0] = event.getOption("alpha", OptionMapping::getAsInt);

		int rgba = 0, argb = 0;

		long userID = event.getUser().getIdLong();
		for (int i = 0, offset = 24; i < 4; i++)
		{
			if (rgbaColors[i] == null || argbColors[i] == null)
			{
				event.reply("Impossible, this is required!").queue();
				return;
			}

			if (notInRange(rgbaColors[i]) || notInRange(argbColors[i]))
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "tool.color_rgba.wrong_range")).queue();
				return;
			}

			rgba += rgbaColors[i] << offset; //舉例 如果是#0D18F70C 那麼紅色就是13 然後往左推24 bits 綠色是24左推16bits 藍色是247左推8 不透明度是12左推0 結果是
			argb += argbColors[i] << offset;
			offset -= 8; //offset原是24 每次減8後 下次推的時候就是推16 然後推8 最後推0
		}

		String decimal = JsonHandle.getStringFromJsonKey(userID, "tool.color_rgba.decimal");
		String hexadecimal = JsonHandle.getStringFromJsonKey(userID, "tool.color_rgba.hexadecimal");
		event.reply("RGBA: `" + Arrays.toString(rgbaColors) + "`\n" +
							"RGBA(" + decimal + "): `" + rgba + "`\n" +
							"RGBA(" + hexadecimal + "): `#" + String.format("%08X` `#%08x", rgba, rgba) + "`\n" +
							"ARGB: `" + Arrays.toString(argbColors) + "`\n" +
							"ARGB(" + decimal + "): `" + argb + "`\n" +
							"ARGB(" + hexadecimal + "): `#" + String.format("%08X` `#%08x`", argb, argb)).queue();
	}

	private boolean notInRange(int color)
	{
		return color < 0 || color > 255;
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
	private final Pattern decimalRegex = Pattern.compile("\\d{1,10}"); //最高4294967295 最低0
	private final Pattern hexadecimalRegex = Pattern.compile("[0-9A-Fa-f]{8}"); //FFFFFFFF
	private final Pattern leadingSharpHexadecimalRegex = Pattern.compile("#[0-9A-Fa-f]{8}"); //#FFFFFFFF

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		String rgbString = event.getOption("rgba_or_argb", OptionMapping::getAsString);
		if (rgbString == null)
		{
			event.reply("Impossible, this is required!").queue();
			return;
		}

		long userID = event.getUser().getIdLong();
		long rgba;
		if (decimalRegex.matcher(rgbString).matches())
		{
			rgba = Long.parseLong(rgbString);
			if (rgba > 0xFFFFFFFFL)
				rgba = 0xFFFFFFFFL;
		}
		else if (hexadecimalRegex.matcher(rgbString).matches())
			rgba = Integer.parseInt(rgbString, 16);
		else if (leadingSharpHexadecimalRegex.matcher(rgbString).matches())
			rgba = Integer.parseInt(rgbString.substring(1), 16); //像#FFFFFF這樣開頭帶一個#的形式 並去掉開頭的#
		else
		{
			event.reply(JsonHandle.getStringFromJsonKey(userID, "tool.color_integer.wrong_argument")).queue();
			return;
		}

		//{ rgba / 65536 % 256, rgba / 256 % 256, rgba % 256 }
		//假設是16777215 除以65536就會變成255, 16777215 除以256後取除以256的餘數也是255, 取除以256的餘數也是255
		long[] rgbaColors = { rgba >> 24, (rgba >> 16) & 255, (rgba >> 8) & 255, rgba & 255 };
		long[] argbColors = { rgbaColors[3],rgbaColors[0],rgbaColors[1],rgbaColors[2] };

		event.reply("RGBA: `" + Arrays.toString(rgbaColors) + "`\n" +
							"ARGB: `" + Arrays.toString(argbColors) + "`\n" +
							"RGBA / ARGB(" + JsonHandle.getStringFromJsonKey(userID, "tool.color_integer.decimal") + "): `" + rgba + "`\n" +
							"RGBA / ARGB(" + JsonHandle.getStringFromJsonKey(userID, "tool.color_integer.hexadecimal") + "): `#" + String.format("%08X` `#%08x`", rgba, rgba)).queue();
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

			default -> "You need to choose whether you are making a datapack or a resourcepack.";
		}).queue();
	}
}