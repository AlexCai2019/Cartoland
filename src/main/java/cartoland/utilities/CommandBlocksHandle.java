package cartoland.utilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cartoland.utilities.IDAndEntities.*;

/**
 * {@code CommandBlocksHandle} is a utility class that handles command blocks of users. Command blocks is a
 * feature that whatever a user say in some specific channels, the user will gain command blocks as a kind of
 * reward point. This class open a static field {@link #lotteryDataMap} for {@link JsonHandle}, which is where all the
 * command blocks are stored, hence this class can be seen as an extension of {@code JsonHandle}. Can not be
 * instantiated.
 *
 * @since 1.5
 * @see JsonHandle
 * @author Alex Cai
 */
public class CommandBlocksHandle
{
	private CommandBlocksHandle()
	{
		throw new AssertionError(YOU_SHALL_NOT_ACCESS);
	}

	public static boolean changed = true;
	private static final String LOTTERY_DATA_FILE_NAME = "serialize/lottery_data.ser";
	private static final long GAMBLE_ROLE_MIN = 100000L;

	//會有unchecked assignment的警告 but I did it anyway
	@SuppressWarnings("unchecked")
	private static final Map<Long, LotteryData> lotteryDataMap = (FileHandle.deserialize(LOTTERY_DATA_FILE_NAME) instanceof HashMap map) ? map : new HashMap<>();

	static
	{
		FileHandle.registerSerialize(LOTTERY_DATA_FILE_NAME, lotteryDataMap);
	}

	public static LotteryData getLotteryData(long userID)
	{
		LotteryData lotteryData = lotteryDataMap.get(userID);
		if (lotteryData == null) //如果沒有記錄這名玩家
		{
			lotteryDataMap.put(userID, lotteryData = new LotteryData(userID));
			final LotteryData finalLotteryData = lotteryData; //lambda要用
			jda.retrieveUserById(userID).queue(user -> finalLotteryData.name = user.getEffectiveName());
		}
		return lotteryData;
	}

	public static int size()
	{
		return lotteryDataMap.size();
	}

	public static List<LotteryData> toArrayList()
	{
		return new ArrayList<>(lotteryDataMap.values());
	}

	public static void initial()
	{
		lotteryDataMap.forEach((userID, lotteryData) ->
			jda.retrieveUserById(userID).queue(user ->
			{
				String name = user.getEffectiveName();
				if (!lotteryData.name.equals(name)) //如果名字和紀錄的名字不一樣
					lotteryData.setName(name); //新名字
			}));
		changed = true;
	}

	public static class LotteryData implements Serializable
	{
		private String name; //名字
		private final long userID;
		private long blocks; //方塊數
		private int won; //勝場
		private int lost; //敗場
		private int showHandWon; //梭哈勝
		private int showHandLost; //梭哈敗(破產)

		public LotteryData(long userID)
		{
			this(userID, 0L);
		}

		public LotteryData(long userID, long blocks)
		{
			this.userID = userID;
			this.blocks = blocks;
			won = 0;
			lost = 0;
			showHandWon = 0;
			showHandLost = 0;
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
		 * Add command blocks to the user that has userID as ID. This method calls
		 * {@link Algorithm#safeAdd(long, long)} in order to add without overflow.
		 *
		 * @param add The amount of command blocks that are going to add on this user.
		 */
		public void addBlocks(long add)
		{
			setBlocks(Algorithm.safeAdd(blocks, add));
		}

		public void setBlocks(long newValue)
		{
			changed = true; //指令方塊改變過了

			long oldValue = blocks;
			blocks = newValue;

			boolean less = newValue < GAMBLE_ROLE_MIN; //true = 新值依舊比GAMBLE_ROLE_MIN少
			if (oldValue < GAMBLE_ROLE_MIN == less) //沒有跨過GAMBLE_ROLE_MIN
				return;

			cartolandServer.retrieveMemberById(userID).queue(member ->
			{
				boolean hasRole = member.getRoles().contains(godOfGamblersRole);
				if (!less && !hasRole) //大於等於GAMBLE_ROLE_MIN 且沒有身分組
				 cartolandServer.addRoleToMember(member, godOfGamblersRole).queue(); //給予賭神身分組
				else if (less && hasRole) //小於GAMBLE_ROLE_MIN 且有身分組
				 cartolandServer.removeRoleFromMember(member, godOfGamblersRole).queue(); //剝奪賭神身分組
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
	}
}