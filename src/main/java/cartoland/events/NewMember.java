package cartoland.events;

import cartoland.utilities.CastToInstance;
import cartoland.utilities.FileHandle;
import cartoland.utilities.IDs;
import cartoland.utilities.TimerHandle;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * {@code NewMember} is a listener that triggers when a user joined a server that the bot is in, or get a new role. For now,
 * it only reacts with the "member role" in Cartoland. This class was registered in
 * {@link cartoland.Cartoland#main(String[])}, with the build of JDA.
 *
 * @since 1.4
 * @author Alex Cai
 */
public class NewMember extends ListenerAdapter
{
	private final String welcomeMessage =
			"歡迎你，%s，來到 %s。\n" +
			"記得先詳閱 <#" + IDs.READ_ME_CHANNEL_ID +"> 內的訊息，並遵守一切公告規則。\n" +
			"使用 </language:1102681768840138936> 設定本機器人的語言。\n" +
			"%s, welcome to %s.\n" +
			"Please read messages in <#" + IDs.READ_ME_CHANNEL_ID + ">, and follow all rules.\n" +
			"Set the language of this bot through </language:1102681768840138936> .";
	private static final String ALL_MEMBERS = "serialize/all_members.ser";

	@SuppressWarnings("unchecked")
	private static final Set<Long> allMembers = CastToInstance.modifiableSet(FileHandle.deserialize(ALL_MEMBERS));

	private static List<Long> allMembersList = Collections.emptyList();

	static
	{
		FileHandle.registerSerialize(ALL_MEMBERS, allMembers);
	}

	public static List<Long> getAllMembersList()
	{
		if (allMembersList.size() != allMembers.size())
			allMembersList = new ArrayList<>(allMembers);
		return allMembersList;
	}

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event)
	{
		Guild cartoland = event.getGuild();
		if (cartoland.getIdLong() != IDs.CARTOLAND_SERVER_ID) //不是創聯
			return; //結束
		User user = event.getUser();
		if (user.isBot() && user.isSystem())
			return;
		String userName = user.getEffectiveName();
		String serverName = cartoland.getName();
		user.openPrivateChannel()
				.flatMap(privateChannel -> privateChannel.sendMessage(welcomeMessage.formatted(userName, serverName, userName, serverName)))
				.queue(null, new ErrorHandler().ignore(ErrorResponse.CANNOT_SEND_TO_USER)); //不能傳送私訊就算了
		allMembers.add(user.getIdLong()); //記錄下每個成員
	}

	@Override
	public void onGuildMemberRemove(GuildMemberRemoveEvent event)
	{
		long userID = event.getUser().getIdLong();
		allMembers.remove(userID);
		TimerHandle.deleteBirthday(userID);
	}
}