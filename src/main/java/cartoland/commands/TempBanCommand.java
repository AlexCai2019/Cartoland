package cartoland.commands;

import cartoland.methods.IStringHelper;
import cartoland.utilities.*;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class TempBanCommand implements ICommand, IStringHelper
{
	private static final Logger logger = LoggerFactory.getLogger(TempBanCommand.class);

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

		if (!member.hasPermission(Permission.BAN_MEMBERS))
		{
			event.reply(JsonHandle.getString(userID, "admin.temp_ban.no_permission")).setEphemeral(true).queue();
			return;
		}

		Member target = event.getOption("target", OptionMapping::getAsMember);
		if (target == null)
		{
			event.reply(JsonHandle.getString(userID, "admin.temp_ban.no_member")).setEphemeral(true).queue();
			return;
		}
		if (target.isOwner()) //無法禁言群主 會擲出HierarchyException
		{
			event.reply(JsonHandle.getString(userID, "admin.temp_ban.can_t_owner")).setEphemeral(true).queue();
			return;
		}

		double duration = event.getOption("unbanTime", 0.0, OptionMapping::getAsDouble);
		String unit = event.getOption("unit", "", OptionMapping::getAsString);

		long durationHours = Math.round(duration * switch (unit) //將單位轉成小時
		{
			case "double_hour" -> 2;
			case "day" -> 24;
			case "week" -> 24 * 7;
			case "month" -> 24 * 30;
			case "season" -> 24 * 30 * 3;
			case "year" -> 24 * 365;
			case "decade" -> 24 * 365 * 10;
			case "wood_rat" -> 24 * 365 * 60;
			case "century" -> 24 * 365 * 100;
			default -> 1; //其實unit一定等於上述那些或hour 但是default必須要有
		}); //Math.round會處理溢位

		if (durationHours < 1L) //時間不能小於一小時
		{
			event.reply(JsonHandle.getString(userID, "admin.temp_ban.duration_too_short")).setEphemeral(true).queue();
			return;
		}

		String bannedTime = cleanFPString(Double.toString(duration)) + ' ' + JsonHandle.getString(userID, "admin.unit_" + unit);
		StringBuilder replyStringBuilder = new StringBuilder(JsonHandle.getString(userID, "admin.temp_ban.success",
				target.getAsMention(), bannedTime, System.currentTimeMillis() / 1000 + durationHours * 60 * 60)); //直到<t:> 以秒為單位
		//TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + TimeUnit.HOURS.toSeconds(durationHours)

		String reason = event.getOption("reason", OptionMapping::getAsString);
		if (reason != null)
			replyStringBuilder.append(JsonHandle.getString(userID, "admin.temp_ban.reason", reason));

		event.reply(replyStringBuilder.toString()).queue(); //回覆

		//回覆完再開始動作 避免超過三秒限制

		Guild guild = target.getGuild();
		//紀錄被ban的人的ID, 解除時間, 群組
		new MembersHandle.BannedUser(target.getIdLong(), Algorithm.safeAdd(TimerHandle.getHoursFrom1970(), durationHours), guild.getIdLong()).tempBan();
		//TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis())
		guild.ban(target, 0, TimeUnit.SECONDS).reason(reason + '\n' + bannedTime).queue();
		logger.info("{}({}) temp_ban {}({}) {} {}", member.getUser().getName(), member.getId(), target.getUser().getName(), target.getId(), bannedTime, reason);
	}
}