package cartoland.events;

import cartoland.utilities.*;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static cartoland.utilities.IDAndEntities.*;

/**
 * {@code BotOnlineOffline} is a listener that triggers when this bot went online or went offline normally. It won't
 * trigger if this bot was shutdown by accident, such as killed by ^C, server shutdown, etc. This class was
 * registered in {@link cartoland.Cartoland#main}, with the build of JDA. The {@link #onReady} method helps all the entities
 * (except {@link IDAndEntities#jda}) in {@link IDAndEntities} get their instances, and the {@link #onShutdown}
 * method help synchronize JSONObjects and their files.
 *
 * @since 1.0
 * @author Alex Cai
 */
public class BotOnlineOffline extends ListenerAdapter
{
	/**
	 * The method that inherited from {@link ListenerAdapter}, triggers when the bot was online. It will initialize
	 * entities in {@link IDAndEntities}, start schedule events and send online message to bot channel.
	 *
	 * @param event The event that carries information.
	 * @since 1.0
	 * @author Alex Cai
	 */
	@Override
	public void onReady(@NotNull ReadyEvent event)
	{
		cartolandServer = jda.getGuildById(CARTOLAND_SERVER_ID); //創聯
		if (cartolandServer == null)
			problemOccurred("Can't find Cartoland Server");

		questionsChannel = cartolandServer.getForumChannelById(QUESTIONS_CHANNEL_ID);
		if (questionsChannel == null)
			problemOccurred("Can't find Questions Channel.");

		voteKickChannel = cartolandServer.getForumChannelById(VOTE_KICK_CHANNEL_ID);
		if (voteKickChannel == null)
			problemOccurred("Can't find Vote Kick Channel.");

		lobbyChannel = cartolandServer.getTextChannelById(LOBBY_CHANNEL_ID); //創聯的大廳頻道
		if (lobbyChannel == null)
			problemOccurred("Can't find Lobby Channel.");

		botChannel = cartolandServer.getTextChannelById(BOT_CHANNEL_ID); //創聯的機器人頻道
		if (botChannel == null)
			problemOccurred("Can't find Bot Channel.");

		undergroundChannel = cartolandServer.getTextChannelById(UNDERGROUND_CHANNEL_ID); //創聯的地下聊天室
		if (undergroundChannel == null)
			problemOccurred("Can't find Underground Channel.");

		resolvedForumTag = questionsChannel.getAvailableTagById(RESOLVED_FORUM_TAG_ID);
		if (resolvedForumTag == null)
			problemOccurred("Can't find Resolved Forum Tag");

		unresolvedForumTag = questionsChannel.getAvailableTagById(UNRESOLVED_FORUM_TAG_ID);
		if (unresolvedForumTag == null)
			problemOccurred("Can't find Unresolved Forum Tag");

		godOfGamblersRole = cartolandServer.getRoleById(GOD_OF_GAMBLERS_ROLE_ID); //賭神身分組
		if (godOfGamblersRole == null)
			problemOccurred("Can't find god Of Gamblers Role.");

		memberRole = cartolandServer.getRoleById(MEMBER_ROLE_ID); //會員身分組
		if (memberRole == null)
			problemOccurred("Can't find Member Role.");

		nsfwRole = cartolandServer.getRoleById(NSFW_ROLE_ID); //地下身分組
		if (nsfwRole == null)
			problemOccurred("Can't find NSFW Role.");

		botItself = jda.getSelfUser(); //機器人自己

		CommandBlocksHandle.initial(); //初始化idAndName

		TimerHandle.registerTimerEvent((byte) 3, () ->
		{
			undergroundChannel.sendMessage("https://i.imgur.com/c0HCirP.jpg").queue(); //誰會想在凌晨三點吃美味蟹堡
			undergroundChannel.sendMessage("https://i.imgur.com/EGO35hf.jpg").queue(); //好棒，三點了
		}); //好棒 三點了

		TimerHandle.registerTimerEvent((byte) 12, () ->
		{
			List<ThreadChannel> forumPosts = questionsChannel.getThreadChannels(); //論壇貼文們
			for (ThreadChannel forumPost : forumPosts) //走訪論壇貼文們
				QuestionForumHandle.tryIdleForumPost(forumPost); //試著讓它們idle
		}); //中午十二點時處理並提醒未解決的論壇貼文

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
		//https://stackoverflow.com/questions/34202701

		FileHandle.serialize(); //所有有註冊的物件

		TimerHandle.stopTimer();

		String logString = "offline";
		System.out.println(logString);
		FileHandle.log(logString);
		FileHandle.closeLog();
	}

	/**
	 * When an error occurred, an entity is null.
	 *
	 * @param logString The content that will print to standard error stream and log file.
	 * @throws NullPointerException always throw
	 */
	private void problemOccurred(String logString)
	{
		System.err.println(logString);
		FileHandle.log(logString);
		jda.shutdownNow();
		throw new NullPointerException();
	}
}