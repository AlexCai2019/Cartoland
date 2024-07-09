package cartoland.utilities;

import cartoland.Cartoland;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.Map;

public final class AnonymousHandle
{
	private static final String PRIVATE_TO_UNDERGROUND_MAP = "serialize/private_to_underground.ser";

	@SuppressWarnings("unchecked")
	private static final Map<Long, Long> privateToUnderground = CastToInstance.modifiableMap(FileHandle.deserialize(PRIVATE_TO_UNDERGROUND_MAP));

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

	/**
	 * Check if the member is able to send message through bot to underground channel.
	 *
	 * @param userID The id of the checked member.
	 * @return Underground channel and an empty string if no error, or null and error message if the member is invalid for underground.
	 * @since 2.2
	 * @author Alex Cai
	 */
	public static ObjectAndString checkMemberValid(long userID)
	{
		ObjectAndString returnData = new ObjectAndString();
		Guild cartoland = Cartoland.getJDA().getGuildById(IDs.CARTOLAND_SERVER_ID); //尋找創聯
		if (cartoland == null) //機器人找不到創聯
			return returnData.string("Can't get Cartoland server."); //結束

		Role nsfwRole = cartoland.getRoleById(IDs.NSFW_ROLE_ID);
		if (nsfwRole == null) //機器人找不到地下身分組
			return returnData.string("Can't get NSFW role.");

		TextChannel undergroundChannel = cartoland.getTextChannelById(IDs.UNDERGROUND_CHANNEL_ID); //尋找地下頻道
		if (undergroundChannel == null) //機器人找不到地下頻道
			return returnData.string("Can't get underground channel."); //結束

		final String can_t = ", hence you can't send message to the NSFW channel.";

		//雖然JDA官方群組說除非必要否則別用complete()
		//但是這裡不用的話會有同步問題
		Member member = cartoland.retrieveMemberById(userID).complete();

		if (member == null) //不是成員
			return returnData.string("You are not a member of " + cartoland.getName() + can_t);
		else if (member.isTimedOut()) //使用者已被禁言
			return returnData.string("You are timed out from " + cartoland.getName() + can_t);
		else if (!member.getRoles().contains(nsfwRole)) //使用者沒有地下身分組
			return returnData.string("You don't have role " + nsfwRole.getName() + can_t);
		else
			return returnData.object(undergroundChannel); //回傳地下頻道
	}
}