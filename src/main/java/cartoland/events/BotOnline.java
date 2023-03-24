package cartoland.events;

import cartoland.utilities.CommandBlocksHandle;
import cartoland.utilities.FileHandle;
import cartoland.utilities.QuestionForumHandle;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static cartoland.utilities.IDAndEntities.*;

/**
 * {@code BotOnline} is a listener that triggers when this bot was online. This class was registered in
 * {@link cartoland.Cartoland#main}, with the build of JDA. This class helps all the entities (except
 * {@link cartoland.utilities.IDAndEntities#jda}) in {@link cartoland.utilities.IDAndEntities} get their instances.
 *
 * @since 1.0
 * @author Alex Cai
 */
public class BotOnline extends ListenerAdapter
{
	/**
	 * The method that inherited from {@link ListenerAdapter}, triggers when the bot was online. It will send
	 * online message to bot channel.
	 *
	 * @param event The event that carries information.
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

		memberRole = cartolandServer.getRoleById(MEMBER_ROLE_ID); //會員身分組
		if (memberRole == null)
			problemOccurred("Can't find Member Role.");

		nsfwRole = cartolandServer.getRoleById(NSFW_ROLE_ID); //地下身分組
		if (nsfwRole == null)
			problemOccurred("Can't find NSFW Role.");

		botItself = jda.getSelfUser(); //機器人自己

		ohBoy3AM(); //好棒 三點了

		idleFormPost12PM(); //中午十二點時處理未解決的論壇貼文

		initialIDAndName(); //初始化idAndName

		botChannel.sendMessage("Cartoland Bot 已上線。\nCartoland Bot is now online.").queue();
		String logString = "online";
		System.out.println(logString);
		FileHandle.log(logString);
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
		System.err.print('\u0007');
		FileHandle.log(logString);
		jda.shutdownNow();
		throw new NullPointerException();
	}

	private long secondsUntil(int hour)
	{
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime untilTime = now.withHour(hour).withMinute(0).withSecond(0);

		if (now.compareTo(untilTime) > 0)
			untilTime = untilTime.plusDays(1L);

		return Duration.between(now, untilTime).getSeconds();
	}

	//https://stackoverflow.com/questions/65984126
	private void ohBoy3AM()
	{
		long secondsUntil3AM = secondsUntil(3);

		threeAMTask = scheduleExecutor.scheduleAtFixedRate(
			() -> undergroundChannel.sendMessage("https://imgur.com/EGO35hf").queue(),
			secondsUntil3AM, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
	}

	private void idleFormPost12PM()
	{
		long secondsUntil12PM = secondsUntil(12);

		twelvePMTask = scheduleExecutor.scheduleAtFixedRate(() -> questionsChannel.getThreadChannels()
				.stream()
				.filter(QuestionForumHandle::forumPostShouldIdle)
				.forEach(QuestionForumHandle::idleForumPost),
			secondsUntil12PM, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
	}

	private void initialIDAndName()
	{
		HashMap<Long, Long> commandBlockMap = CommandBlocksHandle.getMap();
		List<CacheRestAction<User>> retrieve = new ArrayList<>(commandBlockMap.size());
		commandBlockMap.keySet().forEach(userID -> retrieve.add(jda.retrieveUserById(userID)));
		if (retrieve.size() > 0)
			RestAction.allOf(retrieve).queue(users -> users.forEach(user -> idAndNames.put(user.getIdLong(), user.getAsTag())));
		CommandBlocksHandle.changed = true;
	}
}