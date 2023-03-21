package cartoland.events;

import cartoland.utilities.IDAndEntities;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

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

	@Override
	public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event)
	{
		if (event.getGuild().getIdLong() != IDAndEntities.CARTOLAND_SERVER_ID)
			return;
		User user = event.getUser();
		if (user.isBot() || user.isSystem() || !user.hasPrivateChannel())
			return;

		user.openPrivateChannel().queue(privateChannel ->
		{
			  String userName = user.getName();
			  String serverName = IDAndEntities.cartolandServer.getName();
			  privateChannel.sendMessage("歡迎你，" + userName + "，來到 " + serverName + "\n" +
												 "記得先詳閱 <#973898745777377330> 內的訊息，並遵守一切公告規則。\n" +
												 userName + ", welcome to " + serverName + ".\n" +
												 "Please read messages in <#973898745777377330>, and follow all rules.").queue();
		});
	}

	@Override
	public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent event)
	{
		if (!event.getRoles().contains(IDAndEntities.memberRole))
			return;

		String mentionUser = event.getUser().getAsMention();
		String serverName = IDAndEntities.cartolandServer.getName();

		IDAndEntities.lobbyChannel.sendMessage("歡迎 " + mentionUser + " 加入 " + serverName + "\n" +
													   mentionUser + ", welcome to " + serverName).queue(message -> message.addReaction(wave).queue());
	}
}