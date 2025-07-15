package cartoland.commands;

import cartoland.Cartoland;
import cartoland.utilities.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.attribute.ISlowmodeChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * {@code AdminCommand} is an execution when a moderator uses /admin command. This class extends
 * {@link HasSubcommands} class which implements {@link ICommand} interface, which is for the commands HashMap in
 * {@link cartoland.events.CommandUsage}. This class doesn't handle sub commands, but call other classes to
 * deal with it.
 *
 * @since 2.1
 * @author Alex Cai
 */
public class AdminCommand extends HasSubcommands
{
	private static final Logger logger = LoggerFactory.getLogger(AdminCommand.class);

	private static final String TEMP_BAN_SET = "serialize/temp_ban_set.ser";
	public record BanData(long userID, long unbanTime, long bannedServerID) implements Serializable
	{
		@Serial
		private static final long serialVersionUID = 23_14069263277926900L;

		public void tryUnban()
		{
			if (TimerHandle.getHoursFrom1970() < unbanTime) //還沒到這個人要被解ban的時間
				return; //結束
			JDA jda = Cartoland.getJDA();
			Guild bannedServer = jda.getGuildById(bannedServerID); //找到當初ban他的群組
			if (bannedServer != null) //群組還在
				jda.retrieveUserById(userID).flatMap(bannedServer::unban).queue(); //找到這名使用者後解ban他
			tempBanSet.remove(this); //不再紀錄這名使用者 無論群組是否已經不在了
		}
	}
	@SuppressWarnings("unchecked")
	public static final Set<BanData> tempBanSet = CastToInstance.modifiableSet(FileHandle.deserialize(TEMP_BAN_SET));

	public static final String MUTE = "mute";
	public static final String TEMP_BAN = "temp_ban";
	public static final String SLOW_MODE = "slow_mode";
	public static final String CLEAR_MESSAGE = "clear_message";

	static
	{
		FileHandle.registerSerialize(TEMP_BAN_SET, tempBanSet); //註冊串聯化
	}

	public AdminCommand()
	{
		super(4);
		subcommands.put(MUTE, new MuteSubcommand());
		subcommands.put(TEMP_BAN, new TempBanSubcommand());
		subcommands.put(SLOW_MODE, new SlowModeSubcommand());
		subcommands.put(CLEAR_MESSAGE, new ClearMessageSubcommand());
	}

	/**
	 * Create a string from the value of a double without trailing zeros.
	 * <pre>
	 *     String s1 = Algorithm.cleanFPString("5.5"); //5.5
	 *     String s2 = Algorithm.cleanFPString("5.0"); //5
	 *     String s3 = Algorithm.cleanFPString("1.500"); //1.5
	 * </pre>
	 *
	 * @param fpString The string that are going to trim the trailing zeros.
	 * @return The string of duration without trailing zeros.
	 * @since 2.1
	 * @author Alex Cai
	 */
	private static String cleanFPString(String fpString)
	{
		int dotIndex = fpString.indexOf('.'); //小數點的索引
		int headOfTrailingZeros = fpString.length(); //將會是小數部分最後的一串0中 第一個0

		//從最後一個字元開始 一路往左到小數點
		int index;
		for (index = headOfTrailingZeros - 1; index >= dotIndex; index--) //開始時 headOfTrailingZeros為字串長度 index則為字串長度 - 1
		{
			if (fpString.charAt(index) != '0') //如果發現第一個不是0的數
			{
				headOfTrailingZeros = index + 1; //那就說明這個索引的右邊 一定末端連續0的第一個
				break; //找到了 結束
			}
		}

		//結果:
		//5.000000 => 5
		//1.500000 => 1.5
		//從第一個數字開始 一路到連續0的第一個 如果小數點後都是連續0 那就連小數點都不要了
		return fpString.substring(0, (index == dotIndex) ? dotIndex : headOfTrailingZeros); //經歷過for迴圈 此時index必定是連續0開頭的左邊那個
	}

	/**
	 * {@code MuteSubCommand} is a class that handles one of the subcommands of {@code /admin} command, which is
	 * {@code /admin mute}.
	 *
	 * @since 2.1
	 * @author Alex Cai
	 */
	private static class MuteSubcommand implements ICommand
	{
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

			if (!member.hasPermission(Permission.MODERATE_MEMBERS))
			{
				event.reply(JsonHandle.getString(userID, "admin.mute.no_permission")).setEphemeral(true).queue();
				return;
			}

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
			StringBuilder replyStringBuilder = new StringBuilder(JsonHandle.getString(userID, "admin.mute.success",
					target.getAsMention(), mutedTime, (System.currentTimeMillis() + durationMillis) / 1000));
			String reason = event.getOption("reason", OptionMapping::getAsString);
			if (reason != null) //有理由
				replyStringBuilder.append(JsonHandle.getString(userID, "admin.mute.reason", reason)); //加上理由

			event.reply(replyStringBuilder.toString()).queue();
			target.timeoutFor(Duration.ofMillis(durationMillis)).reason(reason).queue(); //執行禁言
			logger.info("{}({}) mute {}({}) {} {}", member.getUser().getName(), member.getId(), target.getUser().getName(), target.getId(), mutedTime, reason);
		}
	}

	/**
	 * {@code TempBanSubCommand} is a class that handles one of the subcommands of {@code /admin} command, which is
	 * {@code /admin temp_ban}.
	 *
	 * @since 2.1
	 * @author Alex Cai
	 */
	private static class TempBanSubcommand implements ICommand
	{
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

			double duration = event.getOption("duration", 0.0, OptionMapping::getAsDouble);
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
			tempBanSet.add(new BanData(target.getIdLong(), Algorithm.safeAdd(TimerHandle.getHoursFrom1970(), durationHours), guild.getIdLong()));
			//TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis())
			guild.ban(target, 0, TimeUnit.SECONDS).reason(reason + '\n' + bannedTime).queue();
			logger.info("{}({}) temp_ban {}({}) {} {}", member.getUser().getName(), member.getId(), target.getUser().getName(), target.getId(), bannedTime, reason);
		}
	}

	/**
	 * {@code SlowModeSubCommand} is a class that handles one of the subcommands of {@code /admin} command, which is
	 * {@code /admin slow_mode}.
	 *
	 * @since 2.1
	 * @author Alex Cai
	 */
	private static class SlowModeSubcommand implements ICommand
	{
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

			if (!member.hasPermission(Permission.MANAGE_CHANNEL))
			{
				event.reply(JsonHandle.getString(userID, "admin.slow_mode.no_permission")).setEphemeral(true).queue();
				return;
			}

			if (!(event.getOption("channel", OptionMapping::getAsChannel) instanceof ISlowmodeChannel channel)) //不是可以設慢速模式的頻道
			{
				event.reply(JsonHandle.getString(userID, "admin.slow_mode.wrong_channel")).setEphemeral(true).queue();
				return;
			}

			//可惜沒有getAsFloat
			float time = event.getOption("time", 0.0, OptionMapping::getAsDouble).floatValue(); //解包並轉float
			if (time < 0) //不能負時間 可以0 0代表取消慢速
			{
				event.reply(JsonHandle.getString(userID, "admin.slow_mode.time_must_be_no_negative")).setEphemeral(true).queue();
				return;
			}

			String unit = event.getOption("unit", "", OptionMapping::getAsString); //單位字串

			int timeSecond = Math.round(time * switch (unit) //將單位轉成秒
			{
				case "second" -> 1;
				case "minute" -> 60;
				case "quarter" -> 60 * 15;
				case "hour" -> 60 * 60;
				case "double_hour" -> 60 * 60 * 2;
				default -> 0;
			});
			if (timeSecond > ISlowmodeChannel.MAX_SLOWMODE) //不能超過6小時 21600秒
			{
				event.reply(JsonHandle.getString(userID, "admin.slow_mode.too_long", ISlowmodeChannel.MAX_SLOWMODE / (60 * 60)))
						.setEphemeral(true).queue();
				return;
			}

			String slowTime = cleanFPString(Float.toString(time)) + ' ' + JsonHandle.getString(userID, "admin.unit_" + unit);
			if (timeSecond > 0)
				event.reply(JsonHandle.getString(userID, "admin.slow_mode.success", channel.getAsMention(), slowTime)).queue();
			else //一定是等於0 前面過濾掉小於0的情況了
				event.reply(JsonHandle.getString(userID, "admin.slow_mode.cancel", channel.getAsMention())).queue();
			channel.getManager().setSlowmode(timeSecond).queue(); //設定慢速時間
		}
	}
}