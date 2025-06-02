package cartoland.utilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MembersHandle
{
	static Map<Long, String> userLanguage = new HashMap<>(); //使用者的語言設定 id為key, 而en, tw等等的語言字串為value

	public static void memberJoin(long userID)
	{
		userLanguage.put(userID, Languages.TW_MANDARIN); //預設中文
		DatabaseHandle.onMemberJoin(userID);
	}

	public static List<Long> getAllMembers()
	{
		return DatabaseHandle.readAllMembers();
	}

	public static String getIntroduction(long userID)
	{
		return DatabaseHandle.readIntroduction(userID);
	}

	public static void updateIntroduction(long userID, String content)
	{
		if (content.isEmpty())
			DatabaseHandle.writeIntroduction(userID, null);
		else
			DatabaseHandle.writeIntroduction(userID, content);
	}

	public static void memberLeave(long userID)
	{
		userLanguage.remove(userID);
		DatabaseHandle.onMemberLeave(userID);
	}
}

/**
 * Language constants. Can not be instantiated or inherited.
 *
 * @since 1.0
 * @author Alex Cai
 */
final class Languages
{
	private Languages()
	{
		throw new AssertionError(IDs.YOU_SHALL_NOT_ACCESS);
	}

	public static final String ENGLISH = "en";
	public static final String TW_MANDARIN = "tw";
	public static final String TAIWANESE = "ta";
	public static final String CANTONESE = "hk";
	public static final String CHINESE = "cn";
	public static final String ESPANOL = "es";
	public static final String JAPANESE = "jp";
}