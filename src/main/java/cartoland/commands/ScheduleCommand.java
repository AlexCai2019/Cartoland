package cartoland.commands;

import cartoland.utilities.TimerHandle;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.List;

public class ScheduleCommand extends HasSubcommands
{
	public static final String CREATE = "create";
	public static final String DELETE = "delete";
	public static final String LIST = "list";

	public ScheduleCommand()
	{
		super(3);
		subcommands.put(CREATE, new CreateSubCommand());
		subcommands.put(DELETE, event ->
		{
			List<TimerHandle.TimerEvent> scheduledEvents = TimerHandle.TimerEvent.scheduledEvents(); //所有排程事件
			if (scheduledEvents.isEmpty())
			{
				event.reply("There's no any scheduled events!").queue();
				return;
			}

			String scheduledEventName = event.getOption("name", " ", OptionMapping::getAsString); //排程事件名稱

			int found = 0;
			for (TimerHandle.TimerEvent timerEvent : scheduledEvents) //走訪所有排程事件
			{
				if (timerEvent.getName().equals(scheduledEventName)) //名字一樣
				{
					timerEvent.unregister(); //移除事件
					found++; //計數 + 1 畢竟允許重名
				}
			}

			if (found == 0) //如果不曾schedule過該名稱的事件
			{
				event.reply("There's no `" + scheduledEventName + "` event!").queue();
				return;
			}

			//如果有找到
			if (found > 1)
				event.reply("Removed " + found + " scheduled `" + scheduledEventName + "` messages.").queue();
			else
				event.reply("Removed scheduled `" + scheduledEventName + "` message.").queue();
		});
		subcommands.put(LIST, event ->
		{
			List<TimerHandle.TimerEvent> events = TimerHandle.TimerEvent.scheduledEvents(); //事件們
			if (events.isEmpty()) //如果沒有事件名稱 必須至少回覆一個字 否則會卡在deferReply
			{
				event.reply("There's no scheduled messages!").setEphemeral(true).queue();
				return;
			}

			StringBuilder replyString = new StringBuilder("```\n"); //回覆字串
			for (TimerHandle.TimerEvent timerEvent : events)
				replyString.append(timerEvent.getName()).append('\n');
			event.reply(replyString.append("```").toString()).queue();
		});
	}

	private static class CreateSubCommand implements ICommand
	{
		@Override
		public void commandProcess(SlashCommandInteractionEvent event)
		{
			int time = event.getOption("time", 0, OptionMapping::getAsInt); //時間 介於0到23之間
			if (time < 0 || time > 23) //不得超出範圍
			{
				event.reply("Time must between 0 and 23!").setEphemeral(true).queue();
				return;
			}

			GuildChannel guildChannel = event.getOption("channel", OptionMapping::getAsChannel); //頻道
			if (guildChannel == null)
			{
				event.reply("Error: The channel might be deleted, or I don't have permission to access it.").setEphemeral(true).queue();
				return;
			}
			if (!guildChannel.getType().isMessage()) //必須要是訊息頻道
			{
				event.reply("Please input a message channel!").setEphemeral(true).queue();
				return;
			}
			GuildMessageChannel guildMessageChannel = (GuildMessageChannel)guildChannel;
			if (!guildMessageChannel.canTalk()) //不能說話
			{
				event.reply("I don't have the permission to view or talk in this channel!").setEphemeral(true).queue();
				return;
			}

			String content = event.getOption("content", " ", OptionMapping::getAsString); //內容
			String truncate, ellipsis;
			if (content.length() > 20) //訊息太長就取其前20個字
			{
				truncate = content.substring(0, 20);
				ellipsis = "…";
			}
			else
			{
				truncate = content;
				ellipsis = "";
			}
			String name = guildChannel.getName() + '_' + time + '_' + truncate; //頻道名_時間_開頭前20個字
			boolean once = event.getOption("once", Boolean.FALSE, OptionMapping::getAsBoolean); //是否為一次性

			TimerHandle.TimerEvent timerEvent = new TimerHandle.TimerEvent(time, name, content, guildChannel.getIdLong());
			timerEvent.setOnce(once); //設定是否為一次性
			timerEvent.register(); //註冊

			event.reply("The bot will send \"" + truncate + ellipsis + "\" to " + guildChannel.getAsMention() + " at " + time + (once ? " once." : " everyday.")).queue();
		}
	}
}