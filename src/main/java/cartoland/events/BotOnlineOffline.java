package cartoland.events;

import cartoland.Cartoland;
import cartoland.utilities.*;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
	private void birthdayMembers(List<Long> birthdayMembersIDs, TextChannel lobbyChannel)
	{
		lobbyChannel.getGuild().retrieveMembersByIds(birthdayMembersIDs).onSuccess(members -> //獲取所有的生日成員們
		{
			StringBuilder builder = new StringBuilder();
			int membersCount = members.size();
			//最後把剛剛沒說的寄出去
			if (membersCount <= 25) //由於Discord取名最多可以到32個字 因此每個人最多會占用77個字元 而Discord一次輸入的上限是2000字 77 * 25 = 1925
			{
				for (Member member : members) //走訪所有成員們
					buildBirthdayString(member, builder);
				lobbyChannel.sendMessage(builder).queue(); //將生日祝賀合併為一則訊息
				return;
			}

			//這以下是人數超過25人的應對方法
			for (int i = 0, j = 0; i < membersCount; i++, j++)
			{
				if (j == 25) //當到第26人時 先把第1到第25人寄出 但陣列是從0開始的
				{
					lobbyChannel.sendMessage(builder).complete(); //先送出一次生日祝賀 要等它完成後才能重設builder
					builder.setLength(0); //重設builder
					j = 0;
				}
				buildBirthdayString(members.get(i), builder);
			}
			lobbyChannel.sendMessage(builder).queue(); //把剛剛有累積到 不滿26人的寄出
		});
	}

	private void buildBirthdayString(Member member, StringBuilder builder)
	{
		String nickname = member.getNickname(); //暱稱
		String name = member.getUser().getEffectiveName(); //全域名稱 沒有設定的話就是名稱
		if (nickname != null) //有設定暱稱
			builder.append("今天是 ").append(nickname).append('(').append(name).append(") 的生日！\n"); //後面備註全域名稱/名稱
		else //沒有設定暱稱
			builder.append("今天是 ").append(name).append(" 的生日！\n"); //直接顯示全域名稱/名稱
	}

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
			FileHandle.changeLogDate(); //更換log的日期

			//這以下是和生日有關的
			List<Long> birthdayMembersID = TimerHandle.todayBirthdayMembers(); //今天生日的成員們的ID
			if (birthdayMembersID.isEmpty()) //今天沒有人生日
				return;
			TextChannel lobbyChannel = Cartoland.getJDA().getTextChannelById(IDs.LOBBY_CHANNEL_ID); //大廳頻道
			if (lobbyChannel == null) //找不到大廳頻道
				return;
			int totalMembers = birthdayMembersID.size();
			if (totalMembers <= 100) //小於等於100人
			{
				birthdayMembers(birthdayMembersID, lobbyChannel); //直接放下去跑就好
				return;
			}

			int membersRange;
			for (membersRange = 0; membersRange + 100 < totalMembers; membersRange += 100) //一次只能100人
				birthdayMembers(birthdayMembersID.subList(membersRange, membersRange + 100), lobbyChannel); //每次取100個
			birthdayMembers(birthdayMembersID.subList(membersRange, totalMembers), lobbyChannel); //最後不滿100人
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
		FileHandle.startLog();
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