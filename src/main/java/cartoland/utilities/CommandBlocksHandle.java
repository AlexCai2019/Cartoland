package cartoland.utilities;

import cartoland.Cartoland;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * {@code CommandBlocksHandle} is a utility class that handles command blocks of users. Command blocks is a
 * feature that whatever a user say in some specific channels, the user will gain command blocks as a kind of
 * reward point. Can not be instantiated or inherited.
 *
 * @since 1.5
 * @author Alex Cai
 */
public final class CommandBlocksHandle
{
	private CommandBlocksHandle()
	{
		throw new AssertionError(IDs.YOU_SHALL_NOT_ACCESS);
	}

	public static boolean changed = true;
	private static final String LOTTERY_DATA_FILE_NAME = "serialize/lottery_data.ser";
	private static final long GAMBLE_ROLE_MIN = 100000L;

	//會有unchecked assignment的警告 but I did it anyway
	@SuppressWarnings("unchecked")
	private static final Map<Long, LotteryData> lotteryDataMap = (FileHandle.deserialize(LOTTERY_DATA_FILE_NAME) instanceof HashMap map) ? map : new HashMap<>();

	private static final List<LotteryData> lotteryDataList = new ArrayList<>(lotteryDataMap.values()); //將map轉換為array list
	//因為每次修改的是LotteryData的內容 而不是參考本身 所以可以事先建好

	static
	{
		FileHandle.registerSerialize(LOTTERY_DATA_FILE_NAME, lotteryDataMap);
	}

	/**
	 * Get the lottery data of a user from ID.
	 *
	 * @param userID The ID of the user.
	 * @return The lottery data of the user. It will never be null.
	 * @since 2.0
	 * @author Alex Cai
	 */
	public static LotteryData getLotteryData(long userID)
	{
		LotteryData lotteryData = lotteryDataMap.get(userID);
		if (lotteryData != null) //已經有這名玩家
			return lotteryData;

		//如果沒有記錄這名玩家
		LotteryData newUser = new LotteryData(userID);
		lotteryDataMap.put(userID, newUser); //放入這名玩家
		lotteryDataList.add(newUser); //放入這名玩家
		Cartoland.getJDA().retrieveUserById(userID).queue(user -> newUser.name = user.getEffectiveName());
		return newUser; //絕不回傳null
	}

	public static int size()
	{
		return lotteryDataMap.size();
	}

	public static List<LotteryData> toArrayList()
	{
		return lotteryDataList;
	}

	public static void initial()
	{
		Set<Long> keySet = lotteryDataMap.keySet();
		JDA jda = Cartoland.getJDA();
		for (long userID : keySet) //找到每位使用者
			jda.retrieveUserById(userID).queue(user -> lotteryDataMap.get(userID).name = user.getEffectiveName()); //更新名字
		changed = true;
	}

	/**
	 * This is a data class that stores members' lottery data.
	 *
	 * @since 2.0
	 * @author Alex Cai
	 */
	public static class LotteryData implements Serializable
	{
		public static final long DAILY = 100L; //每日獎勵
		public static final long WEEKLY = 100L; //每周獎勵
		public static final long MONTHLY = 500L;
		public static final long YEARLY = 10000L;
		private String name; //名字
		private final long userID;
		private long blocks; //方塊數
		private int won; //勝場
		private int lost; //敗場
		private int showHandWon; //梭哈勝
		private int showHandLost; //梭哈敗(破產)
		private long lastClaimSecond; //上次領每日獎勵的時間
		private int streak; //連續領每日獎勵

		@Serial
		private static final long serialVersionUID = 3_141592653589793238L;

		private LotteryData(long userID)
		{
			this.userID = userID;
			blocks = 0L;
			won = 0;
			lost = 0;
			showHandWon = 0;
			showHandLost = 0;
			lastClaimSecond = 0L;
			streak = 0;
		}

		public void setName(String newName)
		{
			name = newName;
		}

		public String getName()
		{
			return name;
		}

		/**
		 * Add command blocks to the user. This method calls {@link Algorithm#safeAdd(long, long) in
		 * order to add without overflow.
		 *
		 * @param add The amount of command blocks that are going to add on this user.
		 * @since 2.0
		 * @author Alex Cai
		 */
		public void addBlocks(long add)
		{
			setBlocks(Algorithm.safeAdd(blocks, add));
		}

		/**
		 * Subtract command blocks to the user. This method checks if this user has enough command
		 * blocks in order to prevent negative command blocks.
		 *
		 * @param sub The amount of command blocks that are going to subtract on this user.
		 * @since 2.1
		 * @author Alex Cai
		 */
		public void subBlocks(long sub)
		{
			setBlocks(blocks > sub ? blocks - sub : 0L);
		}

		/**
		 * Set command blocks to the user.
		 *
		 * @param newValue The amount of command blocks that are going to set on this user.
		 * @since 2.0
		 * @author Alex Cai
		 */
		public void setBlocks(long newValue)
		{
			changed = true; //指令方塊改變過了

			long oldValue = blocks;
			blocks = newValue;

			boolean less = newValue < GAMBLE_ROLE_MIN; //true = 新值依舊比GAMBLE_ROLE_MIN少
			if (oldValue < GAMBLE_ROLE_MIN == less) //沒有跨過GAMBLE_ROLE_MIN
				return;

			Guild cartoland = Cartoland.getJDA().getGuildById(IDs.CARTOLAND_SERVER_ID); //創聯
			if (cartoland == null) //找不到創聯
				return;
			cartoland.retrieveMemberById(userID).queue(member ->
			{
				Role godOfGamblersRole = cartoland.getRoleById(IDs.GOD_OF_GAMBLERS_ROLE_ID); //賭神身分組
				if (godOfGamblersRole == null) //找不到賭神身分組
					return;
				boolean hasRole = member.getRoles().contains(godOfGamblersRole);
				if (!less && !hasRole) //大於等於GAMBLE_ROLE_MIN 且沒有身分組
					cartoland.addRoleToMember(member, godOfGamblersRole).queue(); //給予賭神身分組
				else if (less && hasRole) //小於GAMBLE_ROLE_MIN 且有身分組
					cartoland.removeRoleFromMember(member, godOfGamblersRole).queue(); //剝奪賭神身分組
			});
		}

		public long getBlocks()
		{
			return blocks;
		}

		public int getWon()
		{
			return won;
		}

		public int getLost()
		{
			return lost;
		}

		public int getShowHandWon()
		{
			return showHandWon;
		}

		public int getShowHandLost()
		{
			return showHandLost;
		}

		public void addGame(boolean isWon, boolean isShowHand)
		{
			if (isWon)
			{
				won++;
				if (isShowHand)
					showHandWon++;
			}
			else
			{
				lost++;
				if (isShowHand)
					showHandLost++;
			}
		}

		/**
		 * Try claim the daily reward. Success if
		 *
		 * @param until The time until next available daily reward.
		 * @return If the difference in seconds between now and the last time daily reward was claimed is more than a day.
		 * @since 2.1
		 * @author Alex Cai
		 */
		public boolean tryClaimDaily(int[] until)
		{
			long nowSecond = System.currentTimeMillis() / 1000L; //現在距離1970/1/1有幾秒
			long difference = nowSecond - lastClaimSecond; //和上次領的時間差
			if (difference < 60 * 60 * 24) //時間小於一天 86400秒
			{
				//不超過一天
				int secondsUntil = 60 * 60 * 24 - (int) difference;
				until[0] = secondsUntil / (60 * 60);
				until[1] = (secondsUntil / 60) % 60;
				until[2] = secondsUntil % 60;
				return false;
			}

			addBlocks(DAILY); //增加每日獎勵
			lastClaimSecond = nowSecond; //最後一次領的時間為現在
			if (difference >= 60 * 60 * 24 * 2) //大於兩天 代表超過48小時沒領了
				streak = 0; //連續歸零
			streak++; //+1 連續領
			return true;
		}

		public boolean tryClaimBonus(boolean[] bonus)
		{
			long addBonus = 0L; //獎勵的額外指令方塊

			if (bonus[0] = (streak % 7 == 0)) //一週
				addBonus += WEEKLY;
			if (bonus[1] = (streak % 30 == 0)) //一個月
				addBonus += MONTHLY;
			if (bonus[2] = (streak % 365 == 0)) //一年
				addBonus += YEARLY;

			if (addBonus != 0L)
			{
				addBlocks(addBonus);
				return true;
			}
			else
				return false;
		}

		public int getStreak()
		{
			return streak;
		}
	}
}