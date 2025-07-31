package cartoland.utilities;

import cartoland.Cartoland;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

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

	public static class TimerEvent implements Runnable
	{
		private final int hour;
		@Getter
		private final String name;
		private final Runnable function;
		private final boolean isSystem;
		@Setter
		private boolean once = false;

		//timer event: 泛指所有定時事件
		//scheduled event: 特指透過schedule指令新增的定時事件 又稱排程事件
		@SuppressWarnings({"unchecked"}) //閉嘴IntelliJ IDEA
		private static final Set<TimerEvent>[] allTimerEvents = new LinkedHashSet[HOURS]; //用LinkedHashSet確保訊息根據schedule的順序發送
		private static int scheduledEventsCount = 0; //scheduled event的數量
		@Getter private static long updatedTime = System.currentTimeMillis();
		static
		{
			//初始化時間事件
			for (short i = 0; i < HOURS; i++)
				allTimerEvents[i] = new LinkedHashSet<>();
		}

		public static List<TimerEvent> scheduledEvents()
		{
			if (scheduledEventsCount == 0) //如果沒有
				return Collections.emptyList();

			List<TimerEvent> events = new ArrayList<>(); //要回傳的 正被排程的事件
			for (Set<TimerEvent> timerEvents : allTimerEvents) //所有定時事件 包括系統事件
				for (TimerEvent timerEvent : timerEvents)
					if (!timerEvent.isSystem) //不可以回傳系統定時事件
						events.add(timerEvent); //只儲存排程事件
			return events;
		}

		public TimerEvent(int hour, String name, String contents, long channelID)
		{
			this(hour, name, () ->
			{
				MessageChannel channel = Cartoland.getJDA().getChannelById(MessageChannel.class, channelID); //尋找頻道
				if (channel != null) //如果找到頻道
					channel.sendMessage(contents).queue(); //發送訊息
			}, false); //不是系統的一部份 可以被清除

			DatabaseHandle.writeScheduledEvent(hour, name, contents, channelID);
		}

		private TimerEvent(int hour, String name, Runnable function, boolean isSystem)
		{
			this.hour = hour; //執行時間
			this.name = name; //註冊名稱
			this.function = function; //執行函數
			this.isSystem = isSystem; //是否為系統
		}

		public void register()
		{
			allTimerEvents[hour].add(this); //記錄下這個小時要跑這個
			if (isSystem)
				return;
			scheduledEventsCount++; //數量 + 1
			updatedTime = System.currentTimeMillis(); //最後一次更新的時間
		}

		@Override
		public void run()
		{
			function.run();
			if (once)
				unregister();
		}

		public void unregister()
		{
			allTimerEvents[hour].remove(this); //不再需要跑這個
			if (isSystem)
				return;
			scheduledEventsCount--; //數量 - 1
			updatedTime = System.currentTimeMillis(); //最後一次更新的時間
		}
	}

	private static final ZoneId utc8 = ZoneId.of("UTC+8");

	private static final int STORE_YEAR = 2000; //用於在資料庫中佔位的年份

	private static final short HOURS = 24; //一天有24小時

	//https://stackoverflow.com/questions/65984126
	private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	private static final ScheduledFuture<?> everyHour = executorService.scheduleAtFixedRate(() -> //每小時執行一次
	{
		for (TimerEvent event : TimerEvent.allTimerEvents[LocalTime.now(utc8).getHour()]) //根據現在是幾點 走訪被註冊的事件們
			event.run(); //執行

		//根據現在的時間 決定是否解ban
		for (MembersHandle.BannedUser user : DatabaseHandle.readAllBannedUsers())
			user.tryUnban(); //嘗試解ban

		ForumChannel questionsChannel = Cartoland.getJDA().getForumChannelById(IDs.QUESTIONS_CHANNEL_ID); //疑難雜症頻道
		if (questionsChannel == null) //找不到就算了
			return;
		for (ThreadChannel forumPost : questionsChannel.getThreadChannels()) //走訪論壇貼文們
			QuestionForumHandle.getInstance(forumPost).remind(); //試著提醒
	}, Duration.between(LocalDateTime.now(utc8), LocalDateTime.now(utc8).withMinute(0).withSecond(0).plusHours(1L)).getSeconds(), 60 * 60, TimeUnit.SECONDS); //從下個小時開始

	public static long getHoursFrom1970()
	{
		return System.currentTimeMillis() / (1000 * 60 * 60); //從1970年1月1日開始過了幾個小時
	}

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

	public static void startTimer()
	{
		JDA jda = Cartoland.getJDA();

		//半夜12點
		new TimerEvent(0, "zero", () -> //和生日有關的
		{
			LocalDate today = LocalDate.now(utc8);
			List<Long> birthdayMembersID = DatabaseHandle.readTodayBirthday(LocalDate.of(STORE_YEAR, today.getMonthValue(), today.getDayOfMonth())); //今天生日的成員們的ID
			if (birthdayMembersID.isEmpty()) //今天沒有人生日
				return;

			TextChannel lobbyChannel = jda.getTextChannelById(IDs.ZH_CHAT_CHANNEL_ID); //大廳頻道
			if (lobbyChannel == null) //找不到大廳頻道
				return;

			for (long birthdayMemberID : birthdayMembersID)
				lobbyChannel.sendMessage("今天是 <@" + Long.toUnsignedString(birthdayMemberID) + "> 的生日！\n").queue();
		}, true).register();

		//凌晨3點
		new TimerEvent(3, "three", () -> //好棒 三點了
		{
			TextChannel undergroundChannel = jda.getTextChannelById(IDs.UNDERGROUND_CHANNEL_ID);
			if (undergroundChannel == null) //找不到地下頻道
				return; //結束
			undergroundChannel.sendMessage("https://i.imgur.com/nWkSB2G.jpg").queue(); //誰會想在凌晨三點吃美味蟹堡
			undergroundChannel.sendMessage("https://i.imgur.com/gF69EIo.jpg").queue(); //好棒，三點了
		}, true).register();

		for (TimerEvent scheduledEvent : DatabaseHandle.readAllScheduledEvents()) //所有資料庫內儲存的排程事件
			scheduledEvent.register(); //註冊
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