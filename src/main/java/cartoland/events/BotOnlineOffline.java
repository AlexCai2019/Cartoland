package cartoland.events;

import cartoland.buttons.IButton;
import cartoland.utilities.*;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

/**
 * {@code BotOnlineOffline} is a listener that triggers when this bot went online or went offline normally. It won't
 * trigger if this bot was shutdown by accident, such as killed by ^C, server shutdown, etc. This class was
 * registered in {@link cartoland.Cartoland#main(String[])}, with the build of JDA. The {@link #onReady(ReadyEvent)}
 * invokes {@link CommandBlocksHandle#initial()}, and the {@link #onShutdown(ShutdownEvent)} method helps serialize
 * objects and stop scheduled functions.
 *
 * @since 1.0
 * @author Alex Cai
 */
public class BotOnlineOffline extends ListenerAdapter
{
	private final boolean initialCommandBlocks;

	public BotOnlineOffline(boolean initialCommandBlocks)
	{
		this.initialCommandBlocks = initialCommandBlocks;
	}

	/**
	 * The method that inherited from {@link ListenerAdapter}, triggers when the bot was online. It will send online
	 * message to bot channel.
	 *
	 * @param event The event that carries information.
	 * @since 1.0
	 * @author Alex Cai
	 */
	@Override
	public void onReady(@NotNull ReadyEvent event)
	{
		if (initialCommandBlocks)
			CommandBlocksHandle.initial(); //初始化idAndName

		TextChannel botChannel = event.getJDA().getTextChannelById(IDs.BOT_CHANNEL_ID);
		if (botChannel != null)
			botChannel.sendMessage("Cartoland Bot 已上線。\nCartoland Bot is now online.").queue();
		String logString = "online";
		System.out.println(logString);
		FileHandle.log(logString);


		var dtpChannel = event.getJDA().getNewsChannelById(IDs.DATAPACK_SHOWCASE_CHANNEL_ID);
		if (dtpChannel == null)
			return;
		final Button archiveButton = Button.success(IButton.ARCHIVE_THREAD, "Archive Thread")
				.withEmoji(Emoji.fromUnicode("📁"));
		final Button renameButton = Button.primary(IButton.RENAME_THREAD, "Edit Title")
				.withEmoji(Emoji.fromUnicode("✏️"));
		dtpChannel.retrieveMessageById(1252810824641155123L).queue(message ->
		{
			String name = message.getAuthor().getEffectiveName();
			message.createThreadChannel(name + '(' + TimerHandle.getDateString() + ')')
					.flatMap(thread -> thread.sendMessage("Thread automatically created by " + name + " in " + dtpChannel.getAsMention()).addActionRow(archiveButton, renameButton))
					.flatMap(Message::pin)
					.queue();
		});
		dtpChannel.retrieveMessageById(1252832677455986768L).queue(message ->
		{
			String name = message.getAuthor().getEffectiveName();
			message.createThreadChannel(name + '(' + TimerHandle.getDateString() + ')')
					.flatMap(thread -> thread.sendMessage("Thread automatically created by " + name + " in " + dtpChannel.getAsMention()).addActionRow(archiveButton, renameButton))
					.flatMap(Message::pin)
					.queue();
		});
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

		String logString = "offline";
		System.out.println(logString);
		FileHandle.log(logString);
		FileHandle.flushLog();
	}
}