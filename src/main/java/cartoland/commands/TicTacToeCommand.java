package cartoland.commands;

import cartoland.events.CommandUsage;
import cartoland.mini_games.IMiniGame;
import cartoland.mini_games.TicTacToeGame;
import cartoland.utilities.CommonFunctions;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * @since 2.0
 * @author Alex Cai
 */
public class TicTacToeCommand implements ICommand
{
	private final CommandUsage commandCore;

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
				event.reply("Start Tic-Tac_Toe!").queue();
				commandCore.getGames().put(userID, new TicTacToeGame());
			}
			else
				event.reply("You are already in " + playing.gameName() + " game.").queue();
			return;
		}

		//帶參數
		if (playing == null) //沒有在玩遊戲 但指令還是帶了引數
		{
			event.reply("Please run /tic_tac_toe without arguments.").queue();
			return;
		}

		//已經有在玩遊戲
		if (!(playing instanceof TicTacToeGame ticTacToe)) //不是在玩井字遊戲
		{
			event.reply("You are already in a " + playing.gameName() + " game.").setEphemeral(true).queue();
			return;
		}

		if (ticTacToe.humanPlace(row, column))
		{
			event.reply("You win!").queue();
		}

		//TODO: Finish the rest of the command.
	}
}