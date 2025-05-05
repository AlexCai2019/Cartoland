package cartoland.commands;

import cartoland.utilities.JsonHandle;
import cartoland.utilities.RegularExpressions;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.Arrays;

/**
 * {@code ToolCommand} is an execution when a user uses /tool command. This class implements {@link ICommand} interface,
 * which is for the commands HashMap in {@link cartoland.events.CommandUsage}. This class doesn't handle sub
 * commands, but call other classes to deal with it.
 *
 * @since 1.4
 * @author Alex Cai
 */
public class ToolCommand extends HasSubcommands
{
	public static final String UUID_STRING = "uuid_string";
	public static final String UUID_ARRAY = "uuid_array";
	public static final String COLOR_RGBA = "color_rgba";
	public static final String COLOR_INTEGER = "color_integer";

	public ToolCommand()
	{
		super(4);
		subcommands.put(UUID_STRING, new UUIDStringSubCommand()); //tool uuid_string
		subcommands.put(UUID_ARRAY, new UUIDArraySubCommand()); //tool uuid_array
		subcommands.put(COLOR_RGBA, new ColorRGBASubCommand()); //tool color_rgba
		subcommands.put(COLOR_INTEGER, new ColorIntegerSubCommand()); //tool color_rgb
	}

	/**
	 * {@code UUIDStringSubCommand} is a class that handles one of the subcommands of {@code /tool} command, which
	 * is {@code /tool uuid_string}.
	 *
	 * @since 1.4
	 * @see ToolCommand
	 * @see UUIDArraySubCommand
	 * @author Alex Cai
	 */
	private static class UUIDStringSubCommand implements ICommand
	{
		@Override
		public void commandProcess(SlashCommandInteractionEvent event)
		{
			String rawUUID = event.getOption("raw_uuid", "", OptionMapping::getAsString);

			String[] uuidStrings;
			String dash, noDash;

			if (RegularExpressions.UUID_DASH_REGEX.matcher(rawUUID).matches()) //有橫線
			{
				uuidStrings = rawUUID.split("-");
				int length;

				//不用String.format("%8x")的方式補前綴0
				//因為還要將uuidStrings[0]轉成int
				length = uuidStrings[0].length();
				if (length < 8)
					uuidStrings[0] = ("00000000" + uuidStrings[0]).substring(length); //不滿8個數字就補上前綴0

				length = uuidStrings[1].length();
				if (length < 4)
					uuidStrings[1] = ("0000" + uuidStrings[1]).substring(length); //不滿4個數字就補上前綴0

				length = uuidStrings[2].length();
				if (length < 4)
					uuidStrings[2] = ("0000" + uuidStrings[2]).substring(length); //不滿4個數字就補上前綴0

				length = uuidStrings[3].length();
				if (length < 4)
					uuidStrings[3] = ("0000" + uuidStrings[3]).substring(length); //不滿4個數字就補上前綴0

				length = uuidStrings[4].length();
				if (length < 12)
					uuidStrings[4] = ("000000000000" + uuidStrings[4]).substring(length); //不滿12個數字就補上前綴0

				dash = rawUUID;
				noDash = String.join("", uuidStrings);
			}
			else if (RegularExpressions.UUID_NO_DASH_REGEX.matcher(rawUUID).matches()) //沒有橫線
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
				noDash = rawUUID;
			}
			else //不是一個合法的UUID字串
			{
				event.reply(JsonHandle.getString(event.getUser().getIdLong(), "tool.uuid_string.invalid_string")).setEphemeral(true).queue();
				return;
			}

			int[] uuidArray =
			{
				(int) Long.parseLong(uuidStrings[0], 16), //先parse成long 再cast成int 這樣才能溢位成負數
				(int) Long.parseLong(uuidStrings[1] + uuidStrings[2], 16),
				(int) Long.parseLong(uuidStrings[3] + uuidStrings[4].substring(0, 4), 16),
				(int) Long.parseLong(uuidStrings[4].substring(4), 16)
			};

			event.reply("UUID: `" + dash + "`\n" +
								"UUID (" + JsonHandle.getString(event.getUser().getIdLong(), "tool.uuid_string.without_dash") + "): `" + noDash + "`\n" +
								"UUID array: `" + Arrays.toString(uuidArray) + '`').queue();
		}
	}

	/**
	 * {@code UUIDArraySubCommand} is a class that handles one of the subcommands of {@code /tool} command, which is
	 * {@code /tool uuid_array}.
	 *
	 * @since 1.4
	 * @see ToolCommand
	 * @see UUIDStringSubCommand
	 * @author Alex Cai
	 */
	private static class UUIDArraySubCommand implements ICommand
	{
		@Override
		public void commandProcess(SlashCommandInteractionEvent event)
		{
			Integer[] uuidArray = new Integer[4]; //裝箱類別應該比基本型別好 因為下面大量使用了String.format
			for (char c = '0'; c < '4'; c++)
				uuidArray[c - '0'] = event.getOption(String.valueOf(c), 0, OptionMapping::getAsInt);

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
								"UUID(" + JsonHandle.getString(event.getUser().getIdLong(), "tool.uuid_array.without_dash") + "): `" + String.join("", uuidStrings) + "`\n" +
								"UUID array: `" + Arrays.toString(uuidArray) + '`').queue();
		}
	}

	/**
	 * {@code ColorRGBASubCommand} is a class that handles one of the subcommands of {@code /tool} command, which is
	 * {@code /tool color_rgba}.
	 *
	 * @since 1.6
	 * @author Alex Cai
	 */
	private static class ColorRGBASubCommand implements ICommand
	{
		@Override
		public void commandProcess(SlashCommandInteractionEvent event)
		{
			int[] rgbaColors = new int[4];
			int[] argbColors = new int[4];

			rgbaColors[0] = argbColors[1] = event.getOption("red", 0, OptionMapping::getAsInt);
			rgbaColors[1] = argbColors[2] = event.getOption("green", 0, OptionMapping::getAsInt);
			rgbaColors[2] = argbColors[3] = event.getOption("blue", 0, OptionMapping::getAsInt);
			rgbaColors[3] = argbColors[0] = event.getOption("alpha", 255, OptionMapping::getAsInt); //沒有設定透明度就是255
			int[] rgbColors = { rgbaColors[0],rgbaColors[1],rgbaColors[2] };

			int rgba = 0, argb = 0, rgb = 0;

			long userID = event.getUser().getIdLong();
			for (int i = 0, offset = 24; i < 4; i++)
			{
				int rgbaTemp = rgbaColors[i]; //避免重複解包
				int argbTemp = argbColors[i];
				if (notInRange(rgbaTemp) || notInRange(argbTemp)) //範圍不對
				{
					event.reply(JsonHandle.getString(userID, "tool.color_rgba.wrong_range")).setEphemeral(true).queue();
					return; //直接結束
				}

				rgba += rgbaTemp << offset; //舉例 如果是13,24,247,12 那麼作為紅色的13往左推24 bits 綠色是24左推16bits 藍色是247左推8 不透明度是12左推0 結果是#0D18F70C
				argb += argbTemp << offset;
				offset -= 8; //offset原是24 每次減8後 下次推的時候就是推16 然後推8 最後推0
				rgb += rgbaTemp << offset; //舉例 如果是13,24,247 那麼作為紅色的13往左推16 bits 綠色是24左推8bits 藍色是247左推0 結果是#0D18F7
			}

			String decimal = JsonHandle.getString(userID, "tool.color_rgba.decimal");
			String hexadecimal = JsonHandle.getString(userID, "tool.color_rgba.hexadecimal");

			event.reply("RGB: `" + Arrays.toString(rgbColors) + "`\n" +
								"RGB(" + decimal + "): `" + rgb + "`\n" +
								"RGB(" + hexadecimal + "): `#" + String.format("%06X` `#%06x", rgb, rgb) + "`\n" +
								"RGBA: `" + Arrays.toString(rgbaColors) + "`\n" +
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
	 * {@code ColorIntegerSubCommand} is a class that handles one of the subcommands of {@code /tool} command,
	 * which is {@code /tool color_integer}.
	 *
	 * @since 1.6
	 * @author Alex Cai
	 */
	private static class ColorIntegerSubCommand implements ICommand
	{
		private static final float DIVIDE_255 = 1.0F / 255.0F;

		@Override
		public void commandProcess(SlashCommandInteractionEvent event)
		{
			String rgbString = event.getOption("rgba_or_argb", "", OptionMapping::getAsString);

			long userID = event.getUser().getIdLong();
			long rgbaInput;
			if (RegularExpressions.DECIMAL_UNSIGNED_INT_REGEX.matcher(rgbString).matches())
				rgbaInput = Long.parseLong(rgbString);
			else if (RegularExpressions.HEXADECIMAL_UNSIGNED_INT_REGEX.matcher(rgbString).matches())
				rgbaInput = Long.parseLong(rgbString, 16);
			else if (RegularExpressions.LEADING_SHARP_HEXADECIMAL_UNSIGNED_INT_REGEX.matcher(rgbString).matches())
				rgbaInput = Long.parseLong(rgbString.substring(1), 16);//像#FFFFFF這樣開頭帶一個#的形式 並去掉開頭的#
			else
			{
				event.reply(JsonHandle.getString(userID, "tool.color_integer.wrong_argument")).setEphemeral(true).queue();
				return;
			}
			long rgba = Math.min(rgbaInput, 0xFFFF_FFFFL);

			//{ rgba / 65536 % 256, rgba / 256 % 256, rgba % 256 }
			//假設是16777215 除以65536就會變成255, 16777215 除以256後取除以256的餘數也是255, 取除以256的餘數也是255
			long[] colorsLong = { rgba >> 24, (rgba >> 16) & 255, (rgba >> 8) & 255, rgba & 255 };
			float[] colorsDouble = { colorsLong[0] * DIVIDE_255, colorsLong[1] * DIVIDE_255, colorsLong[2] * DIVIDE_255, colorsLong[3] * DIVIDE_255 };

			event.reply("RGBA / ARGB: (" + JsonHandle.getString(userID, "tool.color_integer.integer") + "): `" + Arrays.toString(colorsLong) + "`\n" +
								"RGBA / ARGB: (" + JsonHandle.getString(userID, "tool.color_integer.float") + "):`" + Arrays.toString(colorsDouble) + "`\n" +
								"RGBA / ARGB: (" + JsonHandle.getString(userID, "tool.color_integer.decimal") + "): `" + rgba + "`\n" +
								"RGBA / ARGB: (" + JsonHandle.getString(userID, "tool.color_integer.hexadecimal") + "): `#" + String.format("%08X` `#%08x`", rgba, rgba)).queue();
		}
	}
}