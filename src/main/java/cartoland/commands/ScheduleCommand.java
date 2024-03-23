package cartoland.commands;

import cartoland.Cartoland;
import cartoland.utilities.CommonFunctions;
import cartoland.utilities.TimerHandle;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Set;

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
			String scheduledEventName = event.getOption("name", " ", CommonFunctions.getAsString); //事件名稱
			if (TimerHandle.hasScheduledEvent(scheduledEventName)) //如果曾有schedule過該名稱的事件
			{
				event.reply("Removed scheduled " + scheduledEventName + " message.").queue();
				TimerHandle.unregisterScheduledEvent(scheduledEventName); //移除事件
			}
			else
				event.reply("There's no " + scheduledEventName + " event!").queue();
		});
		subcommands.put(LIST, event ->
		{
			Set<String> eventNames = TimerHandle.scheduledEventsNames(); //事件名稱們
			if (eventNames.isEmpty()) //如果沒有事件名稱 必須至少回覆一個字 否則會卡在deferReply
				event.reply("There's no scheduled messages!").setEphemeral(true).queue();
			else
				event.reply(String.join("\n", eventNames)).queue();
		});
	}

	private static class CreateSubCommand implements ICommand
	{
		@Override
		public void commandProcess(SlashCommandInteractionEvent event)
		{
			int time = event.getOption("time", 0, CommonFunctions.getAsInt); //時間 介於0到23之間
			if (time < 0 || time > 23) //不得超出範圍
			{
				event.reply("Time must between 0 and 23!").setEphemeral(true).queue();
				return;
			}

			GuildChannel guildChannel = event.getOption("channel", CommonFunctions.getAsChannel); //頻道
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
			if (!guildMessageChannel.canTalk())
			{
				event.reply("I don't have the permission to view or talk in this channel!").setEphemeral(true).queue();
				return;
			}

			String content = event.getOption("content", " ", CommonFunctions.getAsString); //內容
			int contentLength = content.length(); //訊息的長度
			String first20Characters = contentLength <= 20 ? content : content.substring(0, 20); //取其前20個字
			String name = guildChannel.getName() + '_' + time + '_' + first20Characters; //頻道名_時間_開頭前20個字

			if (TimerHandle.hasScheduledEvent(name)) //如果已經註冊過這個事件名稱了
			{
				event.reply("Already has " + name + " event!").setEphemeral(true).queue();
				return;
			}

			boolean once = event.getOption("once", Boolean.FALSE, CommonFunctions.getAsBoolean); //是否為一次性

			long channelID = guildChannel.getIdLong(); //頻道ID
			Runnable sendMessageToChannel = () -> //事件內容的Runnable
			{
				MessageChannel channel = Cartoland.getJDA().getChannelById(MessageChannel.class, channelID); //尋找頻道
				if (channel != null) //如果找到頻道
					channel.sendMessage(content).queue(); //發送訊息
			};
			TimerHandle.registerScheduledEvent(name, new TimerHandle.TimerEvent((byte) time, once ? () -> //如果是一次性
			{
				sendMessageToChannel.run(); //執行Runnable
				TimerHandle.unregisterScheduledEvent(name); //執行完後刪除事件
			} : sendMessageToChannel)); //不是一次性 就直接把Runnable傳入

			event.reply("The bot will send \"" + first20Characters + (contentLength > 20 ? "…" : "") + "\" to " + guildChannel.getAsMention() + " at " + time + (once ? " once." : " everyday.")).queue();
		}
	}
}