package cartoland.commands;

import cartoland.events.CommandUsage;
import cartoland.mini_games.IMiniGame;
import cartoland.mini_games.TicTacToeGame;
import cartoland.utilities.CommandBlocksHandle;
import cartoland.utilities.CommonFunctions;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

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
	private final CommandUsage commandCore;
	private static final byte REWARD = 5;
	private static final byte PUNISH = 100;

	public TicTacToeCommand(CommandUsage commandUsage)
	{
		commandCore = commandUsage;
	}

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		Integer row = event.getOption("row", CommonFunctions.getAsInt);
		Integer column = event.getOption("column", CommonFunctions.getAsInt);
		long userID = event.getUser().getIdLong();
		IMiniGame playing = commandCore.getGames().get(userID);

		if (row == null || column == null) //不帶參數
		{
			if (playing == null) //沒有在玩遊戲 開始井字遊戲
			{
				TicTacToeGame newGame = new TicTacToeGame();
				event.reply(JsonHandle.getStringFromJsonKey(userID, "tic_tac_toe.start") + newGame.getBoard()).queue();
				commandCore.getGames().put(userID, newGame);
			}
			else
				event.reply(JsonHandle.getStringFromJsonKey(userID, "tic_tac_toe.playing_another_game").formatted(playing.gameName())).setEphemeral(true).queue();
			return;
		}

		//帶參數
		if (playing == null) //沒有在玩遊戲 但指令還是帶了引數
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

		if (ticTacToe.humanPlace(row, column))
		{
			event.reply(JsonHandle.getStringFromJsonKey(userID, "tic_tac_toe.win").formatted(REWARD) + ticTacToe.getBoard()).queue();
			CommandBlocksHandle.getLotteryData(userID).addBlocks(REWARD);
			commandCore.getGames().remove(userID);
			return;
		}

		if (ticTacToe.isTie())
		{
			event.reply(JsonHandle.getStringFromJsonKey(userID, "tic_tac_toe.tie") + ticTacToe.getBoard()).queue();
			commandCore.getGames().remove(userID);
			return;
		}

		String playerMove = JsonHandle.getStringFromJsonKey(userID, "tic_tac_toe.your_move") + ticTacToe.getBoard();

		if (ticTacToe.aiPlaced())
		{
			event.reply(JsonHandle.getStringFromJsonKey(userID, "tic_tac_toe.lose").formatted(PUNISH) + ticTacToe.getBoard()).queue();
			CommandBlocksHandle.LotteryData lotteryData = CommandBlocksHandle.getLotteryData(userID);
			long newValue = lotteryData.getBlocks() - PUNISH; //懲罰PUNISH個指令方塊
			if (newValue < 0) //資產變負了
				newValue = 0L; //0就好
			lotteryData.setBlocks(newValue);
			commandCore.getGames().remove(userID);
			return;
		}

		event.reply(playerMove + JsonHandle.getStringFromJsonKey(userID, "tic_tac_toe.bot_s_move") +
							ticTacToe.getBoard() + "\n</tic_tac_toe:1123462079546937485>").setEphemeral(true).queue();
	}
}