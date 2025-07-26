package cartoland.utilities;

import cartoland.Cartoland;
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

	private static final ZoneId utc8 = ZoneId.of("UTC+8");

	private static final int STORE_YEAR = 2000; //用於在資料庫中佔位的年份

	private static final short HOURS = 24; //一天有24小時

	private static final String SCHEDULED_EVENTS = "serialize/scheduled_events.ser";

	@SuppressWarnings({"unchecked"}) //閉嘴IntelliJ IDEA
	private static final Set<Runnable>[] hourRunFunctions = new LinkedHashSet[HOURS]; //用LinkedHashSet確保訊息根據schedule的順序發送
	@SuppressWarnings("unchecked") //閉嘴IntelliJ IDEA
	private static final Map<String, TimerEvent> scheduledEvents = CastToInstance.modifiableMap(FileHandle.deserialize(SCHEDULED_EVENTS)); //timer event是匿名的 scheduled event是有名字的
	private static final Set<TimerEvent> toBeRemoved = new HashSet<>(); //不能直接在Runnable裡呼叫unregister

	static
	{
		FileHandle.registerSerialize(SCHEDULED_EVENTS, scheduledEvents);

		//初始化時間事件
		for (short i = 0; i < HOURS; i++)
			hourRunFunctions[i] = new LinkedHashSet<>();

		//半夜12點
		final byte zero = 0;
		TimerHandle.registerTimerEvent(new TimerEvent(zero, () -> //和生日有關的
		{
			LocalDate today = LocalDate.now(utc8);
			List<Long> birthdayMembersID = DatabaseHandle.readTodayBirthday(LocalDate.of(STORE_YEAR, today.getMonthValue(), today.getDayOfMonth())); //今天生日的成員們的ID
			if (birthdayMembersID.isEmpty()) //今天沒有人生日
				return;

			TextChannel lobbyChannel = Cartoland.getJDA().getTextChannelById(IDs.ZH_CHAT_CHANNEL_ID); //大廳頻道
			if (lobbyChannel == null) //找不到大廳頻道
				return;

			for (long birthdayMemberID : birthdayMembersID)
				lobbyChannel.sendMessage("今天是 <@" + Long.toUnsignedString(birthdayMemberID) + "> 的生日！\n").queue();
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
		for (MembersHandle.BannedUser user : DatabaseHandle.readAllBannedUsers())
			user.tryUnban(); //嘗試解ban

	}, Duration.between(LocalDateTime.now(utc8), LocalDateTime.now(utc8).withMinute(0).withSecond(0).plusHours(1L)).getSeconds(), 60 * 60, TimeUnit.SECONDS); //從下個小時開始

	public static void setBirthday(long userID, int month, int day)
	{
		if (month == 0 && day == 0) //刪除生日
			DatabaseHandle.writeBirthday(userID, null);
		else
			DatabaseHandle.writeBirthday(userID, LocalDate.of(STORE_YEAR, month, day));
	}

	public static LocalDate getBirthday(long userID)
	{
		return DatabaseHandle.readBirthday(userID); //查詢db的紀錄
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