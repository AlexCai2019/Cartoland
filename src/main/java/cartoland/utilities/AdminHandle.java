package cartoland.utilities;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class AdminHandle
{
	private AdminHandle()
	{
		throw new AssertionError(IDs.YOU_SHALL_NOT_ACCESS);
	}
	private static final long TWENTY_EIGHT_DAYS_MILLISECONDS = 1000L * 60 * 60 * 24 * 28;
	private static final String TEMP_BAN_LIST = "serialize/temp_ban_list.ser";

	//userID為value[0] ban time為value[1] ban guild為value[2]
	public static final Set<long[]> tempBanSet = (FileHandle.deserialize(TEMP_BAN_LIST) instanceof HashSet<?> set) ? set.stream()
			.map(o -> (long[])o).collect(Collectors.toSet()) : new HashSet<>();
	public static final byte USER_ID_INDEX = 0;
	public static final byte BANNED_TIME = 1;
	public static final byte BANNED_SERVER = 2;

	static
	{
		FileHandle.registerSerialize(TEMP_BAN_LIST, tempBanSet); //註冊串聯化
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
	 * This is a record that holds reply information for an event and whether it is ephemeral.
	 *
	 * @since 2.1
	 * @author Alex Cai
	 */
	public static record ReturnInformation(String replyMessage, boolean ephemeral) {}

	public static ReturnInformation mute(Member member, Member target, Double durationBox, String unit, String reason)
	{
		if (member == null)
			return null; //不可能

		long userID = member.getIdLong();

		if (!member.hasPermission(Permission.MODERATE_MEMBERS))
			return new ReturnInformation(JsonHandle.getStringFromJsonKey(userID, "admin.mute.no_permission"), true);

		if (target == null) //找不到要被禁言的成員
			return new ReturnInformation(JsonHandle.getStringFromJsonKey(userID, "admin.mute.no_member"), true);

		if (target.isOwner()) //無法禁言群主 會擲出HierarchyException
			return new ReturnInformation(JsonHandle.getStringFromJsonKey(userID, "admin.mute.can_t_owner"), true);
		if (target.isTimedOut()) //已經被禁言了
			return new ReturnInformation(JsonHandle.getStringFromJsonKey(userID, "admin.mute.already_timed_out"), true);

		if (durationBox == null)
			return null; //不可能
		double duration = durationBox;
		if (duration <= 0) //不能負時間
			return new ReturnInformation(JsonHandle.getStringFromJsonKey(userID, "admin.mute.duration_must_be_positive"), true);

		if (unit == null) //單位
			return null; //不可能

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
			return new ReturnInformation(JsonHandle.getStringFromJsonKey(userID, "admin.mute.too_long"), true);

		String mutedTime = buildDurationString(duration) + ' ' + JsonHandle.getStringFromJsonKey(userID, "admin.mute.unit_" + unit);
		String replyString = JsonHandle.getStringFromJsonKey(userID, "admin.mute.success")
				.formatted(target.getAsMention(), mutedTime, (System.currentTimeMillis() + durationMillis) / 1000);
		if (reason != null) //有理由
			replyString += JsonHandle.getStringFromJsonKey(userID, "admin.mute.reason").formatted(reason);

		target.timeoutFor(Duration.ofMillis(durationMillis)).reason(reason).queue();
		return new ReturnInformation(replyString, false);
	}

	public static ReturnInformation tempBan(Member member, Member target, Double durationBox, String unit, String reason)
	{
		if (member == null)
			return null;

		long userID = member.getIdLong();

		if (!member.hasPermission(Permission.BAN_MEMBERS))
			return new ReturnInformation(JsonHandle.getStringFromJsonKey(userID, "admin.temp_ban.no_permission"), true);

		if (target == null)
			return new ReturnInformation(JsonHandle.getStringFromJsonKey(userID, "admin.temp_ban.no_member"), true);
		if (target.isOwner()) //無法禁言群主 會擲出HierarchyException
			return new ReturnInformation(JsonHandle.getStringFromJsonKey(userID, "admin.temp_ban.can_t_owner"), true);

		if (durationBox == null)
			return null;
		double duration = durationBox;
		if (duration <= 0) //不能負時間
			return new ReturnInformation(JsonHandle.getStringFromJsonKey(userID, "admin.temp_ban.duration_too_short"), true);

		if (unit == null)
			return null;

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
			return new ReturnInformation(JsonHandle.getStringFromJsonKey(userID, "admin.temp_ban.duration_too_short"), true);

		String bannedTime = buildDurationString(duration) + ' ' + JsonHandle.getStringFromJsonKey(userID, "admin.temp_ban.unit_" + unit);
		String replyString = JsonHandle.getStringFromJsonKey(userID, "admin.temp_ban.success")
				.formatted(
						target.getAsMention(), bannedTime,
						System.currentTimeMillis() / 1000 + durationHours * 60 * 60); //直到<t:> 以秒為單位
		//TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + TimeUnit.HOURS.toSeconds(durationHours)
		if (reason != null)
			replyString += JsonHandle.getStringFromJsonKey(userID, "admin.temp_ban.reason").formatted(reason);

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
		return new ReturnInformation(replyString, false); //回覆
	}
}