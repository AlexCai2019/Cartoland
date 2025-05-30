package cartoland.commands;

import cartoland.mini_games.LightOutGame;
import cartoland.mini_games.MiniGame;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class LightOutCommand extends HasSubcommands
{
	public static final String START = "start";
	public static final String FLIP = "flip";
	public static final String BOARD = "board";
	public static final String GIVE_UP = "give_up";

	public LightOutCommand(MiniGame.MiniGameMap games)
	{
		super(4);
		subcommands.put(START, event ->
		{
			long userID = event.getUser().getIdLong();
			MiniGame playing = games.get(userID);
			if (playing != null) //已經有在玩遊戲
			{
				event.reply(JsonHandle.getString(userID, "mini_game.playing_another_game", JsonHandle.getString(userID, playing.gameName() + ".name")))
						.setEphemeral(true)
						.queue();
				return;
			}
			LightOutGame newGame = new LightOutGame();
			event.reply(JsonHandle.getString(userID, "light_out.start") + newGame.getBoard()).queue();
			games.put(userID, newGame);
		});

		subcommands.put(FLIP, new FlipSubcommand(games));

		subcommands.put(BOARD, event ->
		{
			long userID = event.getUser().getIdLong();
			MiniGame playing = games.get(userID);

			if (playing == null) //沒有在玩遊戲 但還是使用了/light_out board
			{
				event.reply(JsonHandle.getString(userID, "mini_game.not_playing", "</light_out start:1211761952276217856>"))
						.setEphemeral(true)
						.queue();
				return;
			}

			//已經有在玩遊戲
			event.reply(playing instanceof LightOutGame lightOut ? //是在玩關燈遊戲
							lightOut.getBoard() :
							JsonHandle.getString(userID, "mini_game.playing_another_game", JsonHandle.getString(userID, playing.gameName() + ".name")))
					.setEphemeral(true)
					.queue();
		});

		subcommands.put(GIVE_UP, event ->
		{
			long userID = event.getUser().getIdLong();
			MiniGame playing = games.get(userID);
			if (playing == null)
			{
				event.reply(JsonHandle.getString(userID, "mini_game.no_game_gave_up")).queue();
				return;
			}
			if (!(playing instanceof LightOutGame lightOut))
			{
				event.reply(JsonHandle.getString(userID, "mini_game.playing_another_game", JsonHandle.getString(userID, playing.gameName() + ".name")))
						.setEphemeral(true)
						.queue();
				return;
			}
			games.remove(event.getUser().getIdLong());
			event.reply(JsonHandle.getString(userID, "light_out.gave_up") + lightOut.getBoard()).queue();
		});
	}

	private static class FlipSubcommand extends GameSubcommand
	{
		private FlipSubcommand(MiniGame.MiniGameMap games)
		{
			super(games);
		}

		@Override
		public void commandProcess(SlashCommandInteractionEvent event)
		{
			long userID = event.getUser().getIdLong();
			MiniGame playing = games.get(userID);

			if (playing == null) //沒有在玩任何遊戲
			{
				event.reply(JsonHandle.getString(userID, "mini_game.not_playing", "</light_out start:1211761952276217856>"))
						.setEphemeral(true)
						.queue();
				return;
			}
			if (!(playing instanceof LightOutGame lightOut))
			{
				event.reply(JsonHandle.getString(userID, "mini_game.playing_another_game", JsonHandle.getString(userID, playing.gameName() + ".name")))
						.setEphemeral(true)
						.queue();
				return;
			}

			int row = event.getOption("row", 1, OptionMapping::getAsInt) - 1;
			int column = event.getOption("column", 1, OptionMapping::getAsInt) - 1;

			if (!LightOutGame.isInBounds(row, column))
			{
				event.reply("The arguments are not in bounds!").setEphemeral(true).queue();
				return;
			}

			if (lightOut.flip(row, column)) //翻面之後贏了
			{
				gameOver(event, "You won!\n" + lightOut.getBoard());
				return;
			}

			event.reply(lightOut.getBoard()).setEphemeral(true).queue();
		}
	}
}