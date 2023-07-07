package cartoland.events;

import cartoland.Cartoland;
import cartoland.utilities.*;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static cartoland.utilities.IDs.*;

/**
 * {@code BotOnlineOffline} is a listener that triggers when this bot went online or went offline normally. It won't
 * trigger if this bot was shutdown by accident, such as killed by ^C, server shutdown, etc. This class was
 * registered in {@link cartoland.Cartoland#main}, with the build of JDA. The {@link #onReady} starts schedule functions,
 * and the {@link #onShutdown} method help synchronize JSONObjects and their files.
 *
 * @since 1.0
 * @author Alex Cai
 */
public class BotOnlineOffline extends ListenerAdapter
{
	/**
	 * The method that inherited from {@link ListenerAdapter}, triggers when the bot was online. It will initialize
	 * entities in {@link IDs}, start schedule events and send online message to bot channel.
	 *
	 * @param event The event that carries information.
	 * @since 1.0
	 * @author Alex Cai
	 */
	@Override
	public void onReady(@NotNull ReadyEvent event)
	{
		CommandBlocksHandle.initial(); //初始化idAndName

		TimerHandle.registerTimerEvent((byte) 3, () ->
		{
			TextChannel undergroundChannel = Cartoland.getJDA().getTextChannelById(UNDERGROUND_CHANNEL_ID);
			if (undergroundChannel == null)
				return;
			undergroundChannel.sendMessage("https://i.imgur.com/c0HCirP.jpg").queue(); //誰會想在凌晨三點吃美味蟹堡
			undergroundChannel.sendMessage("https://i.imgur.com/EGO35hf.jpg").queue(); //好棒，三點了
		}); //好棒 三點了

		TimerHandle.registerTimerEvent((byte) 12, () ->
		{
			ForumChannel questionsChannel = Cartoland.getJDA().getForumChannelById(QUESTIONS_CHANNEL_ID);
			if (questionsChannel == null)
				return;
			List<ThreadChannel> forumPosts = questionsChannel.getThreadChannels(); //論壇貼文們
			for (ThreadChannel forumPost : forumPosts) //走訪論壇貼文們
				QuestionForumHandle.tryIdleForumPost(forumPost); //試著讓它們idle
		}); //中午十二點時處理並提醒未解決的論壇貼文

		TextChannel botChannel = event.getJDA().getTextChannelById(BOT_CHANNEL_ID);
		if (botChannel != null)
			botChannel.sendMessage("Cartoland Bot 已上線。\nCartoland Bot is now online.").queue();
		String logString = "online";
		System.out.println(logString);
		FileHandle.log(logString);
	}

	/**
	 * The method that inherited from {@link ListenerAdapter}. When the bot go offline normally, it will shut
	 * down scheduled events, log "offline" to terminal & log file, serialize idle forum posts and write
	 * JSONObject into users.json & command_blocks.json.
	 *
	 * @param event Information about the shutdown.
	 * @since 1.0
	 * @author Alex Cai
	 */
	@Override
	public void onShutdown(@NotNull ShutdownEvent event)
	{
		CommandBlocksHandle.optimizeMap(); //最佳化指令方塊紀錄

		FileHandle.serialize(); //所有有註冊的物件

		TimerHandle.stopTimer(); //停止每小時的事件執行緒

		String logString = "offline";
		System.out.println(logString);
		FileHandle.log(logString);
		FileHandle.closeLog();
	}
}