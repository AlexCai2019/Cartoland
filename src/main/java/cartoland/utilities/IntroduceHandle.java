package cartoland.utilities;

import java.util.HashMap;
import java.util.Map;

/**
 * {@code IntroduceHandle} is a utility class that handles introduction of users. Whenever user typed anything in the
 * self-intro channel, the message will be store into {@link #introduction}. Users can also modify their introduction
 * via /introduce command.
 *
 * @since 2.0
 * @author Alex Cai
 */
public final class IntroduceHandle
{
	private IntroduceHandle()
	{
		throw new AssertionError(IDAndEntities.YOU_SHALL_NOT_ACCESS);
	}

	private static final String INTRODUCTION_FILE_NAME = "serialize/introduction.ser";

	@SuppressWarnings("unchecked")
	private static final Map<Long, String> introduction = (FileHandle.deserialize(INTRODUCTION_FILE_NAME) instanceof HashMap map) ? map : new HashMap<>();

	static
	{
		FileHandle.registerSerialize(INTRODUCTION_FILE_NAME, introduction);
	}

	/**
	 * Update the user introduction.
	 *
	 * @param userID The ID of the user that are going to update his/her introduction.
	 * @param content The content of the introduction that the user want to replace the old one.
	 * @since 2.0
	 * @author Alex Cai
	 */
	public static void updateIntroduction(long userID, String content)
	{
		introduction.put(userID, content);
	}

	/**
	 * Delete the user introduction.
	 *
	 * @param userID The ID of the user that are going to delete his/her introduction.
	 * @since 2.0
	 * @author Alex Cai
	 */
	public static void deleteIntroduction(long userID)
	{
		introduction.remove(userID);
	}

	/**
	 * Get the user introduction.
	 *
	 * @param userID The ID of the user that are going to get his/her introduction.
	 * @since 2.0
	 * @author Alex Cai
	 */
	public static String getIntroduction(long userID)
	{
		return introduction.get(userID);
	}
}