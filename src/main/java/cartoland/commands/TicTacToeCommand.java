package cartoland.commands;

import cartoland.mini_games.IMiniGame;
import cartoland.mini_games.TicTacToeGame;
import cartoland.utilities.CommonFunctions;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * {@code TicTacToeCommand} is an execution when user uses /tic_tac_toe command. This class implements
 * {@link ICommand} interface, which is for the commands HashMap in {@link cartoland.events.CommandUsage}. This can be seen as a frontend
 * of the Tic-Tac-Toe game.
 *
 * @since 2.0
 * @see TicTacToeGame The backend of the Tic-Tac-Toe game.
 * @author Alex Cai
 */
public class TicTacToeCommand extends HasSubcommands
{
	public static final String START = "start";
	public static final String PLAY = "play";
	public static final String BOARD = "board";

	public TicTacToeCommand(IMiniGame.MiniGameMap games)
	{
		super(3);

		subcommands.put(START, event ->
		{
			long userID = event.getUser().getIdLong();
			IMiniGame playing = games.get(userID);
			if (playing != null) //已經有在玩遊戲
			{
				event.reply(JsonHandle.getString(userID, "mini_game.playing_another_game", playing.gameName()))
						.setEphemeral(true)
						.queue();
				return;
			}

			//沒有在玩遊戲 開始井字遊戲
			int difficulty = event.getOption("difficulty", 0, CommonFunctions.getAsInt);
			TicTacToeGame newGame = new TicTacToeGame(difficulty);
			event.reply(JsonHandle.getString(userID, "tic_tac_toe.start") + newGame.getBoard()).queue();
			games.put(userID, newGame);
		});

		subcommands.put(PLAY, new PlaySubCommand(games));

		subcommands.put(BOARD, event ->
		{
			long userID = event.getUser().getIdLong();
			IMiniGame playing = games.get(userID);

			if (playing == null) //沒有在玩遊戲 但還是使用了/tic_tac_toe board
			{
				event.reply(JsonHandle.getString(userID, "mini_game.not_playing", "</tic_tac_toe start:1123462079546937485>"))
						.setEphemeral(true)
						.queue();
				return;
			}

			//已經有在玩遊戲
			event.reply(playing instanceof TicTacToeGame ticTacToe ? //是在玩井字遊戲
							ticTacToe.getBoard() :
							JsonHandle.getString(userID, "mini_game.playing_another_game", playing.gameName()))
					.setEphemeral(true)
					.queue();
		});
	}

	/**
	 * {@code PlaySubCommand} is a class that handles one of the subcommands of {@code /tic_tac_toe} command, which
	 * is {@code /tic_tac_toe play}.
	 *
	 * @since 2.1
	 * @author Alex Cai
	 */
	public static class PlaySubCommand extends GameSubcommand
	{

		private PlaySubCommand(IMiniGame.MiniGameMap games)
		{
			super(games);
		}

		@Override
		public void commandProcess(SlashCommandInteractionEvent event)
		{
			long userID = event.getUser().getIdLong();
			IMiniGame playing = games.get(userID);

			if (playing == null) //沒有在玩遊戲 但還是使用了/tic_tac_toe play
			{
				event.reply(JsonHandle.getString(userID, "mini_game.not_playing", "</tic_tac_toe start:1123462079546937485>"))
						.setEphemeral(true)
						.queue();
				return;
			}

			//已經有在玩遊戲
			if (!(playing instanceof TicTacToeGame ticTacToe)) //不是在玩井字遊戲
			{
				event.reply(JsonHandle.getString(userID, "mini_game.playing_another_game", playing.gameName()))
						.setEphemeral(true)
						.queue();
				return;
			}

			int row = event.getOption("row", 0, CommonFunctions.getAsInt);
			int column = event.getOption("column", 0, CommonFunctions.getAsInt);

			if (!TicTacToeGame.isInBounds(row, column)) //不在範圍內
			{
				event.reply(JsonHandle.getString(userID, "tic_tac_toe.out_of_bounds")).setEphemeral(true).queue();
				return;
			}

			if (ticTacToe.isPlaced(row, column)) //已經有放子了
			{
				event.reply(JsonHandle.getString(userID, "tic_tac_toe.already_taken")).setEphemeral(true).queue();
				return;
			}

			//玩家下
			if (ticTacToe.humanPlace(row, column)) //玩家贏
			{
				event.reply(JsonHandle.getString(userID, "tic_tac_toe.win") + ticTacToe.getBoard()).queue();
				games.remove(userID);
				return;
			}

			if (ticTacToe.isTie()) //平手
			{
				//之所以不像四子棋那樣在機器人動之後執行 是因為這個棋盤有奇數個格子 因此最後一步一定是玩家來下
				event.reply(JsonHandle.getString(userID, "tic_tac_toe.tie") + ticTacToe.getBoard()).queue();
				games.remove(userID);
				return;
			}

			String playerMove = ticTacToe.getBoard(); //先獲得棋盤

			//機器人下
			if (ticTacToe.aiPlace()) //機器人贏
			{
				event.reply(JsonHandle.getString(userID, "tic_tac_toe.lose") + ticTacToe.getBoard()).queue();
				games.remove(userID);
				return;
			}

			event.reply(JsonHandle.getString(userID, "tic_tac_toe.your_move") + playerMove +
								JsonHandle.getString(userID, "tic_tac_toe.bot_s_move") + ticTacToe.getBoard() +
								"\n</tic_tac_toe play:1123462079546937485>").setEphemeral(true).queue();
		}
	}
}