package cartoland.events;

import cartoland.utilities.IDAndEntities;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * {@code JoinServer} is a listener that triggers when a user joined a server that the bot is in. This class was registered
 * in {@link cartoland.Cartoland#main}, with the build of JDA.
 *
 * @since 1.5
 * @author Alex Cai
 */
public class JoinServer extends ListenerAdapter
{
	@Override
	public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event)
	{
		if (event.getGuild().getIdLong() != IDAndEntities.CARTOLAND_SERVER_ID)
			return;
		User user = event.getUser();
		if (user.isBot() || user.isSystem() || !user.hasPrivateChannel())
			return;

		user.openPrivateChannel().flatMap(privateChannel ->
		{
			 String userName = user.getName();
			 String serverName = IDAndEntities.cartolandServer.getName();
			 privateChannel.sendMessage("歡迎你，" + userName + "，來到 " + serverName + "\n" +
												"記得先詳閱 <#973898745777377330> 內的訊息，並遵守一切公告規則。\n" +
												userName + ", welcome to " + serverName + ".\n" +
												"Please read messages in <#973898745777377330>, and follow all rules.").queue();
			 return null;
		}).queue();
	}
}
