package cartoland.utilities;

import static cartoland.utilities.JsonHandle.commandBlocksFile;

/**
 * {@code CommandBlocksHandle} is a utility class that handles command blocks of users. Command blocks is a
 * feature that whatever a user say in some specific channels, the user will gain command blocks as a kind of
 * reward point. This class access a static field {@link JsonHandle#commandBlocksFile} from {@link JsonHandle}, which is
 * where all the command blocks are stored, hence this class can be seen as an extension of {@code JsonHandle}. Can
 * not be instantiated.
 *
 * @since 1.5
 * @see JsonHandle
 * @author Alex Cai
 */
public class CommandBlocksHandle
{
	private CommandBlocksHandle()
	{
		throw new AssertionError(IDAndEntities.YOU_SHALL_NOT_ACCESS);
	}

	public static void addCommandBlocks(long userID, long add)
	{
		String userIDString = Long.toUnsignedString(userID);
		if (commandBlocksFile.has(userIDString))
		{
			long level = commandBlocksFile.getLong(userIDString);
			level += add;
			commandBlocksFile.put(userIDString, level >= 0 ? level : Long.MAX_VALUE); //避免溢位
		}
		else
			commandBlocksFile.put(userIDString, add);
	}

	public static void setCommandBlocks(long userID, long value)
	{
		commandBlocksFile.put(Long.toUnsignedString(userID), value);
	}

	public static long getCommandBlocks(long userID)
	{
		String userIDString = Long.toUnsignedString(userID);
		if (commandBlocksFile.has(userIDString))
			return commandBlocksFile.getLong(userIDString);
		commandBlocksFile.put(userIDString, 0L);
		return 0L;
	}
}