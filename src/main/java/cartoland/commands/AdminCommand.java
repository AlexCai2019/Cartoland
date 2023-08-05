package cartoland.commands;

import cartoland.utilities.CommonFunctions;
import cartoland.utilities.FileHandle;
import cartoland.utilities.JsonHandle;
import cartoland.utilities.TimerHandle;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * {@code AdminCommand} is an execution when a moderator uses /admin command. This class implements {@link ICommand}
 * interface, which is for the commands HashMap in {@link cartoland.events.CommandUsage}. This class doesn't
 * handle sub commands, but call other classes to deal with it.
 *
 * @since 2.1
 * @author Alex Cai
 */
public class AdminCommand implements ICommand
{
	private static final String TEMP_BAN_SET = "serialize/temp_ban_set.ser";

	//userID為value[0] ban time為value[1] ban guild為value[2]
	public static final Set<long[]> tempBanSet = (FileHandle.deserialize(TEMP_BAN_SET) instanceof HashSet<?> set) ? set.stream()
			.map(o -> (long[])o).collect(Collectors.toSet()) : new HashSet<>();
	public static final byte USER_ID_INDEX = 0;
	public static final byte BANNED_TIME = 1;
	public static final byte BANNED_SERVER = 2;
	private final Map<String, ICommand> subCommands = new HashMap<>();

	static
	{
		FileHandle.registerSerialize(TEMP_BAN_SET, tempBanSet); //註冊串聯化
	}

	public AdminCommand()
	{
		subCommands.put("mute", new MuteSubCommand());
		subCommands.put("temp_ban", new TempBanSubCommand());
		subCommands.put("slow_mode", new SlowModeSubCommand());
	}

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		subCommands.get(event.getSubcommandName()).commandProcess(event);
	}

	private static String buildDurationString(double duration)
	{
		String durationString = Double.toString(duration); //將時間轉成字串
		int dotIndex = durationString.indexOf('.'); //小數點的索引
		int firstZero = durationString.length(); //尋找小數部分最後的一串0中 第一個0
		int index;
		for (index = firstZero - 1; index >= dotIndex; index--)
		{
			if (durationString.charAt(index) != '0')
			{
				firstZero = index + 1;
				break;
			}
		}
		//結果:
		//5.000000 => 5
		//1.500000 => 1.5
		return durationString.substring(0, (index == dotIndex) ? dotIndex : firstZero);
	}

	/**
	 * {@code MuteSubCommand} is a class that handles one of the sub commands of {@code /admin} command, which is
	 * {@code /admin mute}.
	 *
	 * @since 2.1
	 * @author Alex Cai
	 */
	private static class MuteSubCommand implements ICommand
	{
		private static final long TWENTY_EIGHT_DAYS_MILLISECONDS = 1000L * 60 * 60 * 24 * 28;

		@Override
		public void commandProcess(SlashCommandInteractionEvent event)
		{
			Member member = event.getMember();
			if (member == null)
			{
				event.reply("Impossible, this is required!").queue();
				return;
			}

			long userID = member.getIdLong();

			if (!member.hasPermission(Permission.MODERATE_MEMBERS))
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "admin.mute.no_permission")).setEphemeral(true).queue();
				return;
			}

			Member target = event.getOption("target", CommonFunctions.getAsMember);
			if (target == null) //找不到要被禁言的成員
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "admin.mute.no_member")).setEphemeral(true).queue();
				return;
			}

			if (target.isOwner()) //無法禁言群主 會擲出HierarchyException
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "admin.mute.can_t_owner")).setEphemeral(true).queue();
				return;
			}
			if (target.isTimedOut()) //已經被禁言了
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "admin.mute.already_timed_out")).setEphemeral(true).queue();
				return;
			}

			Double durationBox = event.getOption("duration", CommonFunctions.getAsDouble);
			if (durationBox == null)
			{
				event.reply("Impossible, this is required!").queue();
				return;
			}
			double duration = durationBox;
			if (duration <= 0) //不能負時間
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "admin.mute.duration_must_be_positive")).setEphemeral(true).queue();
				return;
			}

			String unit = event.getOption("unit", CommonFunctions.getAsString);
			if (unit == null) //單位
			{
				event.reply("Impossible, this is required!").queue();
				return;
			}

			//不用java.util.concurrent.TimeUnit 因為它不接受浮點數
			long durationMillis = Math.round(duration * switch (unit) //將單位轉成毫秒 1000毫秒等於1秒
			{
				case "second" -> 1000;
				case "minute" -> 1000 * 60;
				case "hour" -> 1000 * 60 * 60;
				case "double_hour" -> 1000 * 60 * 60 * 2;
				case "day" -> 1000 * 60 * 60 * 24;
				case "week" -> 1000 * 60 * 60 * 24 * 7;
				default -> 1;
			}); //Math.round會處理溢位

			if (durationMillis > TWENTY_EIGHT_DAYS_MILLISECONDS) //不能禁言超過28天
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "admin.mute.too_long")).setEphemeral(true).queue();
				return;
			}

			String mutedTime = buildDurationString(duration) + ' ' + JsonHandle.getStringFromJsonKey(userID, "admin.mute.unit_" + unit);
			String replyString = JsonHandle.getStringFromJsonKey(userID, "admin.mute.success")
					.formatted(target.getAsMention(), mutedTime, (System.currentTimeMillis() + durationMillis) / 1000);
			String reason = event.getOption("reason", CommonFunctions.getAsString);
			if (reason != null) //有理由
				replyString += JsonHandle.getStringFromJsonKey(userID, "admin.mute.reason").formatted(reason);

			event.reply(replyString).queue();
			target.timeoutFor(Duration.ofMillis(durationMillis)).reason(reason).queue();
		}
	}

	/**
	 * {@code TempBanSubCommand} is a class that handles one of the sub commands of {@code /admin} command, which is
	 * {@code /admin temp_ban}.
	 *
	 * @since 2.1
	 * @author Alex Cai
	 */
	private static class TempBanSubCommand implements ICommand
	{
		@Override
		public void commandProcess(SlashCommandInteractionEvent event)
		{
			Member member = event.getMember();
			if (member == null)
			{
				event.reply("Impossible, this is required!").queue();
				return;
			}

			long userID = member.getIdLong();

			if (!member.hasPermission(Permission.BAN_MEMBERS))
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "admin.temp_ban.no_permission")).setEphemeral(true).queue();
				return;
			}

			Member target = event.getOption("target", CommonFunctions.getAsMember);
			if (target == null)
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "admin.temp_ban.no_member")).setEphemeral(true).queue();
				return;
			}
			if (target.isOwner()) //無法禁言群主 會擲出HierarchyException
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "admin.temp_ban.can_t_owner")).setEphemeral(true).queue();
				return;
			}

			Double durationBox = event.getOption("duration", CommonFunctions.getAsDouble);
			if (durationBox == null)
			{
				event.reply("Impossible, this is required!").queue();
				return;
			}
			double duration = durationBox;
			if (duration <= 0) //不能負時間
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "admin.temp_ban.duration_too_short")).setEphemeral(true).queue();
				return;
			}

			String unit = event.getOption("unit", CommonFunctions.getAsString);
			if (unit == null)
			{
				event.reply("Impossible, this is required!").queue();
				return;
			}

			long durationHours = Math.round(duration * switch (unit) //將單位轉成小時
			{
				case "double_hour" -> 2;
				case "day" -> 24;
				case "week" -> 24 * 7;
				case "month" -> 24 * 30;
				case "season" -> 24 * 30 * 3;
				case "year" -> 24 * 365;
				case "wood_rat" -> 24 * 365 * 60;
				case "century" -> 24 * 365 * 100;
				default -> 1;
			}); //Math.round會處理溢位

			if (durationHours < 1L) //時間不能小於一小時
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "admin.temp_ban.duration_too_short")).setEphemeral(true).queue();
				return;
			}

			String bannedTime = buildDurationString(duration) + ' ' + JsonHandle.getStringFromJsonKey(userID, "admin.temp_ban.unit_" + unit);
			String replyString = JsonHandle.getStringFromJsonKey(userID, "admin.temp_ban.success")
					.formatted(
							target.getAsMention(), bannedTime,
							System.currentTimeMillis() / 1000 + durationHours * 60 * 60); //直到<t:> 以秒為單位
			//TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + TimeUnit.HOURS.toSeconds(durationHours)

			String reason = event.getOption("reason", CommonFunctions.getAsString);
			if (reason != null)
				replyString += JsonHandle.getStringFromJsonKey(userID, "admin.temp_ban.reason").formatted(reason);

			event.reply(replyString).queue(); //回覆

			//回覆完再開始動作 避免超過三秒限制
			long pardonTime = TimerHandle.getHoursFrom1970() + durationHours; //計算解除時間
			//TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis())
			if (pardonTime <= 0) //溢位
				pardonTime = Long.MAX_VALUE;

			Guild guild = target.getGuild();
			long[] banData = new long[3];
			banData[USER_ID_INDEX] = target.getIdLong();
			banData[BANNED_TIME] = pardonTime;
			banData[BANNED_SERVER] = guild.getIdLong();
			tempBanSet.add(banData); //紀錄ban了這個人
			guild.ban(target, 0, TimeUnit.SECONDS).reason(reason + '\n' + bannedTime).queue();
		}
	}

	/**
	 * {@code SlowModeSubCommand} is a class that handles one of the sub commands of {@code /admin} command, which is
	 * {@code /admin slow_mode}.
	 *
	 * @since 2.1
	 * @author Alex Cai
	 */
	private static class SlowModeSubCommand implements ICommand
	{
		@Override
		public void commandProcess(SlashCommandInteractionEvent event)
		{
			if (!(event.getOption("channel", CommonFunctions.getAsChannel) instanceof GuildMessageChannel messageChannel))
			{
				event.reply("Please select a message channel").setEphemeral(true).queue();
				return;
			}
			//TODO: complete /admin slow_mode
			event.reply("Under construction...").queue();
		}
	}
}