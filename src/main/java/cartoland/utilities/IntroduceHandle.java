package cartoland.utilities;

import java.util.HashMap;
import java.util.Map;

public class IntroduceHandle
{
	private IntroduceHandle()
	{
		throw new AssertionError(IDAndEntities.YOU_SHALL_NOT_ACCESS);
	}

	private static final String INTRODUCTION_FILE_NAME = "introduction.ser";
	private static final Map<Long, String> introduction = (FileHandle.deserialize(INTRODUCTION_FILE_NAME) instanceof Map map) ? map : new HashMap<>();

	public static void updateIntroduction(long userID, String content)
	{
		introduction.put(userID, content);
	}

	public static void deleteIntroduction(long userID)
	{
		introduction.remove(userID);
	}

	public static String getIntroduction(long userID)
	{
		return introduction.get(userID);
	}

	public static void serializeIntroduction()
	{
		FileHandle.serialize(INTRODUCTION_FILE_NAME, introduction);
	}
}
