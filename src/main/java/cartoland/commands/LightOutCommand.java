package cartoland.commands;

import cartoland.mini_games.IMiniGame;
import cartoland.mini_games.LightOutGame;
import cartoland.utilities.CommonFunctions;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class LightOutCommand extends HasSubcommands
{
	public static final String START = "start";
	public static final String FLIP = "flip";
	public static final String BOARD = "board";

	public LightOutCommand(IMiniGame.MiniGameMap games)
	{
		super(3);
		subcommands.put(START, event ->
		{
			long userID = event.getUser().getIdLong();
			IMiniGame playing = games.get(userID);
			if (playing != null) //已經有在玩遊戲
			{
				event.reply("..." + playing.gameName())
						.setEphemeral(true)
						.queue();
				return;
			}
			LightOutGame newGame = new LightOutGame();
			event.reply("..." + newGame.getBoard()).queue();
			games.put(userID, newGame);
		});

		subcommands.put(FLIP, new FlipSubcommand(games));

		subcommands.put(BOARD, event ->
		{
			long userID = event.getUser().getIdLong();
			IMiniGame playing = games.get(userID);

			if (playing == null) //沒有在玩遊戲 但還是使用了/light_out board
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "mini_game.not_playing").formatted("</light_out start:...>"))
						.setEphemeral(true)
						.queue();
				return;
			}

			//已經有在玩遊戲
			event.reply(playing instanceof LightOutGame lightOut ? //是在玩井字遊戲
							lightOut.getBoard() :
							"...")
					.setEphemeral(true)
					.queue();
		});
	}

	private static class FlipSubcommand extends GameSubcommand
	{
		private FlipSubcommand(IMiniGame.MiniGameMap games)
		{
			super(games);
		}

		@Override
		public void commandProcess(SlashCommandInteractionEvent event)
		{
			long userID = event.getUser().getIdLong();
			IMiniGame playing = games.get(userID);

			if (playing == null) //沒有在玩任何遊戲
			{
				event.reply("...").setEphemeral(true).queue();
				return;
			}
			if (!(playing instanceof LightOutGame lightOut))
			{
				event.reply("...").setEphemeral(true).queue();
				return;
			}

			int row = event.getOption("row", 0, CommonFunctions.getAsInt);
			int column = event.getOption("column", 0, CommonFunctions.getAsInt);

			if (!LightOutGame.isInBounds(row, column))
			{
				event.reply("...").setEphemeral(true).queue();
				return;
			}

			if (lightOut.flip(row, column)) //翻面之後贏了
			{
				event.reply("You won!\n" + lightOut.getBoard()).queue();
				games.remove(userID);
				return;
			}

			event.reply(lightOut.getBoard()).setEphemeral(true).queue();
		}
	}
}