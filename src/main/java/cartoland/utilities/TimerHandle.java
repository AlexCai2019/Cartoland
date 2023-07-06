package cartoland.utilities;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @since 2.1
 * @author Alex Cai
 */
public final class TimerHandle
{
	private TimerHandle()
	{
		throw new AssertionError(IDAndEntities.YOU_SHALL_NOT_ACCESS);
	}

	private static final List<TimerEvent> timerEvents = new ArrayList<>();
	private static final String TEMP_BAN_LIST = "serialize/temp_ban_list.ser";
	@SuppressWarnings("unchecked")
	public static final Map<Long, Long> tempBanList = (FileHandle.deserialize(TEMP_BAN_LIST) instanceof HashMap map) ? map : new HashMap<>(); //userID為key ban time為value

	//https://stackoverflow.com/questions/65984126
	private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	private static byte nowHour = (byte) LocalTime.now().getHour(); //現在是幾點
	private static long hoursFrom1970 = System.currentTimeMillis() / (1000 * 60 * 60); //從1970年1月1日開始過了幾個小時
	private static final ScheduledFuture<?> everyHour = executorService.scheduleAtFixedRate(() ->
	{
		hoursFrom1970++;
		nowHour++;
		if (nowHour == 24)
			nowHour = 0;

		//根據現在的時間 決定是否解ban
		Set<Long> bannedIDs = new HashSet<>(tempBanList.keySet()); //建立新物件 以免修改到原map
		for (Long bannedID : bannedIDs)
		{
			if (tempBanList.get(bannedID) >= hoursFrom1970) //已經超過了它們要被解ban的時間
			{
				IDAndEntities.jda.retrieveUserById(bannedID).queue(user -> IDAndEntities.cartolandServer.unban(user).queue()); //找到這名使用者後解ban他
				tempBanList.remove(bannedID); //不再紀錄這名使用者
			}
		}

		for (TimerEvent event : timerEvents) //走訪被註冊的事件們
			if (event.shouldExecute(nowHour)) //時間到了
				event.execute(); //執行
	}, secondsUntil((nowHour + 1) % 24), 60 * 60, TimeUnit.SECONDS); //從下個小時開始

	static
	{
		FileHandle.registerSerialize(TEMP_BAN_LIST, tempBanList);
	}

	private static long secondsUntil(int hour)
	{
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime untilTime = now.withHour(hour).withMinute(0).withSecond(0);

		if (now.compareTo(untilTime) > 0)
			untilTime = untilTime.plusDays(1L);

		return Duration.between(now, untilTime).getSeconds();
	}

	public static void registerTimerEvent(byte hour, Runnable function)
	{
		if (0 <= hour && hour <= 23)
			timerEvents.add(new TimerEvent(hour, function));
		else
			throw new IllegalArgumentException("Hour must between 0 and 23!");
	}

	public static void stopTimer()
	{
		everyHour.cancel(true);
		executorService.shutdown();
	}

	/**
	 * @since 2.1
	 * @author Alex Cai
	 */
	private static class TimerEvent
	{
		private final byte hour;
		private final Runnable function;

		private TimerEvent(byte hour, Runnable function)
		{
			this.hour = hour;
			this.function = function;
		}

		private boolean shouldExecute(byte hour)
		{
			return this.hour == hour;
		}

		private void execute()
		{
			function.run();
		}
	}
}