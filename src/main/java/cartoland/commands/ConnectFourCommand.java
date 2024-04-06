package cartoland.commands;

import cartoland.mini_games.ConnectFourGame;
import cartoland.mini_games.MiniGame;
import cartoland.utilities.CommonFunctions;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * {@code ConnectFourCommand} is an execution when user uses /connect_four command. This class implements
 * {@link ICommand} interface, which is for the commands HashMap in {@link cartoland.events.CommandUsage}. This can be seen as a frontend
 * of the Connect-Four game.
 *
 * @author Alex Cai
 * @since 2.1
 */
public class ConnectFourCommand extends HasSubcommands
{
	public static final String START = "start";
	public static final String PLAY = "play";
	public static final String BOARD = "board";
	public static final String GIVE_UP = "give_up";

	public ConnectFourCommand(MiniGame.MiniGameMap games)
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

			ConnectFourGame newGame = new ConnectFourGame();
			event.reply(JsonHandle.getString(userID, "connect_four.start") + newGame.getBoard()).queue();
			games.put(userID, newGame);
		});

		//不要再想著用按鈕了 不能超過5個按鈕
		subcommands.put(PLAY, new PlaySubcommand(games));

		subcommands.put(BOARD, event ->
		{
			long userID = event.getUser().getIdLong();
			MiniGame playing = games.get(userID);

			if (playing == null) //沒有在玩遊戲 但還是使用了/connect_four board
			{
				event.reply(JsonHandle.getString(userID, "mini_game.not_playing", "</connect_four start:1123462079546937485>"))
						.setEphemeral(true)
						.queue();
				return;
			}

			//已經有在玩遊戲
			event.reply(playing instanceof ConnectFourGame connectFour ? //是在玩四子棋
							connectFour.getBoard() :
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
			if (!(playing instanceof ConnectFourGame connectFour))
			{
				event.reply(JsonHandle.getString(userID, "mini_game.playing_another_game", JsonHandle.getString(userID, playing.gameName() + ".name")))
						.setEphemeral(true)
						.queue();
				return;
			}
			games.remove(userID);
			event.reply(JsonHandle.getString(userID, "light_out.gave_up") + connectFour.getBoard()).queue();
		});
	}

	/**
	 * {@code PlaySubCommand} is a class that handles one of the subcommands of {@code /connect_four} command,
	 * which is {@code /connect_four play}.
	 *
	 * @since 2.1
	 * @author Alex Cai
	 */
	private static class PlaySubcommand extends GameSubcommand
	{
		private PlaySubcommand(MiniGame.MiniGameMap games)
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
				event.reply(JsonHandle.getString(userID, "mini_game.not_playing", "</connect_four start:1123462079546937485>"))
						.setEphemeral(true)
						.queue();
				return;
			}

			if (!(playing instanceof ConnectFourGame connectFour)) //如果不是在玩四子棋卻還用了指令
			{
				event.reply(JsonHandle.getString(userID, "mini_game.playing_another_game", JsonHandle.getString(userID, playing.gameName() + ".name"))).setEphemeral(true).queue();
				return;
			}

			int column = event.getOption("column", 1, CommonFunctions.getAsInt) - 1; //因為輸入的行數是以1為開始 所以要 - 1

			if (!connectFour.isInBounds(column))
			{
				event.reply(JsonHandle.getString(userID, "connect_four.must_be_in_range")).setEphemeral(true).queue();
				return;
			}

			if (connectFour.isFull(column)) //直行已經滿了卻還是放棋子
			{
				event.reply(JsonHandle.getString(userID, "connect_four.can_t_put")).setEphemeral(true).queue();
				return;
			}

			if (connectFour.humanPlace(column)) //如果玩家贏了
			{
				gameOver(event, JsonHandle.getString(userID, "connect_four.win"));
				return;
			}

			String playerMove = connectFour.getBoard(); //記錄下目前玩家動過後的棋盤

			if (connectFour.aiPlace()) //如果機器人贏了
			{
				gameOver(event, JsonHandle.getString(userID, "connect_four.lose"));
				return;
			}

			if (connectFour.isTie()) //如果平手
			{
				//之所以不像井字遊戲那樣在機器人動之前執行 是因為這個棋盤有偶數個格子 因此最後一步一定是機器人來下
				gameOver(event, JsonHandle.getString(userID, "connect_four.tie"));
				return;
			}

			event.reply(JsonHandle.getString(userID, "connect_four.your_move") + playerMove +
							JsonHandle.getString(userID, "connect_four.bot_s_move") + connectFour.getBoard() + "\n</connect_four play:1142380307509690458>")
					.setEphemeral(true)
					.queue();
		}
	}
}