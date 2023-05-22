package cartoland.events;

import cartoland.utilities.FileHandle;
import cartoland.utilities.IDAndEntities;
import net.dv8tion.jda.api.entities.User;
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
	private final Emoji wave = Emoji.fromUnicode("👋");
	private final String welcomeMessage =
			"""
			歡迎你，%%s，來到 %%s。
			記得先詳閱 <#%d> 內的訊息，並遵守一切公告規則。
			%%s, welcome to %%s.
			Please read messages in <#%d>, and follow all rules.
			""".formatted(IDAndEntities.READ_ME_CHANNEL_ID, IDAndEntities.READ_ME_CHANNEL_ID);
	private final String ALL_MEMBERS = "serialize/all_members.ser";
	private final Set<Long> allMembers = FileHandle.deserialize(ALL_MEMBERS) instanceof HashSet<?> set ?
			set.stream().map(userID -> (Long)userID).collect(Collectors.toSet()) : new HashSet<>();

	public NewMember()
	{
		FileHandle.registerSerialize(ALL_MEMBERS, allMembers);
	}

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event)
	{
		User user = event.getUser();
		if (user.isBot() || user.isSystem() || !user.hasPrivateChannel())
			return;

		String serverName = IDAndEntities.cartolandServer.getName();
		String userTag = user.getAsTag();
		user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(welcomeMessage.formatted(userTag, serverName, userTag, serverName)).queue());
	}

	@Override
	public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event)
	{
		if (allMembers.contains(event.getUser().getIdLong()))
			return;

		if (!event.getRoles().contains(IDAndEntities.memberRole))
			return;

		String mentionUser = event.getUser().getAsMention();
		String serverName = IDAndEntities.cartolandServer.getName();

		IDAndEntities.lobbyChannel.sendMessage("歡迎 " + mentionUser + " 加入 " + serverName + "\n" +
													   mentionUser + ", welcome to " + serverName).queue(message -> message.addReaction(wave).queue());
	}

	@Override
	public void onGuildMemberRemove(GuildMemberRemoveEvent event)
	{
		allMembers.remove(event.getUser().getIdLong());
	}
}