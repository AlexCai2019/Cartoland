package cartoland.utilities;

import cartoland.Cartoland;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

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
		String introduction = DatabaseHandle.readIntroduction(userID);
		return introduction == null ? "" : introduction; //null變空字串
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

	public record BannedUser(long userID, long unbanTime, long guildID)
	{
		public void tempBan()
		{
			DatabaseHandle.banUser(this);
		}

		public void tryUnban()
		{
			if (TimerHandle.getHoursFrom1970() < unbanTime) //還沒到這個人要被解ban的時間
				return; //結束

			DatabaseHandle.pardonUser(this); //不再紀錄這名使用者 無論群組是否已經不在了

			Guild bannedServer = Cartoland.getJDA().getGuildById(guildID); //找到當初ban他的群組
			if (bannedServer != null) //群組還在
				bannedServer.unban(User.fromId(userID)).queue(); //解ban他
		}
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