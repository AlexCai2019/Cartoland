package cartoland.commands;

import cartoland.mini_games.MiniGame;
import cartoland.mini_games.OneATwoBGame;
import cartoland.utilities.CommandBlocksHandle;
import cartoland.utilities.CommonFunctions;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * {@code OneATwoBCommand} is an execution when user uses /one_a_two_b command. This class implements
 * {@link ICommand} interface, which is for the commands HashMap in {@link cartoland.events.CommandUsage}. This can be seen as a frontend
 * of the 1A2B game. This used to be a lambda in {@link cartoland.events.CommandUsage}, until 1.3 became an independent file.
 *
 * @since 1.3
 * @see OneATwoBGame The backend of the 1A2B game.
 * @author Alex Cai
 */
public class OneATwoBCommand extends HasSubcommands
{
	private static final int MAX_MINUTE = 2;
	private static final int MAX_GUESSES = 7;
	private static final byte REWARD = 100;

	public static final String START = "start";
	public static final String PLAY = "play";
	public static final String GIVE_UP = "give_up";

	public OneATwoBCommand(MiniGame.MiniGameMap games)
	{
		super(3);
		subcommands.put(START, event ->
		{
			long userID = event.getUser().getIdLong();
			MiniGame playing = games.get(userID);
			if (playing != null) //已經有在玩遊戲 還用start
			{
				event.reply(JsonHandle.getString(userID, "mini_game.playing_another_game", JsonHandle.getString(userID, playing.gameName() + ".name")))
						.setEphemeral(true)
						.queue();
				return;
			}

			//沒有在玩遊戲 開始1A2B
			event.reply(JsonHandle.getString(userID, "one_a_two_b.start")).queue();
			games.put(userID, new OneATwoBGame());
		});

		subcommands.put(PLAY, new PlaySubcommand(games));

		subcommands.put(GIVE_UP, event ->
		{
			long userID = event.getUser().getIdLong();
			MiniGame playing = games.get(userID);
			if (playing == null)
			{
				event.reply(JsonHandle.getString(userID, "mini_game.no_game_gave_up")).setEphemeral(true).queue();
				return;
			}
			if (!(playing instanceof OneATwoBGame oneATwoB))
			{
				event.reply(JsonHandle.getString(userID, "mini_game.playing_another_game", JsonHandle.getString(userID, playing.gameName() + ".name")))
						.setEphemeral(true)
						.queue();
				return;
			}
			games.remove(userID);
			event.reply(JsonHandle.getString(userID, "one_a_two_b.gave_up") + oneATwoB.getAnswerString()).queue();
		});
	}

	/**
	 * {@code PlaySubCommand} is a class that handles one of the subcommands of {@code /one_a_two_b} command, which
	 * is {@code /one_a_two_b play}.
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
			if (playing == null) //沒有在玩遊戲 但還是用了/one_a_two_b play
			{
				event.reply(JsonHandle.getString(userID, "mini_game.not_playing", "</tic_tac_toe start:1123462079546937485>"))
						.setEphemeral(true)
						.queue();
				return;
			}

			//已經有在玩遊戲
			if (!(playing instanceof OneATwoBGame oneATwoB)) //不是在玩1A2B
			{
				event.reply(JsonHandle.getString(userID, "mini_game.playing_another_game", JsonHandle.getString(userID, playing.gameName() + ".name")))
						.setEphemeral(true)
						.queue();
				return;
			}

			int answer = Math.abs(event.getOption("answer", 0, CommonFunctions.getAsInt)); //避免負數
			if (answer == Integer.MIN_VALUE) //Math.abs無法處理-2147483648
			{
				event.reply("Please input a positive integer!").setEphemeral(true).queue();
				return;
			}

			int[] ab = oneATwoB.calculateAAndB(answer); //如果是null 代表答案不是獨一無二的數字
			if (ab == null)
			{
				event.reply(JsonHandle.getString(userID, "one_a_two_b.not_unique", OneATwoBGame.ANSWER_LENGTH))
						.setEphemeral(true)
						.queue();
				return;
			}

			String shouldReply = String.format("%0" + OneATwoBGame.ANSWER_LENGTH + "d", answer) + " = " + ab[0] + " A " + ab[1] + " B";
			if (ab[0] != OneATwoBGame.ANSWER_LENGTH)//沒有猜出ANSWER_LENGTH個A 遊戲繼續
			{
				event.reply(shouldReply + "\n</one_a_two_b play:1102681768840138941>").setEphemeral(true).queue();
				return;
			}

			//猜出ANSWER_LENGTH個A 遊戲結束
			int guesses = oneATwoB.getGuesses();
			long second = oneATwoB.getTimePassed();
			String replyString = JsonHandle.getString(userID, "one_a_two_b.game_over", shouldReply, answer, guesses) + JsonHandle.getString(userID, "mini_game.used_time", second / 60, second % 60);

			if (second <= MAX_MINUTE * 60L && guesses <= MAX_GUESSES) //如果在2分鐘內猜出來 且不大於7次
			{
				CommandBlocksHandle.getLotteryData(userID).addBlocks(REWARD); //獎勵REWARD顆指令方塊
				gameOver(event, replyString + JsonHandle.getString(userID, "one_a_two_b.reward", MAX_MINUTE, MAX_GUESSES, REWARD));
			}
			else
				gameOver(event, replyString);
		}
	}
}