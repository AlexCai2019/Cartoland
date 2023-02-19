package cartoland.events;

import cartoland.utilities.IDAndEntities;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * {@code GetRole} is a listener that triggers when a member get a role. For now, it only reacts with the "member role".
 * This class was registered in {@link cartoland.Cartoland#main}, with the build of JDA.
 *
 * @since 1.4
 * @author Alex Cai
 */
public class GetRole extends ListenerAdapter
{
	@Override
	public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent event)
	{
		if (!event.getRoles().contains(IDAndEntities.memberRole))
			return;

		String mentionUser = event.getUser().getAsMention();
		String serverName = IDAndEntities.cartolandServer.getName();

		IDAndEntities.lobbyChannel.sendMessage("歡迎 " + mentionUser + " 加入 " + serverName +
														   "\n" + mentionUser + ", welcome to " + serverName).queue();
	}
}