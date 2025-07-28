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
			String scheduledEventName = event.getOption("name", " ", OptionMapping::getAsString); //事件名稱

			int found = 0;
			for (TimerHandle.TimerEvent timerEvent : TimerHandle.scheduledEvents())
			{
				if (timerEvent.getName().equals(scheduledEventName))
				{
					timerEvent.unregister(); //移除事件
					found++;
				}
			}

			if (found != 0) //如果曾有schedule過該名稱的事件
				event.reply("Removed " + found + " scheduled `" + scheduledEventName + "` message(s).").queue();
			else
				event.reply("There's no `" + scheduledEventName + "` event!").queue();
		});
		subcommands.put(LIST, event ->
		{
			List<TimerHandle.TimerEvent> events = TimerHandle.scheduledEvents(); //事件們
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
			int contentLength = content.length(); //訊息的長度
			String first20Characters = contentLength <= 20 ? content : content.substring(0, 20); //取其前20個字
			String name = guildChannel.getName() + '_' + time + '_' + first20Characters; //頻道名_時間_開頭前20個字
			boolean once = event.getOption("once", Boolean.FALSE, OptionMapping::getAsBoolean); //是否為一次性
			TimerHandle.TimerEvent timerEvent = new TimerHandle.TimerEvent(time, name, content, guildChannel.getIdLong());
			timerEvent.setOnce(once); //設定是否為一次性
			timerEvent.register(); //註冊

			event.reply("The bot will send \"" + first20Characters + (contentLength > 20 ? "…" : "") + "\" to " + guildChannel.getAsMention() + " at " + time + (once ? " once." : " everyday.")).queue();
		}
	}
}