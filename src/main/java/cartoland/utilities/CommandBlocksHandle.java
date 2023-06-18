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
 * reward point. This class open a static field {@link #nameAndBlocksMap} for {@link JsonHandle}, which is where all the
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
	private static final String NAME_AND_BLOCKS_FILE_NAME = "serialize/name_and_blocks.ser";
	private static final long GAMBLE_ROLE_MIN = 100000;

	//會有unchecked assignment的警告 but I did it anyway
	@SuppressWarnings("unchecked")
	private static final Map<Long, NameAndBlocks> nameAndBlocksMap = (FileHandle.deserialize(NAME_AND_BLOCKS_FILE_NAME) instanceof HashMap map) ? map : new HashMap<>();

	static
	{
		FileHandle.registerSerialize(NAME_AND_BLOCKS_FILE_NAME, nameAndBlocksMap);
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
		NameAndBlocks nameAndBlocks = nameAndBlocksMap.get(userID);
		setBlocks(userID, (nameAndBlocks != null) ? Algorithm.safeAdd(nameAndBlocks.blocks, add) : add);
	}

	public static void setBlocks(long userID, long value)
	{
		changed = true;
		NameAndBlocks nameAndBlocks = nameAndBlocksMap.get(userID);

		if (nameAndBlocks != null)
			nameAndBlocks.blocks = value;
		else
			jda.retrieveUserById(userID).queue(user -> nameAndBlocksMap.put(userID, new NameAndBlocks(user.getEffectiveName(), value)));

		cartolandServer.retrieveMemberById(userID).queue(member ->
		{
			if (value >= GAMBLE_ROLE_MIN && !member.getRoles().contains(godOfGamblersRole))
				cartolandServer.addRoleToMember(member, godOfGamblersRole).queue();
			else if (value < GAMBLE_ROLE_MIN && member.getRoles().contains(godOfGamblersRole))
				cartolandServer.removeRoleFromMember(member, godOfGamblersRole).queue();
		});
	}

	public static long getBlocks(long userID)
	{
		NameAndBlocks nameAndBlocks = nameAndBlocksMap.get(userID);
		if (nameAndBlocks != null)
			return nameAndBlocks.blocks;

		//如果沒有記錄這名玩家
		jda.retrieveUserById(userID).queue(user -> nameAndBlocksMap.put(userID, new NameAndBlocks(user.getEffectiveName(), 0L)));
		return 0L;
	}

	public static int size()
	{
		return nameAndBlocksMap.size();
	}

	public static void rename(long userID, String newName)
	{
		nameAndBlocksMap.get(userID).name = newName;
	}

	public static List<NameAndBlocks> toArrayList()
	{
		return new ArrayList<>(nameAndBlocksMap.values());
	}

	public static void initial()
	{
		nameAndBlocksMap.forEach((userID, nameAndBlocks) ->
			jda.retrieveUserById(userID).queue(user ->
			{
				String name = user.getEffectiveName();
				if (!nameAndBlocks.getName().equals(name)) //如果名字和紀錄的名字不一樣
					nameAndBlocks.setName(name); //新名字
			}));
		changed = true;
	}

	public static class NameAndBlocks implements Serializable
	{
		private String name; //名字
		private long blocks; //方塊數

		public NameAndBlocks(String name, long blocks)
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