package cartoland.events;

import cartoland.utilities.CommandBlocksHandle;
import cartoland.utilities.FileHandle;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

		String logString = "Cartoland Bot is now online.";
		botChannel.sendMessage(logString).queue();
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

	//https://stackoverflow.com/questions/65984126
	private void ohBoy3AM()
	{
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime threeAM = now.withHour(3).withMinute(0).withSecond(0);

		if (now.compareTo(threeAM) > 0)
			threeAM = threeAM.plusDays(1L);

		long secondsUntil3AM = Duration.between(now, threeAM).getSeconds();

		threeAMTask = scheduleExecutor.scheduleAtFixedRate(
				() -> undergroundChannel.sendMessage("https://imgur.com/EGO35hf").queue(),
				secondsUntil3AM, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
	}

	private final Emoji reminder_ribbon = Emoji.fromUnicode("🎗️");

	private void idleFormPost12PM()
	{
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime twelvePM = now.withHour(12).withMinute(0).withSecond(0);

		if (now.compareTo(twelvePM) > 0)
			twelvePM = twelvePM.plusDays(1L);

		long secondsUntil12PM = Duration.between(now, twelvePM).getSeconds();

		twelvePMTask = scheduleExecutor.scheduleAtFixedRate(() -> questionsChannel.getThreadChannels().forEach(forumPost ->
		{
			if (forumPost.isArchived())
				return;

			forumPost.retrieveMessageById(forumPost.getLatestMessageIdLong()).queue(lastMessage ->
			{
				Member messageCreatorMember = lastMessage.getMember();
				if (messageCreatorMember == null)
					return;
				User messageCreatorUser = messageCreatorMember.getUser();
				if (messageCreatorUser.isBot() || messageCreatorUser.isSystem())
					return;

				if (Duration.between(lastMessage.getTimeCreated(), OffsetDateTime.now()).toHours() < 24L)
					return;

				Member owner = forumPost.getOwner();
				if (owner == null)
					return;

				String mentionOwner = owner.getAsMention();
				forumPost.sendMessage(mentionOwner + "，你的問題解決了嗎？如果已經解決了，記得使用`:resolved:`表情符號關閉貼文。\n" +
											  "如果還沒解決，可以嘗試在問題中加入更多資訊。")
						.queue(message -> message.addReaction(reminder_ribbon).queue());
			});

		}), secondsUntil12PM, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
	}

	private void initialIDAndName()
	{
		Map<String, Object> commandBlockMap = CommandBlocksHandle.getMap();
		List<CacheRestAction<User>> retrieve = new ArrayList<>(commandBlockMap.size());
		commandBlockMap.forEach((userIDString, blocks) -> retrieve.add(jda.retrieveUserById(userIDString)));
		if (retrieve.size() > 0)
			RestAction.allOf(retrieve).queue(users -> users.forEach(user -> idAndNames.put(user.getIdLong(), user.getAsTag())));
		CommandBlocksHandle.changed = true;
	}
}