package cartoland.events;

import cartoland.utilities.IDs;
import cartoland.utilities.MembersHandle;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.time.Duration;
import java.time.OffsetDateTime;

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
		MembersHandle.memberJoin(user.getIdLong()); //記錄下每個成員

		if (Duration.between(user.getTimeCreated(), OffsetDateTime.now()).toDays() > 7) //創帳號日大於七天
			return;

		Role admin = cartoland.getRoleById(IDs.ADMIN_ROLE_ID);
		TextChannel welcomeChannel = cartoland.getSystemChannel();
		if (welcomeChannel != null)
			welcomeChannel.sendMessage((admin != null ? admin.getAsMention() : "") + " 注意 " + user.getAsMention() + " 創帳號日期小於一週！").queue();
	}

	@Override
	public void onGuildMemberRemove(GuildMemberRemoveEvent event)
	{
		Guild cartoland = event.getGuild();
		if (cartoland.getIdLong() != IDs.CARTOLAND_SERVER_ID) //不是創聯
			return; //結束

		User user = event.getUser();
		if (user.isBot() && user.isSystem())
			return;

		long userID = user.getIdLong();
		MembersHandle.memberLeave(userID);

		TextChannel welcomeChannel = event.getGuild().getSystemChannel();
		if (welcomeChannel != null)
			welcomeChannel.sendMessage(user.getName() + '(' + Long.toUnsignedString(userID) + ") bye have a great time!").queue();
	}
}