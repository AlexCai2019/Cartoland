package cartoland.commands;

import cartoland.events.CommandUsage;
import cartoland.mini_games.ConnectFourGame;
import cartoland.mini_games.IMiniGame;
import cartoland.utilities.CommonFunctions;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Map;

/**
 * @since 2.1
 * @author Alex Cai
 */
public class ConnectFourCommand implements ICommand
{
	private final ICommand startSubCommand;
	private final ICommand playSubCommand;

	public ConnectFourCommand(CommandUsage commandUsage)
	{
		startSubCommand = new StartSubCommand(commandUsage);
		playSubCommand = new PlaySubCommand(commandUsage);
	}

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		("start".equals(event.getSubcommandName()) ? startSubCommand : playSubCommand).commandProcess(event);
	}

	private static class StartSubCommand implements ICommand
	{
		private final CommandUsage commandCore;

		private StartSubCommand(CommandUsage commandUsage)
		{
			commandCore = commandUsage;
		}

		@Override
		public void commandProcess(SlashCommandInteractionEvent event)
		{
			long userID = event.getUser().getIdLong();
			Map<Long, IMiniGame> games = commandCore.getGames(); //目前所有人正在玩的遊戲們
			IMiniGame playing = games.get(userID);
			if (playing != null) //已經有在玩遊戲
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "mini_game.playing_another_game").formatted(playing.gameName()))
						.setEphemeral(true)
						.queue();
				return;
			}

			event.reply("Start a game of connect four!").queue();
			games.put(userID, new ConnectFourGame());
		}
	}

	private static class PlaySubCommand implements ICommand
	{
		private final CommandUsage commandCore;

		private PlaySubCommand(CommandUsage commandUsage)
		{
			commandCore = commandUsage;
		}

		@Override
		public void commandProcess(SlashCommandInteractionEvent event)
		{
			Integer columnBox = event.getOption("column", CommonFunctions.getAsInt);
			long userID = event.getUser().getIdLong();
			Map<Long, IMiniGame> games = commandCore.getGames();
			IMiniGame playing = games.get(userID);

			if (columnBox == null) //column為必填
			{
				event.reply("Impossible, this is required!").queue();
				return;
			}

			if (playing == null) //沒有在玩任何遊戲
			{
				event.reply("You are not playing any game!").setEphemeral(true).queue();
				return;
			}

			if (!(playing instanceof ConnectFourGame connectFour)) //如果不是在玩四子棋卻還用了指令
			{
				event.reply("You are not playing connect four!").setEphemeral(true).queue();
				return;
			}

			int column = columnBox - 1; //拆箱 因為columnBox是以1為開始 所以要 - 1
			if (!connectFour.isInBounds(column))
			{
				event.reply("Column must be in range!").setEphemeral(true).queue();
				return;
			}

			if (connectFour.isFull(column))
			{
				event.reply("You can't put piece here!").setEphemeral(true).queue();
				return;
			}

			if (connectFour.humanPlace(column))
			{
				event.reply("You won!").queue();
				return;
			}
		}
	}
}