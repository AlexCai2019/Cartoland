package cartoland.commands;

import cartoland.utilities.IStringHelper;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class MuteCommand implements ICommand, IStringHelper
{
	private static final Logger logger = LoggerFactory.getLogger(MuteCommand.class);

	private static final long MAX_TIME_OUT_LENGTH_MILLIS = 1000L * 60 * 60 * 24 * Member.MAX_TIME_OUT_LENGTH;

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		Member member = event.getMember(); //使用指令的成員
		if (member == null)
		{
			event.reply("Impossible, this is required!").queue();
			return;
		}

		long userID = member.getIdLong(); //使用指令的成員ID

		Member target = event.getOption("target", OptionMapping::getAsMember); //要被禁言的目標
		if (target == null) //找不到要被禁言的成員
		{
			event.reply(JsonHandle.getString(userID, "admin.mute.no_member")).setEphemeral(true).queue();
			return;
		}

		if (target.isOwner()) //無法禁言群主 會擲出HierarchyException
		{
			event.reply(JsonHandle.getString(userID, "admin.mute.can_t_owner")).setEphemeral(true).queue();
			return;
		}
		if (target.isTimedOut()) //已經被禁言了
		{
			event.reply(JsonHandle.getString(userID, "admin.mute.already_timed_out")).setEphemeral(true).queue();
			return;
		}

		double duration = event.getOption("duration", 0.0, OptionMapping::getAsDouble);
		String unit = event.getOption("unit", "", OptionMapping::getAsString);

		//不用java.util.concurrent.TimeUnit 因為它不接受浮點數
		long durationMillis = Math.round(duration * switch (unit) //將單位轉成毫秒 1000毫秒等於1秒
		{
			case "second" -> 1000;
			case "minute" -> 1000 * 60;
			case "quarter" -> 1000 * 60 * 15;
			case "hour" -> 1000 * 60 * 60;
			case "double_hour" -> 1000 * 60 * 60 * 2;
			case "day" -> 1000 * 60 * 60 * 24;
			case "week" -> 1000 * 60 * 60 * 24 * 7;
			default -> 1; //millisecond
		}); //Math.round會處理溢位

		if (durationMillis <= 0) //不能負時間
		{
			event.reply(JsonHandle.getString(userID, "admin.mute.duration_must_be_positive")).setEphemeral(true).queue();
			return;
		}

		if (durationMillis > MAX_TIME_OUT_LENGTH_MILLIS) //不能禁言超過28天
		{
			event.reply(JsonHandle.getString(userID, "admin.mute.too_long", Member.MAX_TIME_OUT_LENGTH)).setEphemeral(true).queue();
			return;
		}

		String mutedTime = cleanFPString(Double.toString(duration)) + ' ' + JsonHandle.getString(userID, "admin.unit_" + unit);
		String success = JsonHandle.getString(userID, "admin.mute.success",
				target.getAsMention(), mutedTime, (System.currentTimeMillis() + durationMillis) / 1000);

		StringBuilder replyBuilder = new StringBuilder(success);
		String reason = event.getOption("reason", OptionMapping::getAsString);
		if (reason != null) //有理由
			replyBuilder.append(JsonHandle.getString(userID, "admin.mute.reason", reason)); //加上理由
		event.reply(replyBuilder.toString()).queue();

		target.timeoutFor(Duration.ofMillis(durationMillis)).reason(reason).queue(); //執行禁言
		logger.info("{}({}) mute {}({}) {} {}", member.getUser().getName(), member.getId(), target.getUser().getName(), target.getId(), mutedTime, reason);
	}
}