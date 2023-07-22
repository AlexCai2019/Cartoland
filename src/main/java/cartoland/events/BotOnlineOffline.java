package cartoland.events;

import cartoland.Cartoland;
import cartoland.utilities.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
	 * The method that inherited from {@link ListenerAdapter}, triggers when the bot was online. It will start
	 * schedule events and send online message to bot channel.
	 *
	 * @param event The event that carries information.
	 * @since 1.0
	 * @author Alex Cai
	 */
	@Override
	public void onReady(@NotNull ReadyEvent event)
	{
		CommandBlocksHandle.initial(); //初始化idAndName

		TimerHandle.registerTimerEvent((byte) 0, () -> //半夜12點
		{
			List<Long> birthdayMembersID = TimerHandle.todayBirthdayMembers(); //今天生日的成員們的ID
			if (birthdayMembersID.isEmpty()) //今天沒有人生日
				return;
			if (birthdayMembersID.size() > 100) //一次只能100人
				birthdayMembersID = birthdayMembersID.subList(0, 100); //超過100人 只能取前100個
			Guild cartoland = Cartoland.getJDA().getGuildById(IDs.CARTOLAND_SERVER_ID); //創聯
			if (cartoland == null)
				return;
			TextChannel lobbyChannel = cartoland.getTextChannelById(IDs.LOBBY_CHANNEL_ID); //大廳頻道
			if (lobbyChannel == null)
				return;
			final List<Long> finalBirthdayMembersID = birthdayMembersID; //lambda要用
			cartoland.retrieveMembersByIds(birthdayMembersID).onSuccess(members -> //獲取所有的生日成員們
			{
				for (Member member : members) //走訪所有成員們
				{
					String nickname = member.getNickname(); //暱稱
					String name = member.getUser().getEffectiveName(); //全域名稱 沒有設定的話就是名稱
					if (nickname != null) //有設定暱稱
						lobbyChannel.sendMessage("今天是 " + nickname + '(' + name + ") 的生日！").queue(); //後面備註全域名稱/名稱
					else //沒有設定暱稱
						lobbyChannel.sendMessage("今天是 " + name + " 的生日！").queue(); //直接顯示全域名稱/名稱
				}

				int birthdayMembersCount = finalBirthdayMembersID.size();
				if (members.size() == birthdayMembersCount)
					return; //沒有人變少
				Set<Long> membersIDSet = members.stream().map(Member::getIdLong).collect(Collectors.toSet());
				for (int i = birthdayMembersCount - 1; i >= 0; i--)
					if (!membersIDSet.contains(finalBirthdayMembersID.get(i)))
						finalBirthdayMembersID.remove(i);
			});
		});

		TimerHandle.registerTimerEvent((byte) 3, () -> //凌晨3點
		{
			TextChannel undergroundChannel = Cartoland.getJDA().getTextChannelById(IDs.UNDERGROUND_CHANNEL_ID);
			if (undergroundChannel == null)
				return;
			undergroundChannel.sendMessage("https://i.imgur.com/c0HCirP.jpg").queue(); //誰會想在凌晨三點吃美味蟹堡
			undergroundChannel.sendMessage("https://i.imgur.com/EGO35hf.jpg").queue(); //好棒，三點了
		}); //好棒 三點了

		TimerHandle.registerTimerEvent((byte) 12, () -> //中午12點
		{
			ForumChannel questionsChannel = Cartoland.getJDA().getForumChannelById(IDs.QUESTIONS_CHANNEL_ID);
			if (questionsChannel == null)
				return;
			List<ThreadChannel> forumPosts = questionsChannel.getThreadChannels(); //論壇貼文們
			for (ThreadChannel forumPost : forumPosts) //走訪論壇貼文們
				ForumsHandle.tryIdleQuestionForumPost(forumPost); //試著讓它們idle
		}); //中午十二點時處理並提醒未解決的論壇貼文

		TextChannel botChannel = event.getJDA().getTextChannelById(IDs.BOT_CHANNEL_ID);
		if (botChannel != null)
			botChannel.sendMessage("Cartoland Bot 已上線。\nCartoland Bot is now online.").queue();
		String logString = "online";
		System.out.println(logString);
		FileHandle.log(logString);
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
		FileHandle.closeLog();
	}
}