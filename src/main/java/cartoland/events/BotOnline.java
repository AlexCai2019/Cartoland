package cartoland.events;

import cartoland.utilities.FileHandle;
import cartoland.utilities.IDAndEntities;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class BotOnline extends ListenerAdapter
{
	@Override
	public void onReady(@NotNull ReadyEvent event)
	{
		IDAndEntities.botChannel = IDAndEntities.jda.getChannelById(TextChannel.class, IDAndEntities.BOT_CHANNEL_ID); //創聯的機器人頻道
		String logString;
		if (IDAndEntities.botChannel != null)
		{
			logString = "Cartoland Bot is now online.";
			IDAndEntities.botChannel.sendMessage(logString).queue();
			System.out.println(logString);
			FileHandle.logIntoFile(logString);
		}
		else
		{
			logString = "Can't find Bot Channel.";
			System.err.println(logString);
			FileHandle.logIntoFile(logString);
			System.exit(-1);
		}

		IDAndEntities.undergroundChannel = IDAndEntities.jda.getChannelById(TextChannel.class, IDAndEntities.UNDERGROUND_CHANNEL_ID); //地下聊天室
		if (IDAndEntities.undergroundChannel == null)
		{
			logString = "Can't find Underground Channel.";
			System.err.println(logString);
			FileHandle.logIntoFile(logString);
			System.exit(-1);
		}

		IDAndEntities.botItself = IDAndEntities.jda.getSelfUser();
	}
}