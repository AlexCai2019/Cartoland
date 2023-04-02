package cartoland.commands;

import cartoland.events.CommandUsage;
import cartoland.mini_games.IMiniGame;
import cartoland.mini_games.MinesweeperGame;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

/**
 * {@code TransferCommand} is an execution when a user uses /minesweeper command. This class implements
 * {@link ICommand} interface, which is for the commands HashMap in {@link CommandUsage}. This can be seen as a frontend
 * of the Minesweeper game.
 *
 * @since 1.5
 * @see MinesweeperGame The backend of the Minesweeper game.
 * @author Alex Cai
 */
public class MinesweeperCommand implements ICommand
{
	private final CommandUsage commandCore;

	public MinesweeperCommand(CommandUsage commandUsage)
	{
		commandCore = commandUsage;
	}

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		IMiniGame playing = commandCore.getGames().get(event.getUser().getIdLong());
		if (playing != null)
		{
			event.reply("You are already in " + playing.gameName() + " game.").setEphemeral(true).queue();
			return;
		}

		String difficulty = event.getOption("difficulty", OptionMapping::getAsString);
		if (difficulty == null)
			return;
		MinesweeperGame minesweeperGame = new MinesweeperGame(difficulty);
		byte[][] map = minesweeperGame.getMap();
		commandCore.getGames().put(event.getUser().getIdLong(), minesweeperGame);

		ReplyCallbackAction reply = event.reply("Start Minesweeper game!");
		for (int rowIndex = 0; rowIndex < map.length; rowIndex++)
			reply = reply.addActionRow(buttonRow(map[rowIndex], rowIndex));

		reply.queue();
	}

	public void pressed(User user, int x, int y)
	{
		;
	}

	private Button[] buttonRow(byte[] mapRow, int rowIndex)
	{
		int rowLength = mapRow.length;
		Button[] rowOfButtons = new Button[rowLength];
		String label;
		for (int columnIndex = 0; columnIndex < rowLength; columnIndex++)
		{
			if (mapRow[columnIndex] <= MinesweeperGame.EIGHT_UNDERCOVER)
				label = " ";
			else if (MinesweeperGame.ZERO_REVEAL <= mapRow[columnIndex] && mapRow[columnIndex] <= MinesweeperGame.EIGHT_REVEAL)
				label = Integer.toString(mapRow[columnIndex] % 10);
			else
				label = "💣";

			rowOfButtons[columnIndex] = Button.primary(rowIndex + " " + columnIndex, label);
		}
		return rowOfButtons;
	}
}