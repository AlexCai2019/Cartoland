package cartoland.commands;

import cartoland.utilities.CommonFunctions;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.time.Duration;
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
	}

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		subCommands.get(event.getSubcommandName()).commandProcess(event);
	}
}

/**
 * @since 2.1
 * @author Alex Cai
 */
class MuteSubCommand implements ICommand
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

		String durationString = Double.toString(duration);
		int dotIndex = durationString.indexOf('.');
		int firstZero = durationString.length();
		int i;
		for (i = firstZero - 1; i >= dotIndex; i--)
		{
			if (durationString.charAt(i) != '0')
			{
				firstZero = i + 1;
				break;
			}
		}
		durationString = durationString.substring(0, (i == dotIndex) ? dotIndex : firstZero);

		String reason = event.getOption("reason", CommonFunctions.getAsString); //理由
		String replyString = JsonHandle.getStringFromJsonKey(userID, "admin.mute.success")
				.formatted(target.getAsMention(), durationString, JsonHandle.getStringFromJsonKey(userID, "admin.mute.unit_" + unit));
		if (reason != null)
			replyString += JsonHandle.getStringFromJsonKey(userID, "admin.mute.reason").formatted(reason);

		event.reply(replyString).queue();
		target.timeoutFor(Duration.ofMillis(durationMillis)).reason(reason).queue();
	}
}