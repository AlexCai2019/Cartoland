package cartoland.events;

import cartoland.utilities.FileHandle;
import cartoland.utilities.IDs;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@code NewMember} is a listener that triggers when a user joined a server that the bot is in, or get a new role. For now,
 * it only reacts with the "member role" in Cartoland. This class was registered in
 * {@link cartoland.Cartoland#main}, with the build of JDA.
 *
 * @since 1.4
 * @author Alex Cai
 */
public class NewMember extends ListenerAdapter
{
	private final String welcomeMessage =
			"""
			歡迎你，%%s，來到 %%s。
			記得先詳閱 <#%d> 內的訊息，並遵守一切公告規則。
			%%s, welcome to %%s.
			Please read messages in <#%d>, and follow all rules.
			""".formatted(IDs.READ_ME_CHANNEL_ID, IDs.READ_ME_CHANNEL_ID);
	private static final String ALL_MEMBERS = "serialize/all_members.ser";
	private static final Set<Long> allMembers = FileHandle.deserialize(ALL_MEMBERS) instanceof HashSet<?> set ?
			set.stream().map(userID -> (Long)userID).collect(Collectors.toSet()) : new HashSet<>();

	static
	{
		FileHandle.registerSerialize(ALL_MEMBERS, allMembers);
	}

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event)
	{
		User user = event.getUser();
		if (!user.hasPrivateChannel())
			return;
		Guild cartoland = event.getGuild();
		if (cartoland.getIdLong() != IDs.CARTOLAND_SERVER_ID)
			return;
		String serverName = cartoland.getName();
		String userName = user.getEffectiveName();
		user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(welcomeMessage.formatted(userName, serverName, userName, serverName)).queue());
	}

	@Override
	public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event)
	{
		User user = event.getUser();
		long userID = user.getIdLong();
		if (allMembers.contains(userID))
			return;

		Guild cartoland = event.getGuild();
		if (cartoland.getIdLong() != IDs.CARTOLAND_SERVER_ID)
			return;

		Role memberRole = cartoland.getRoleById(IDs.MEMBER_ROLE_ID);
		if (memberRole == null)
			return;
		if (!event.getRoles().contains(memberRole))
			return;

		allMembers.add(userID);

		TextChannel lobbyChannel = cartoland.getTextChannelById(IDs.LOBBY_CHANNEL_ID);
		if (lobbyChannel == null)
			return;
		String mentionUser = user.getAsMention();
		String serverName = cartoland.getName();
		lobbyChannel.sendMessage("歡迎 " + mentionUser + " 加入 " + serverName + "\n" + mentionUser + ", welcome to " + serverName)
				.queue(message -> message.addReaction(Emoji.fromUnicode("👋")).queue());
	}

	@Override
	public void onGuildMemberRemove(GuildMemberRemoveEvent event)
	{
		allMembers.remove(event.getUser().getIdLong());
	}
}