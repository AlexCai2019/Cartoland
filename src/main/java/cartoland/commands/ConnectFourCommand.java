package cartoland.commands;

import cartoland.events.CommandUsage;
import cartoland.mini_games.ConnectFourGame;
import cartoland.mini_games.IMiniGame;
import cartoland.utilities.CommonFunctions;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Map;

/**
 * {@code ConnectFourCommand} is an execution when user uses /connect_four command. This class implements
 * {@link ICommand} interface, which is for the commands HashMap in {@link CommandUsage}. This can be seen as a frontend
 * of the Connect-Four game.
 *
 * @author Alex Cai
 * @since 2.1
 */
public class ConnectFourCommand extends HasSubcommands
{
	public ConnectFourCommand(CommandUsage commandUsage)
	{
		super(3);

		subcommands.put("start", event ->
		{
			long userID = event.getUser().getIdLong();
			Map<Long, IMiniGame> games = commandUsage.getGames(); //目前所有人正在玩的遊戲們
			IMiniGame playing = games.get(userID);
			if (playing != null) //已經有在玩遊戲
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "mini_game.playing_another_game").formatted(playing.gameName()))
						.setEphemeral(true)
						.queue();
				return;
			}

			ConnectFourGame newGame = new ConnectFourGame();
			event.reply("connect_four.start" + newGame.getBoard()).queue();
			games.put(userID, newGame);
		});

		subcommands.put("play", new PlaySubCommand(commandUsage));

		subcommands.put("board", event ->
		{
			long userID = event.getUser().getIdLong();
			IMiniGame playing = commandUsage.getGames().get(userID);

			if (playing == null) //沒有在玩遊戲 但還是使用了/connect_four board
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "mini_game.not_playing").formatted("</connect_four start:1123462079546937485>"))
						.setEphemeral(true)
						.queue();
				return;
			}

			//已經有在玩遊戲
			event.reply(playing instanceof ConnectFourGame connectFour ? //是在玩四子棋
							connectFour.getBoard() :
							JsonHandle.getStringFromJsonKey(userID, "mini_game.playing_another_game").formatted(playing.gameName()))
					.setEphemeral(true)
					.queue();
		});
	}

	/**
	 * {@code PlaySubCommand} is a class that handles one of the subcommands of {@code /connect_four} command,
	 * which is {@code /connect_four play}.
	 *
	 * @since 2.1
	 * @author Alex Cai
	 */
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
			long userID = event.getUser().getIdLong();
			Map<Long, IMiniGame> games = commandCore.getGames();
			IMiniGame playing = games.get(userID);

			if (playing == null) //沒有在玩任何遊戲
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "mini_game.not_playing").formatted("</connect_four start:1123462079546937485>"))
						.setEphemeral(true)
						.queue();
				return;
			}

			if (!(playing instanceof ConnectFourGame connectFour)) //如果不是在玩四子棋卻還用了指令
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "mini_game.playing_another_game").formatted(playing.gameName())).setEphemeral(true).queue();
				return;
			}

			int column = event.getOption("column", CommonFunctions.intDefault, CommonFunctions.getAsInt) - 1; //因為輸入的行數是以1為開始 所以要 - 1
			if (!connectFour.isInBounds(column))
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "connect_four.must_be_in_range")).setEphemeral(true).queue();
				return;
			}

			if (connectFour.isFull(column)) //直行已經滿了卻還是放棋子
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "connect_four.can_t_put")).setEphemeral(true).queue();
				return;
			}

			if (connectFour.humanPlace(column)) //如果玩家贏了
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "connect_four.win") + connectFour.getBoard()).queue();
				games.remove(userID);
				return;
			}

			String playerMove = connectFour.getBoard(); //記錄下目前玩家動過後的棋盤

			if (connectFour.aiPlace()) //如果機器人贏了
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "connect_four.lose") + connectFour.getBoard()).queue();
				games.remove(userID);
				return;
			}

			if (connectFour.isTie()) //如果平手
			{
				//之所以不像井字遊戲那樣在機器人動之前執行 是因為這個棋盤有偶數個格子 因此最後一步一定是機器人來下
				event.reply(JsonHandle.getStringFromJsonKey(userID, "connect_four.tie") + connectFour.getBoard()).queue();
				games.remove(userID);
				return;
			}

			event.reply(JsonHandle.getStringFromJsonKey(userID, "connect_four.your_move") + playerMove +
								JsonHandle.getStringFromJsonKey(userID, "connect_four.bot_s_move") + connectFour.getBoard() +
								"\n</connect_four play:1142380307509690458>").setEphemeral(true).queue();
		}
	}
}