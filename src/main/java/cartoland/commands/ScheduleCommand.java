package cartoland.commands;

import cartoland.Cartoland;
import cartoland.utilities.CommonFunctions;
import cartoland.utilities.TimerHandle;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class ScheduleCommand implements ICommand
{
	private final ICommand createSubCommand = new CreateSubCommand();
	private final ICommand cancelSubCommand = event ->
	{
		int time = event.getOption("time", 0, CommonFunctions.getAsInt); //時間 介於0到23之間
		if (time < 0 || time > 23) //不得超出範圍
		{
			event.reply("Time must between 0 and 23!").setEphemeral(true).queue();
			return;
		}
		TimerHandle.unregisterTimerEvent(time);
	};

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		("create".equals(event.getSubcommandName()) ? createSubCommand : cancelSubCommand).commandProcess(event);
	}

	private static class CreateSubCommand implements ICommand
	{
		@Override
		public void commandProcess(SlashCommandInteractionEvent event)
		{
			Integer timeBox = event.getOption("time", CommonFunctions.getAsInt); //時間 介於0到23之間
			if (timeBox == null)
			{
				event.reply("Impossible, this is required!").queue();
				return;
			}
			int time = timeBox;
			if (time < 0 || time > 23) //不得超出範圍
			{
				event.reply("Time must between 0 and 23!").setEphemeral(true).queue();
				return;
			}

			GuildChannel guildChannel = event.getOption("channel", OptionMapping::getAsChannel); //頻道
			if (guildChannel == null)
			{
				event.reply("Impossible, this is required!").queue();
				return;
			}
			if (!guildChannel.getType().isMessage()) //必須要是訊息頻道
			{
				event.reply("Please input a message channel!").setEphemeral(true).queue();
				return;
			}
			long channelID = guildChannel.getIdLong(); //頻道ID

			String content = event.getOption("content", CommonFunctions.getAsString); //內容
			if (content == null)
			{
				event.reply("Impossible, this is required!").queue();
				return;
			}

			if (Boolean.TRUE.equals(event.getOption("once", CommonFunctions.getAsBoolean))) //代表是一次性的
				TimerHandle.registerTimerEvent(time, new Runnable() //不可使用lambda 因為下面要用到this
				{
					@Override
					public void run()
					{
						MessageChannel channel = Cartoland.getJDA().getChannelById(MessageChannel.class, channelID);
						if (channel != null)
							channel.sendMessage(content).queue();
						TimerHandle.unregisterTimerEvent(time, this);
					}
				});
			else
				TimerHandle.registerTimerEvent(time, () ->
				{
					MessageChannel channel = Cartoland.getJDA().getChannelById(MessageChannel.class, channelID);
					if (channel != null)
						channel.sendMessage(content).queue();
				});
		}
	}
}