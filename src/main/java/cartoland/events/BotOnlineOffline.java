package cartoland.events;

import cartoland.utilities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@code BotOnlineOffline} is a listener that triggers when this bot went online or went offline normally. It won't
 * trigger if this bot was shutdown by accident, such as killed by ^C, server shutdown, etc. This class was
 * registered in {@link cartoland.Cartoland#main(String[])}, with the build of JDA. The {@link #onReady(ReadyEvent)}
 * invokes {@link CommandBlocksHandle#initial()} if the second argument of {@link cartoland.Cartoland#main(String[])} is
 * true. The {@link #onShutdown(ShutdownEvent)} method helps serialize objects and stop scheduled functions.
 *
 * @since 1.0
 * @author Alex Cai
 */
public class BotOnlineOffline extends ListenerAdapter
{
	private static final Logger logger = LoggerFactory.getLogger(BotOnlineOffline.class);

	private final boolean shouldInitial;
	private final String bootType;

	public BotOnlineOffline(String initial, String bootType)
	{
		shouldInitial = Boolean.parseBoolean(initial);
		this.bootType = bootType;
	}

	/**
	 * The method that inherited from {@link ListenerAdapter}, triggers when the bot was online. It will send online
	 * message to bot channel if the third argument of {@link cartoland.Cartoland#main(String[])} isn't "true".
	 *
	 * @param event The event that carries information.
	 * @since 1.0
	 * @author Alex Cai
	 */
	@Override
	public void onReady(@NotNull ReadyEvent event)
	{
		if (shouldInitial)
			CommandBlocksHandle.initial(); //初始化idAndName

		TextChannel botChannel = event.getJDA().getTextChannelById(IDs.BOT_CHANNEL_ID);
		if (botChannel != null)
		{
			switch (bootType)
			{
				case "reboot":
					botChannel.sendMessage("Cartoland Bot 重啟完畢。\nCartoland Bot rebooted.").queue();
					break;
				case "start":
					botChannel.sendMessage("Cartoland Bot 已上線。\nCartoland Bot is now online.").queue();
					break;
			}
		}
		logger.info("online");
	}

	/**
	 * The method that inherited from {@link ListenerAdapter}. When the bot go offline normally, it will shut
	 * down scheduled events, log "offline" to terminal & log file, serialize registered objects.
	 *
	 * @param event Information about the shutdown.
	 * @since 1.0
	 * @author Alex Cai
	 */
	@Override
	public void onShutdown(@NotNull ShutdownEvent event)
	{
		FileHandle.serialize(); //所有有註冊的物件

		TimerHandle.stopTimer(); //停止每小時的事件執行緒

		logger.info("offline");
	}
}