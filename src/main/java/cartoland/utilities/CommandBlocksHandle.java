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

	/**
	 * Add command blocks to the user that has userID as ID. This method calls
	 * {@link Algorithm#safeAdd(long, long)} in order to add without overflow.
	 *
	 * @param userID The ID of the user that needs to add command blocks.
	 * @param add The amount of command blocks that are going to add on this user.
	 */
	public static void addBlocks(long userID, long add)
	{
		LotteryData lotteryData = lotteryDataMap.get(userID);
		setBlocks(userID, (lotteryData != null) ? Algorithm.safeAdd(lotteryData.blocks, add) : add);
	}

	public static void setBlocks(long userID, long newValue)
	{
		changed = true;
		LotteryData lotteryData = lotteryDataMap.get(userID);

		long oldValue;
		if (lotteryData != null)
		{
			oldValue = lotteryData.blocks;
			lotteryData.blocks = newValue;
		}
		else
		{
			oldValue = 0L;
			jda.retrieveUserById(userID).queue(user -> lotteryDataMap.put(userID, new LotteryData(user.getEffectiveName(), newValue)));
		}

		boolean less = newValue < GAMBLE_ROLE_MIN; //新值依舊比GAMBLE_ROLE_MIN少
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

	public static long getBlocks(long userID)
	{
		LotteryData lotteryData = lotteryDataMap.get(userID);
		if (lotteryData != null)
			return lotteryData.blocks;

		//如果沒有記錄這名玩家
		jda.retrieveUserById(userID).queue(user -> lotteryDataMap.put(userID, new LotteryData(user.getEffectiveName(), 0L)));
		return 0L;
	}

	public static int size()
	{
		return lotteryDataMap.size();
	}

	public static void rename(long userID, String newName)
	{
		LotteryData lotteryData = lotteryDataMap.get(userID);
		if (lotteryData != null)
			lotteryData.name = newName;
		else
			lotteryDataMap.put(userID, new LotteryData(newName, 0L));
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
					lotteryData.name = name; //新名字
			}));
		changed = true;
	}

	public static class LotteryData implements Serializable
	{
		public String name; //名字
		private long blocks; //方塊數

		public LotteryData(String name, long blocks)
		{
			this.name = name;
			this.blocks = blocks;
		}

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		public long getBlocks()
		{
			return blocks;
		}
	}
}