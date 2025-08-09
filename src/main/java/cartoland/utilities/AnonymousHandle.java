package cartoland.utilities;

import cartoland.Cartoland;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.LinkedHashMap;
import java.util.Map;

public final class AnonymousHandle
{
	private static final Map<Long, Long> cacheConnection = new LinkedHashMap<>()
	{
		private static final int CACHE_SIZE = 10;

		@Override
		protected boolean removeEldestEntry(Map.Entry<Long, Long> eldest)
		{
			return size() > CACHE_SIZE; //超過cache大小後刪除最早的
		}
	};

	public static final long INVALID_CONNECTION = -1L;

	public static void addConnection(long privateMessageID, long undergroundMessageID)
	{
		cacheConnection.put(privateMessageID, undergroundMessageID); //加入cache
		DatabaseHandle.writeUndergroundConnection(privateMessageID, undergroundMessageID); //寫入資料庫
	}

	public static long getConnection(long privateMessageID)
	{
		Long undergroundMessageID = cacheConnection.get(privateMessageID); //先從cache找 大部分人只會編輯最近的訊息

		//有找到就可回傳 沒找到再從資料庫裡找
		return undergroundMessageID != null ? undergroundMessageID : DatabaseHandle.readUndergroundID(privateMessageID);
	}

	/**
	 * Check if the member is able to send message through bot to underground channel.
	 *
	 * @param userID The id of the checked member.
	 * @return Underground channel and null if no error, or null and error message if the member is invalid for underground.
	 * @since 2.2
	 * @author Alex Cai
	 */
	public static ReturnResult<TextChannel> checkMemberValid(long userID)
	{
		Guild cartoland = Cartoland.getJDA().getGuildById(IDs.CARTOLAND_SERVER_ID); //尋找創聯
		if (cartoland == null) //機器人找不到創聯
			return ReturnResult.fail("Can't get Cartoland server."); //結束

		Role nsfwRole = cartoland.getRoleById(IDs.NSFW_ROLE_ID);
		if (nsfwRole == null) //機器人找不到地下身分組
			return ReturnResult.fail("Can't get NSFW role.");

		TextChannel undergroundChannel = cartoland.getTextChannelById(IDs.UNDERGROUND_CHANNEL_ID); //尋找地下頻道
		if (undergroundChannel == null) //機器人找不到地下頻道
			return ReturnResult.fail("Can't get underground channel."); //結束

		final String can_t = ", hence you can't send message to the NSFW channel.";

		//雖然JDA官方群組說除非必要否則別用complete()
		//但是這裡不用的話會有同步問題
		Member member = cartoland.retrieveMemberById(userID).complete();

		if (member == null) //不是成員
			return ReturnResult.fail("You are not a member of " + cartoland.getName() + can_t);
		else if (member.isTimedOut()) //使用者已被禁言
			return ReturnResult.fail("You are timed out from " + cartoland.getName() + can_t);
		else if (!member.getUnsortedRoles().contains(nsfwRole)) //使用者沒有地下身分組
			return ReturnResult.fail("You don't have role " + nsfwRole.getName() + can_t);
		else //成功
			return ReturnResult.success(undergroundChannel); //回傳地下頻道
	}
}