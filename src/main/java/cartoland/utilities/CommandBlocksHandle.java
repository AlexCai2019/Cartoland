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

	/**
	 * Add command blocks to the user that has userID as ID. This method calls
	 * {@link Algorithm#safeAdd(long, long)} in order to add without overflow.
	 *
	 * @param userID The ID of the user that needs to add command blocks.
	 * @param add The amount of command blocks that are going to add on this user.
	 */
	public static void add(long userID, long add)
	{
		String userIDString = Long.toUnsignedString(userID);
		commandBlocksFile.put(userIDString, commandBlocksFile.has(userIDString) ?
				Algorithm.safeAdd(commandBlocksFile.getLong(userIDString), add) : add);
	}

	public static void set(long userID, long value)
	{
		commandBlocksFile.put(Long.toUnsignedString(userID), value);
	}

	public static long get(long userID)
	{
		String userIDString = Long.toUnsignedString(userID);
		if (commandBlocksFile.has(userIDString))
			return commandBlocksFile.getLong(userIDString);
		commandBlocksFile.put(userIDString, 0L);
		return 0L;
	}

	public static int length()
	{
		return commandBlocksFile.length();
	}

	public static java.util.Map<String, Object> getMap()
	{
		return commandBlocksFile.toMap();
	}
}