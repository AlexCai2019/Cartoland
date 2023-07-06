package cartoland.commands;

import cartoland.utilities.CommonFunctions;
import cartoland.utilities.IDAndEntities;
import cartoland.utilities.JsonHandle;
import cartoland.utilities.TimerHandle;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

	private static String buildDurationString(double duration)
	{
		String durationString = Double.toString(duration); //將時間轉成字串
		int dotIndex = durationString.indexOf('.'); //小數點的索引
		int firstZero = durationString.length(); //尋找小數部分最後的一串0中 第一個0
		int i;
		for (i = firstZero - 1; i >= dotIndex; i--)
		{
			if (durationString.charAt(i) != '0')
			{
				firstZero = i + 1;
				break;
			}
		}
		//結果:
		//5.000000 => 5
		//1.500000 => 1.5
		return durationString.substring(0, (i == dotIndex) ? dotIndex : firstZero);
	}

	/**
	 * @since 2.1
	 * @author Alex Cai
	 */
	private static class MuteSubCommand implements ICommand
	{
		private static final long TWENTY_EIGHT_DAYS_MILLISECONDS = 1000L * 60 * 60 * 24 * 28;

		@Override
		public void commandProcess(SlashCommandInteractionEvent event)
		{
			long userID = event.getUser().getIdLong();
			Member target = event.getOption("target", CommonFunctions.getAsMember); //要被禁言的成員
			if (target == null) //找不到該成員
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

			String unit = event.getOption("unit", CommonFunctions.getAsString); //單位
			if (unit == null)
			{
				event.reply("Impossible, this is required!").queue();
				return;
			}

			//不用java.util.concurrent.TimeUnit 因為它不接受浮點數
			long durationMillis = (long) (duration * switch (unit) //將單位轉成毫秒 1000毫秒等於1秒
			{
				case "second" -> 1000;
				case "minute" -> 1000 * 60;
				case "hour" -> 1000 * 60 * 60;
				case "double_hour" -> 1000 * 60 * 60 * 2;
				case "day" -> 1000 * 60 * 60 * 24;
				case "week" -> 1000 * 60 * 60 * 24 * 7;
				default -> 1;
			});

			if (durationMillis <= 0 || durationMillis > TWENTY_EIGHT_DAYS_MILLISECONDS) //小於等於0 代表溢位了
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "admin.mute.too_long")).setEphemeral(true).queue();
				return;
			}

			String reason = event.getOption("reason", CommonFunctions.getAsString); //理由

			String replyString = JsonHandle.getStringFromJsonKey(userID, "admin.mute.success")
					.formatted(target.getAsMention(), buildDurationString(duration),
							   JsonHandle.getStringFromJsonKey(userID, "admin.mute.unit_" + unit),
							   (System.currentTimeMillis() + durationMillis) / 1000);
			if (reason != null)
				replyString += JsonHandle.getStringFromJsonKey(userID, "admin.mute.reason").formatted(reason);

			event.reply(replyString).queue();
			IDAndEntities.cartolandServer.timeoutFor(target, Duration.ofMillis(durationMillis)).reason(reason).queue();
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
			long userID = event.getUser().getIdLong();
			Member target = event.getOption("target", CommonFunctions.getAsMember);
			if (target == null)
			{
				event.reply("Impossible, this is required!").queue();
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
				event.reply(JsonHandle.getStringFromJsonKey(userID, "admin.temp_ban.duration_must_be_positive")).setEphemeral(true).queue();
				return;
			}

			String unit = event.getOption("unit", CommonFunctions.getAsString); //單位
			if (unit == null)
			{
				event.reply("Impossible, this is required!").queue();
				return;
			}

			long durationHours = (long) (duration * switch (unit) //將單位轉成小時
			{
				case "day" -> 24;
				case "week" -> 24 * 7;
				case "month" -> 24 * 30;
				case "season" -> 24 * 30 * 3;
				case "year" -> 24 * 365;
				case "wood_rat" -> 24 * 365 * 60;
				case "century" -> 24 * 365 * 100;
				default -> 1;
			});

			if (durationHours <= 0) //溢位
				durationHours = Long.MAX_VALUE;

			long untilPardon =  TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis()) + durationHours;
			if (untilPardon <= 0) //溢位
				untilPardon = Long.MAX_VALUE;

			TimerHandle.tempBanList.put(target.getIdLong(), untilPardon);

			String reason = event.getOption("reason", CommonFunctions.getAsString); //理由

			String replyString = JsonHandle.getStringFromJsonKey(userID, "admin.temp_ban.success")
					.formatted(target.getAsMention(), buildDurationString(duration),
							   JsonHandle.getStringFromJsonKey(userID, "admin.temp_ban.unit_" + unit),
							   TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + TimeUnit.HOURS.toSeconds(durationHours)); //直到<t:> 以秒為單位
			if (reason != null)
				replyString += JsonHandle.getStringFromJsonKey(userID, "admin.temp_ban.reason").formatted(reason);

			event.reply(replyString).queue();

			IDAndEntities.cartolandServer.ban(target, 0, TimeUnit.SECONDS).reason(reason).queue();
		}
	}
}