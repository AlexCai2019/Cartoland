package cartoland.utilities;

import cartoland.Cartoland;
import cartoland.commands.AdminCommand;
import lombok.Getter;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * {@code TimerHandle} is a utility class that handles schedule. Including checking if functions should execute in every
 * hour or handle {@code /admin temp_ban} with scheduled service. Can not be instantiated or inherited.
 *
 * @since 2.1
 * @author Alex Cai
 */
public final class TimerHandle
{
	private TimerHandle()
	{
		throw new AssertionError(IDs.YOU_SHALL_NOT_ACCESS);
	}

	public record TimerEvent(byte hour, Runnable function) implements Serializable
	{
		@Serial
		private static final long serialVersionUID = 2_718281828459045235L;
	}

	public record Birthday(byte month, byte date) implements Serializable
	{
		@Serial
		private static final long serialVersionUID = 6022140760000000000L;

		private static final Birthday[] cache = new Birthday[DAYS];

		static
		{
			byte month = 1, date = 1, days = daysInMonth(month);
			for (int i = 0; i < DAYS; i++)
			{
				cache[i] = new Birthday(month, date); //建立快取
				date++;
				if (date <= days) //還沒到月底
					continue; //下一個快取
				month++; //下個月
				date = 1; //回到1號
				days = daysInMonth(month); //查看這個月有幾天
			}
		}

		public static Birthday valueOf(int month, int date)
		{
			return cache[getDateOfYear(month, date) - 1]; //記住陣列的索引是從0開始
		}

		@Override
		public int hashCode()
		{
			return (month << 5) + date;
		}
	}

	private static final ZoneId utc8 = ZoneId.of("UTC+8");

	private static final short DAYS = 366; //一年有366天
	private static final short HOURS = 24; //一天有24小時

	private static final String BIRTHDAY_MAP = "serialize/birthday_map.ser";
	private static final String SCHEDULED_EVENTS = "serialize/scheduled_events.ser";

	@SuppressWarnings("unchecked") //閉嘴IntelliJ IDEA
	private static final Map<Long, Birthday> idToBirthday = CastToInstance.modifiableMap(FileHandle.deserialize(BIRTHDAY_MAP));
	private static final Map<Birthday, Set<Long>> birthdayToIDs = HashMap.newHashMap(DAYS);

	@SuppressWarnings({"unchecked"}) //閉嘴IntelliJ IDEA
	private static final Set<Runnable>[] hourRunFunctions = new LinkedHashSet[HOURS]; //用LinkedHashSet確保訊息根據schedule的順序發送
	@SuppressWarnings("unchecked") //閉嘴IntelliJ IDEA
	private static final Map<String, TimerEvent> scheduledEvents = CastToInstance.modifiableMap(FileHandle.deserialize(SCHEDULED_EVENTS)); //timer event是匿名的 scheduled event是有名字的
	private static final Set<TimerEvent> toBeRemoved = new HashSet<>(); //不能直接在Runnable裡呼叫unregister

	static
	{
		FileHandle.registerSerialize(BIRTHDAY_MAP, idToBirthday);
		FileHandle.registerSerialize(SCHEDULED_EVENTS, scheduledEvents);

		for (int i = 0 ; i < DAYS; i++)
			birthdayToIDs.put(Birthday.cache[i], new HashSet<>()); //準備366天份的HashSet

		//生日
		for (Map.Entry<Long, Birthday> idAndBirthday : idToBirthday.entrySet())
			birthdayToIDs.get(idAndBirthday.getValue()).add(idAndBirthday.getKey());

		//初始化時間事件
		for (short i = 0; i < HOURS; i++)
			hourRunFunctions[i] = new LinkedHashSet<>();

		//半夜12點
		final byte zero = 0;
		TimerHandle.registerTimerEvent(new TimerEvent(zero, () -> //和生日有關的
		{
			LocalDate today = LocalDate.now(utc8);
			Set<Long> birthdayMembersID = birthdayToIDs.get(Birthday.valueOf(today.getMonthValue(), today.getDayOfMonth())); //今天生日的成員們的ID
			if (birthdayMembersID.isEmpty()) //今天沒有人生日
				return;
			TextChannel lobbyChannel = Cartoland.getJDA().getTextChannelById(IDs.LOBBY_CHANNEL_ID); //大廳頻道
			if (lobbyChannel == null) //找不到大廳頻道
				return;
			for (long birthdayMemberID : birthdayMembersID)
				lobbyChannel.sendMessage("今天是 <@" + Long.toUnsignedString(birthdayMemberID) + "> 的生日！").queue();
		}));

		final byte three = 3;
		//凌晨3點
		TimerHandle.registerTimerEvent(new TimerEvent(three, () -> //好棒 三點了
		{
			TextChannel undergroundChannel = Cartoland.getJDA().getTextChannelById(IDs.UNDERGROUND_CHANNEL_ID);
			if (undergroundChannel == null) //找不到地下頻道
				return; //結束
			undergroundChannel.sendMessage("https://i.imgur.com/nWkSB2G.jpg").queue(); //誰會想在凌晨三點吃美味蟹堡
			undergroundChannel.sendMessage("https://i.imgur.com/gF69EIo.jpg").queue(); //好棒，三點了
		}));
	}

	//https://stackoverflow.com/questions/65984126
	private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	private static int nowHour = LocalTime.now(utc8).getHour(); //現在是幾點
	@Getter
	private static long hoursFrom1970 = System.currentTimeMillis() / (1000 * 60 * 60); //從1970年1月1日開始過了幾個小時
	private static final ScheduledFuture<?> everyHour = executorService.scheduleAtFixedRate(() -> //每小時執行一次
	{
		hoursFrom1970++; //增加小時
		nowHour++; //增加現在幾點
		if (nowHour == HOURS) //第24小時是0點
			nowHour = 0;

		if (!toBeRemoved.isEmpty()) //有事件要被移除
		{
			for (TimerEvent removeTimerEvent : toBeRemoved) //移除要被移除的事件們
				hourRunFunctions[removeTimerEvent.hour].remove(removeTimerEvent.function);
			toBeRemoved.clear(); //清空將被移除事件的紀錄
		}

		for (Runnable event : hourRunFunctions[nowHour]) //走訪被註冊的事件們
			event.run(); //執行

		ForumChannel questionsChannel = Cartoland.getJDA().getForumChannelById(IDs.QUESTIONS_CHANNEL_ID); //疑難雜症頻道
		if (questionsChannel != null) //找不到就算了
			for (ThreadChannel forumPost : questionsChannel.getThreadChannels()) //走訪論壇貼文們
				QuestionForumHandle.getInstance(forumPost).remind(); //試著提醒

		//根據現在的時間 決定是否解ban
		if (AdminCommand.tempBanSet.isEmpty()) //沒有人被temp_ban
			return; //不用執行
		for (AdminCommand.BanData bannedMember : new HashSet<>(AdminCommand.tempBanSet)) //建立新物件 以免修改到原set
			bannedMember.tryUnban(); //嘗試解ban

	}, secondsUntil((nowHour + 1) % HOURS), 60 * 60, TimeUnit.SECONDS); //從下個小時開始

	public static void setBirthday(long userID, int month, int date)
	{
		Birthday newBirthday = Birthday.valueOf(month, date); //新生日
		birthdayToIDs.get(newBirthday).add(userID); //將該使用者增加到那天生日的清單中
		Birthday oldBirthday = idToBirthday.put(userID, newBirthday); //設定使用者的生日 並同時獲取舊生日
		if (oldBirthday != null) //如果確實設定過舊生日
			birthdayToIDs.get(oldBirthday).remove(userID); //移除設定
	}

	public static Birthday getBirthday(long userID)
	{
		return idToBirthday.get(userID); //查詢map的紀錄
	}

	public static void deleteBirthday(long userID)
	{
		Birthday oldBirthday = idToBirthday.remove(userID); //移除舊生日 並把移除掉的值存起來
		if (oldBirthday != null) //如果設定過舊生日
			birthdayToIDs.get(oldBirthday).remove(userID); //從記錄中移除這位成員
	}

	/**
	 * Get date of year (start with 1). This method always assume the year is a leap year, hence February has
	 * 29 days. The value range of the method is from 1 to 366.
	 *
	 * @param month The month
	 * @param date The day of the month
	 * @return The day of year. Range: 1 ~ 366
	 * @since 2.1
	 * @author Alex Cai
	 */
	private static int getDateOfYear(int month, int date)
	{
		return switch (month) //需要幾天才會抵達這個月 從0開始
		{
			default -> 0;
			case 2 -> 31;
			case 3 -> 31 + 29;
			case 4 -> 31 + 29 + 31;
			case 5 -> 31 + 29 + 31 + 30;
			case 6 -> 31 + 29 + 31 + 30 + 31;
			case 7 -> 31 + 29 + 31 + 30 + 31 + 30;
			case 8 -> 31 + 29 + 31 + 30 + 31 + 30 + 31;
			case 9 -> 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31;
			case 10 -> 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 30;
			case 11 -> 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31;
			case 12 -> 31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31 + 30;
		} + date;
	}

	private static byte daysInMonth(byte month)
	{
		return switch (month)
		{
			case 4, 6, 9, 11 -> 30;
			case 2 -> 29;
			default -> 31;
		};
	}

	private static long secondsUntil(int hour)
	{
		LocalDateTime now = LocalDateTime.now(utc8); //現在的時間
		LocalDateTime untilTime = now.withHour(hour).withMinute(0).withSecond(0); //目標時間

		//如果現在的小時已經超過了目標的小時 例如要在3點時執行 但現在的時間已經4點了 那就明天再執行 否則今天就可執行
		return Duration.between(now, now.isAfter(untilTime) ? untilTime.plusDays(1L) : untilTime).getSeconds();
	}

	private static void registerTimerEvent(TimerEvent timerEvent)
	{
		hourRunFunctions[timerEvent.hour].add(timerEvent.function);
	}

	public static void registerScheduledEvent(String name, TimerEvent timerEvent)
	{
		registerTimerEvent(timerEvent);
		scheduledEvents.put(name, timerEvent);
	}

	public static boolean hasScheduledEvent(String name)
	{
		return scheduledEvents.containsKey(name);
	}

	public static Set<String> scheduledEventsNames()
	{
		return scheduledEvents.keySet();
	}

	public static void unregisterTimerEvent(TimerEvent timerEvent)
	{
		toBeRemoved.add(timerEvent);
	}

	public static void unregisterScheduledEvent(String name)
	{
		unregisterTimerEvent(scheduledEvents.remove(name));
	}

	/**
	 * Stop the {@link #everyHour} timer. This method will be called in
	 * {@link cartoland.events.BotOnlineOffline#onShutdown} when the bot went offline.
	 *
	 * @since 2.1
	 * @author Alex Cai
	 */
	public static void stopTimer()
	{
		//https://stackoverflow.com/questions/34202701
		everyHour.cancel(true);
		executorService.shutdown();
	}

	public static int[] getTime()
	{
		LocalTime now = LocalTime.now(utc8); //現在
		return new int[] {now.getHour(), now.getMinute(), now.getSecond()};
	}

	public static String getDateString()
	{
		return LocalDate.now(utc8).toString();
	}
}