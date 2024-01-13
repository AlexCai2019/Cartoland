package cartoland.utilities;

import cartoland.Cartoland;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.HashMap;
import java.util.Map;

public final class AnonymousHandle
{
	private static final String PRIVATE_TO_UNDERGROUND_MAP = "serialize/private_to_underground.ser";

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static final Map<Long, Long> privateToUnderground = FileHandle.deserialize(PRIVATE_TO_UNDERGROUND_MAP) instanceof HashMap map ? map : new HashMap<>();

	static
	{
		FileHandle.registerSerialize(PRIVATE_TO_UNDERGROUND_MAP, privateToUnderground);
	}

	public static void addConnection(long privateMessageID, long undergroundMessageID)
	{
		privateToUnderground.put(privateMessageID, undergroundMessageID);
	}

	public static Long getConnection(long privateMessageID)
	{
		return privateToUnderground.get(privateMessageID);
	}

	public static String checkMemberValid(long userID, TextChannel[] undergroundChannel)
	{
		Guild cartoland = Cartoland.getJDA().getGuildById(IDs.CARTOLAND_SERVER_ID); //尋找創聯
		if (cartoland == null) //機器人找不到創聯
			return "Can't get Cartoland server."; //結束

		Role nsfwRole = cartoland.getRoleById(IDs.NSFW_ROLE_ID);
		if (nsfwRole == null) //機器人找不到地下身分組
			return "Can't get NSFW role.";

		undergroundChannel[0] = cartoland.getTextChannelById(IDs.UNDERGROUND_CHANNEL_ID); //尋找地下頻道
		if (undergroundChannel[0] == null) //機器人找不到地下頻道
			return "Can't get underground channel."; //結束

		final String can_t = ", hence you can't send message to the NSFW channel.";

		Member member = cartoland.retrieveMemberById(userID).complete();
		if (member == null)
			return "You are not a member of " + cartoland.getName() + can_t;

		if (member.isTimedOut()) //使用者已被禁言
			return "You are timed out from " + cartoland.getName() + can_t;

		if (!member.getRoles().contains(nsfwRole)) //使用者沒有地下身分組
			return "You don't have role " + nsfwRole.getName() + can_t;

		return "";
	}
}