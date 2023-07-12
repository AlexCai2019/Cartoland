package cartoland.commands;

import cartoland.events.CommandUsage;
import cartoland.mini_games.IMiniGame;
import cartoland.mini_games.TicTacToeGame;
import cartoland.utilities.CommandBlocksHandle;
import cartoland.utilities.CommonFunctions;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Map;

/**
 * {@code TicTacToeCommand} is an execution when user uses /tic_tac_toe command. This class implements
 * {@link ICommand} interface, which is for the commands HashMap in {@link CommandUsage}. This can be seen as a frontend
 * of the Tic-Tac-Toe game.
 *
 * @since 2.0
 * @see TicTacToeGame The backend of the Tic-Tac-Toe game.
 * @author Alex Cai
 */
public class TicTacToeCommand implements ICommand
{
	private final ICommand startSubCommand;
	private final ICommand playSubCommand;

	public TicTacToeCommand(CommandUsage commandUsage)
	{
		startSubCommand = new StartSubcommand(commandUsage);
		playSubCommand = new PlaySubCommand(commandUsage);
	}

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		("start".equals(event.getSubcommandName()) ? startSubCommand : playSubCommand).commandProcess(event);
	}

	private static class StartSubcommand implements ICommand
	{
		private final CommandUsage commandCore;

		public StartSubcommand(CommandUsage commandUsage)
		{
			commandCore = commandUsage;
		}

		@Override
		public void commandProcess(SlashCommandInteractionEvent event)
		{
			long userID = event.getUser().getIdLong();
			Map<Long, IMiniGame> games = commandCore.getGames();
			IMiniGame playing = games.get(userID);
			if (playing != null)
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "tic_tac_toe.playing_another_game").formatted(playing.gameName()))
						.setEphemeral(true)
						.queue();
				return;
			}

			//沒有在玩遊戲 開始井字遊戲
			Integer difficulty = event.getOption("difficulty", CommonFunctions.getAsInt);
			if (difficulty == null)
			{
				event.reply("Impossible, this is required!").queue();
				return;
			}
			TicTacToeGame newGame = new TicTacToeGame(difficulty);
			event.reply(JsonHandle.getStringFromJsonKey(userID, "tic_tac_toe.start") + newGame.getBoard()).queue();
			games.put(userID, newGame);
		}
	}

	/**
	 * @since 2.1
	 * @author Alex Cai
	 */
	public static class PlaySubCommand implements ICommand
	{
		private static final int[] REWARDS = { 5, 50, 100 };
		private static final byte PUNISH = 100;

		private final CommandUsage commandCore;

		public PlaySubCommand(CommandUsage commandUsage)
		{
			commandCore = commandUsage;
		}

		@Override
		public void commandProcess(SlashCommandInteractionEvent event)
		{
			Integer rowBox = event.getOption("row", CommonFunctions.getAsInt);
			Integer columnBox = event.getOption("column", CommonFunctions.getAsInt);
			long userID = event.getUser().getIdLong();
			Map<Long, IMiniGame> games = commandCore.getGames();
			IMiniGame playing = games.get(userID);

			if (rowBox == null || columnBox == null) //不帶參數
			{
				event.reply("Impossible, this is required!").queue();
				return;
			}

			int row = rowBox, column = columnBox;

			//帶參數
			if (playing == null) //沒有在玩遊戲 但還是使用了/tic_tac_toe play
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "tic_tac_toe.too_much_arguments")).setEphemeral(true).queue();
				return;
			}

			//已經有在玩遊戲
			if (!(playing instanceof TicTacToeGame ticTacToe)) //不是在玩井字遊戲
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "tic_tac_toe.playing_another_game").formatted(playing.gameName())).setEphemeral(true).queue();
				return;
			}

			if (!TicTacToeGame.isInBounds(row, column)) //不在範圍內
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "tic_tac_toe.out_of_bounds")).setEphemeral(true).queue();
				return;
			}

			if (ticTacToe.isPlaced(row, column)) //已經有放子了
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "tic_tac_toe.already_taken")).setEphemeral(true).queue();
				return;
			}

			//玩家下
			if (ticTacToe.humanPlace(row, column)) //玩家贏
			{
				int reward = REWARDS[ticTacToe.getDifficulty() - 1]; //簡單從1開始
				event.reply(JsonHandle.getStringFromJsonKey(userID, "tic_tac_toe.win").formatted(reward) + ticTacToe.getBoard()).queue();
				CommandBlocksHandle.getLotteryData(userID).addBlocks(reward);
				games.remove(userID);
				return;
			}

			if (ticTacToe.isTie()) //平手
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "tic_tac_toe.tie") + ticTacToe.getBoard()).queue();
				games.remove(userID);
				return;
			}

			String playerMove = JsonHandle.getStringFromJsonKey(userID, "tic_tac_toe.your_move") + ticTacToe.getBoard();

			//機器人下
			if (ticTacToe.aiPlaced()) //機器人贏
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "tic_tac_toe.lose").formatted(PUNISH) + ticTacToe.getBoard()).queue();
				CommandBlocksHandle.LotteryData lotteryData = CommandBlocksHandle.getLotteryData(userID);
				lotteryData.subBlocks(PUNISH); //懲罰PUNISH個指令方塊
				games.remove(userID);
				return;
			}

			event.reply(playerMove + JsonHandle.getStringFromJsonKey(userID, "tic_tac_toe.bot_s_move") +
								ticTacToe.getBoard() + "\n</tic_tac_toe play:1123462079546937485>").setEphemeral(true).queue();
		}
	}
}