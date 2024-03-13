package cartoland.commands;

import cartoland.Cartoland;
import cartoland.utilities.CommonFunctions;
import cartoland.utilities.TimerHandle;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

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
			String scheduledEventName = event.getOption("name", " ", CommonFunctions.getAsString);
			if (TimerHandle.hasScheduledEvent(scheduledEventName))
			{
				event.reply("Removed scheduled " + scheduledEventName + " message.").queue();
				TimerHandle.unregisterScheduledEvent(scheduledEventName);
			}
			else
				event.reply("There's no " + scheduledEventName + " event!").queue();
		});
		subcommands.put(LIST, event -> event.reply(String.join("\n", TimerHandle.scheduledEventsNames())).queue());
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
			int contentLength = content.length();
			String first20Characters = content.substring(0, Math.min(20, contentLength));
			String name = guildChannel.getName() + '_' + time + '_' + first20Characters; //頻道名 + 時間 + 開頭前10個字

			if (TimerHandle.hasScheduledEvent(name))
			{
				event.reply("Already has " + name + " event!").setEphemeral(true).queue();
				return;
			}

			boolean once = event.getOption("once", Boolean.FALSE, CommonFunctions.getAsBoolean);

			long channelID = guildChannel.getIdLong(); //頻道ID
			Runnable sendMessageToChannel = () ->
			{
				MessageChannel channel = Cartoland.getJDA().getChannelById(MessageChannel.class, channelID);
				if (channel != null)
					channel.sendMessage(content).queue();
			};
			TimerHandle.registerScheduledEvent(name, new TimerHandle.TimerEvent(time, once ? () ->
			{
				sendMessageToChannel.run();
				TimerHandle.unregisterScheduledEvent(name);
			} : sendMessageToChannel));

			event.reply("The bot will send \"" + first20Characters + (contentLength > 20 ? "…" : "") + "\" to " + guildChannel.getAsMention() + " at " + time + (once ? " once." : " everyday.")).queue();
		}
	}
}