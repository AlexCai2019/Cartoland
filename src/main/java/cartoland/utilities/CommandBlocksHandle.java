package cartoland.utilities;

import java.util.HashMap;

/**
 * {@code CommandBlocksHandle} is a utility class that handles command blocks of users. Command blocks is a feature
 * that whatever a user say in some specific channels, the user will gain command blocks as a kind of reward point.
 *
 * @since 1.5
 * @author Alex Cai
 */
public class CommandBlocksHandle
{
	private CommandBlocksHandle() {}

	private static final HashMap<String, Object> commandBlocks;

	static
	{
		commandBlocks = JsonHandle.commandBlocksToMap();
	}

	public static void addCommandBlocks(long userID, long add)
	{
		String userIDString = Long.toUnsignedString(userID);
		if (commandBlocks.containsKey(userIDString))
		{
			long level = (long) commandBlocks.get(userIDString);
			level += add;
			commandBlocks.put(userIDString, level >= 0 ? level : Long.MAX_VALUE); //避免溢位
		}
		else
			commandBlocks.put(userIDString, add);
	}

	public static void setCommandBlocks(long userID, long value)
	{
		commandBlocks.put(Long.toUnsignedString(userID), value);
	}

	public static long getCommandBlocks(long userID)
	{
		String userIDString = Long.toUnsignedString(userID);
		if (commandBlocks.containsKey(userIDString))
			return (long) commandBlocks.get(userIDString);
		commandBlocks.put(userIDString, 0L);
		return 0L;
	}
}