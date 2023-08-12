package cartoland.utilities;

import cartoland.Cartoland;
import cartoland.commands.AdminCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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

	private static final List<TimerEvent> timerEvents = new ArrayList<>();

	private static final String BIRTHDAY_MAP = "serialize/birthday_map.ser";
	private static final String BIRTHDAY_ARRAY = "serialize/birthday_array.ser";

	@SuppressWarnings("unchecked") //閉嘴IntelliJ IDEA
	private static final Map<Long, Short> birthdayMap = (FileHandle.deserialize(BIRTHDAY_MAP) instanceof Map map) ? map : new HashMap<>();
	@SuppressWarnings({"unchecked","rawtypes"}) //閉嘴IntelliJ IDEA
	private static final List<Long>[] birthdayArray = (FileHandle.deserialize(BIRTHDAY_ARRAY) instanceof ArrayList[] array) ? array : new ArrayList[366];

	static
	{
		if (birthdayArray[0] == null) //如果從不存在紀錄
		{
			for (int i = 0; i < 366; i++)
				birthdayArray[i] = new ArrayList<>();
			birthdayMap.clear(); //一切紀錄重來
		}

		FileHandle.registerSerialize(BIRTHDAY_MAP, birthdayMap);
		FileHandle.registerSerialize(BIRTHDAY_ARRAY, birthdayArray);

		TimerHandle.registerTimerEvent((byte) 0, () -> //半夜12點
		{
			FileHandle.changeLogDate(); //更換log的日期

			//這以下是和生日有關的
			List<Long> birthdayMembersID = TimerHandle.todayBirthdayMembers(); //今天生日的成員們的ID
			if (birthdayMembersID.isEmpty()) //今天沒有人生日
				return;
			TextChannel lobbyChannel = Cartoland.getJDA().getTextChannelById(IDs.LOBBY_CHANNEL_ID); //大廳頻道
			if (lobbyChannel == null) //找不到大廳頻道
				return;
			int birthdayMembersCount = birthdayMembersID.size(); //今天生日的人數
			if (birthdayMembersCount <= 100) //小於等於100人
			{
				birthdayMembers(birthdayMembersID, lobbyChannel); //直接放下去跑就好
				return;
			}

			int membersRange;
			for (membersRange = 0; membersRange + 100 < birthdayMembersCount; membersRange += 100) //一次只能100人
				birthdayMembers(birthdayMembersID.subList(membersRange, membersRange + 100), lobbyChannel); //每次取100個
			birthdayMembers(birthdayMembersID.subList(membersRange, birthdayMembersCount), lobbyChannel); //最後不滿100人
		});

		TimerHandle.registerTimerEvent((byte) 3, () -> //凌晨3點
		{
			TextChannel undergroundChannel = Cartoland.getJDA().getTextChannelById(IDs.UNDERGROUND_CHANNEL_ID);
			if (undergroundChannel == null)
				return;
			undergroundChannel.sendMessage("https://i.imgur.com/c0HCirP.jpg").queue(); //誰會想在凌晨三點吃美味蟹堡
			undergroundChannel.sendMessage("https://i.imgur.com/EGO35hf.jpg").queue(); //好棒，三點了
		}); //好棒 三點了

		TimerHandle.registerTimerEvent((byte) 12, () -> //中午12點
		{
			ForumChannel questionsChannel = Cartoland.getJDA().getForumChannelById(IDs.QUESTIONS_CHANNEL_ID);
			if (questionsChannel == null)
				return;
			List<ThreadChannel> forumPosts = questionsChannel.getThreadChannels(); //論壇貼文們
			for (ThreadChannel forumPost : forumPosts) //走訪論壇貼文們
				ForumsHandle.tryIdleQuestionForumPost(forumPost); //試著讓它們idle
		}); //中午十二點時處理並提醒未解決的論壇貼文
	}

	private static final int MAX_MEMBERS_AT_ONCE = 60; //由於ID最長應該是18446744073709551615 因此每個人最多會占用33個字元 而Discord一次輸入的上限是2000字 33 * 60 = 1980

	private static void birthdayMembers(List<Long> birthdayMembersIDs, TextChannel lobbyChannel)
	{
		lobbyChannel.getGuild().retrieveMembersByIds(birthdayMembersIDs).onSuccess(members -> //獲取所有的生日成員們
		{
			StringBuilder builder = new StringBuilder();
			int membersCount = members.size();
			//最後把剛剛沒說的寄出去
			if (membersCount <= MAX_MEMBERS_AT_ONCE)
			{
				for (Member member : members) //走訪所有成員們
					builder.append("今天是 ").append(member.getAsMention()).append(" 的生日！\n");
				lobbyChannel.sendMessage(builder).queue(); //將生日祝賀合併為一則訊息
				return; //提前結束
			}

			//這以下是人數超過60人的應對方法
			for (int i = 0, j = 0; i < membersCount; i++, j++)
			{
				if (j == MAX_MEMBERS_AT_ONCE) //當到第61人時 先把第1到第60人寄出 但陣列是從0開始的
				{
					lobbyChannel.sendMessage(builder).complete(); //先送出一次生日祝賀 要等它完成後才能重設builder
					builder.setLength(0); //重設builder
					j = 0;
				}
				builder.append("今天是 ").append(members.get(i).getAsMention()).append(" 的生日！\n");
			}
			lobbyChannel.sendMessage(builder).queue(); //把剛剛有累積到 不滿26人的寄出
		});
	}

	//https://stackoverflow.com/questions/65984126
	private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	private static byte nowHour = (byte) LocalTime.now().getHour(); //現在是幾點
	private static long hoursFrom1970 = System.currentTimeMillis() / (1000 * 60 * 60); //從1970年1月1日開始過了幾個小時
	private static final ScheduledFuture<?> everyHour = executorService.scheduleAtFixedRate(() -> //每小時執行一次
	{
		hoursFrom1970++;
		nowHour++;
		if (nowHour == 24)
			nowHour = 0;

		for (TimerEvent event : timerEvents) //走訪被註冊的事件們
			if (event.shouldExecute(nowHour)) //時間到了
				event.execute(); //執行

		unbanMembers();
	}, secondsUntil((nowHour + 1) % 24), 60 * 60, TimeUnit.SECONDS); //從下個小時開始

	public static long getHoursFrom1970()
	{
		return hoursFrom1970;
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
	private static short getDateOfYear(int month, int date)
	{
		return (short) (switch (month) //一年中的第幾天(以0開始)
		{
			case 1 -> 0;
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
			default -> throw new IllegalArgumentException("Month must between 1 and 12!");
		} + date);
	}

	private static long secondsUntil(int hour)
	{
		if (hour < 0 || hour > 23)
			throw new IllegalArgumentException("Hour must between 0 and 23!");
		LocalDateTime now = LocalDateTime.now(); //現在的時間
		LocalDateTime untilTime = now.withHour(hour).withMinute(0).withSecond(0); //目標時間

		if (now.compareTo(untilTime) > 0) //如果現在的小時已經超過了目標的小時 例如要在3點時執行 但現在的時間已經4點了
			untilTime = untilTime.plusDays(1L); //明天再執行

		return Duration.between(now, untilTime).getSeconds();
	}

	private static void unbanMembers()
	{
		//根據現在的時間 決定是否解ban
		Set<long[]> tempBanSet = AdminCommand.tempBanSet;
		if (tempBanSet.size() == 0) //沒有人被temp_ban
			return; //不用執行
		//這以下是有關解ban的程式碼
		Set<long[]> bannedMembers = new HashSet<>(tempBanSet); //建立新物件 以免修改到原set
		JDA jda = Cartoland.getJDA();
		for (long[] bannedMember : bannedMembers)
		{
			if (hoursFrom1970 < bannedMember[AdminCommand.BANNED_TIME]) //還沒到這個人要被解ban的時間
				continue; //下面一位
			Guild bannedServer = jda.getGuildById(bannedMember[AdminCommand.BANNED_SERVER]); //找到當初ban他的群組
			if (bannedServer != null) //群組還在
				jda.retrieveUserById(bannedMember[AdminCommand.USER_ID_INDEX]) //找到這名使用者後解ban他
						.queue(user -> bannedServer.unban(user).queue()); //解ban
			AdminCommand.tempBanSet.remove(bannedMember); //不再紀錄這名使用者
		}
	}

	public static void registerTimerEvent(byte hour, Runnable function)
	{
		if (hour < 0 || hour > 23)
			throw new IllegalArgumentException("Hour must between 0 and 23!");
		timerEvents.add(new TimerEvent(hour, function)); //註冊一個特定小時會發生的事件
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

	static String getTimeString()
	{
		LocalTime now = LocalTime.now(); //現在
		return String.format("%02d:%02d:%02d", now.getHour(), now.getMinute(), now.getSecond());
	}

	public static String getDateString()
	{
		return LocalDate.now().toString();
	}

	public static List<Long> todayBirthdayMembers()
	{
		LocalDate today = LocalDate.now();
		return birthdayArray[getDateOfYear(today.getMonthValue(), today.getDayOfMonth()) - 1];
	}

	public static void setBirthday(long userID, int month, int date)
	{
		Short oldBirthday = birthdayMap.get(userID); //獲取舊生日
		if (oldBirthday != null) //如果確實設定過舊生日
			birthdayArray[oldBirthday].remove(userID); //移除設定
		short dateOfYear = getDateOfYear(month, date); //一年中的第幾天 1月1號為1 12月31號為366
		birthdayArray[dateOfYear - 1].add(userID); //將該使用者增加到那天生日的清單中
		birthdayMap.put(userID, dateOfYear); //設定使用者的生日
	}

	public static void deleteBirthday(long memberID)
	{
		Short oldBirthday = birthdayMap.remove(memberID); //移除舊生日 並把移除掉的值存起來
		if (oldBirthday != null) //如果設定過舊生日
			birthdayArray[oldBirthday].remove(memberID); //從記錄中移除這位成員
	}

	/**
	 * {@code TimerEvent} is a record class that is used for register hour events.
	 *
	 * @author Alex Cai
	 * @since 2.1
	 */
	private static record TimerEvent(byte hour, Runnable function)
	{
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