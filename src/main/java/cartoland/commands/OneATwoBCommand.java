package cartoland.commands;

import cartoland.events.CommandUsage;
import cartoland.mini_games.IMiniGame;
import cartoland.mini_games.OneATwoBGame;
import cartoland.utilities.CommandBlocksHandle;
import cartoland.utilities.JsonHandle;
import cartoland.utilities.CommonFunctions;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Map;

/**
 * {@code OneATwoBCommand} is an execution when user uses /one_a_two_b command. This class implements
 * {@link ICommand} interface, which is for the commands HashMap in {@link CommandUsage}. This can be seen as a frontend
 * of the 1A2B game. This used to be a lambda in {@code CommandUsage}, until 1.3 became an independent file.
 *
 * @since 1.3
 * @see OneATwoBGame The backend of the 1A2B game.
 * @author Alex Cai
 */
public class OneATwoBCommand implements ICommand
{
	private final ICommand startSubCommand;
	private final ICommand guessSubCommand;
	private static final int MAX_MINUTE = 2;
	private static final int MAX_GUESSES = 7;
	private static final byte REWARD = 100;

	public OneATwoBCommand(CommandUsage commandUsage)
	{
		startSubCommand = new StartSubcommand(commandUsage);
		guessSubCommand = new PlaySubcommand(commandUsage);
	}

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		("start".equals(event.getSubcommandName()) ? startSubCommand : guessSubCommand).commandProcess(event);
	}

	private static class StartSubcommand implements ICommand
	{
		private final CommandUsage commandCore;
		private StartSubcommand(CommandUsage commandUsage)
		{
			commandCore = commandUsage;
		}

		@Override
		public void commandProcess(SlashCommandInteractionEvent event)
		{
			long userID = event.getUser().getIdLong();
			Map<Long, IMiniGame> games = commandCore.getGames();
			IMiniGame playing = games.get(userID);
			if (playing != null) //已經有在玩遊戲
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "mini_game.playing_another_game").formatted(playing.gameName()))
						.setEphemeral(true)
						.queue();
				return;
			}

			//沒有在玩遊戲 開始1A2B
			event.reply(JsonHandle.getStringFromJsonKey(userID, "one_a_two_b.start")).queue();
			games.put(userID, new OneATwoBGame());
		}
	}

	private static class PlaySubcommand implements ICommand
	{
		private final CommandUsage commandCore;
		private PlaySubcommand(CommandUsage commandUsage)
		{
			commandCore = commandUsage;
		}

		@Override
		public void commandProcess(SlashCommandInteractionEvent event)
		{
			long userID = event.getUser().getIdLong();
			IMiniGame playing = commandCore.getGames().get(userID);
			Integer answerBox = event.getOption("answer", CommonFunctions.getAsInt);
			if (answerBox == null)
			{
				event.reply("Impossible, this is required!").queue();
				return;
			}
			int answer = answerBox; //拆箱
			if (answer < 0)
				answer = -answer;

			//已經有在玩遊戲
			if (!(playing instanceof OneATwoBGame oneATwoB)) //不是在玩1A2B
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "one_a_two_b.playing_another_game").formatted(playing.gameName())).setEphemeral(true).queue();
				return;
			}

			int[] ab = oneATwoB.calculateAAndB(answer); //如果是null 代表答案不是獨一無二的數字
			if (ab == null)
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "one_a_two_b.not_unique").formatted(OneATwoBGame.ANSWER_LENGTH)).setEphemeral(true).queue();
				return;
			}

			String shouldReply = String.format("%0" + OneATwoBGame.ANSWER_LENGTH + "d", answer) + " = " + ab[0] + " A " + ab[1] + " B";
			if (ab[0] != OneATwoBGame.ANSWER_LENGTH)//沒有猜出ANSWER_LENGTH個A 遊戲繼續
			{
				event.reply(shouldReply + "\n</one_a_two_b guess:1102681768840138941>").setEphemeral(true).queue();
				return;
			}

			//猜出ANSWER_LENGTH個A 遊戲結束
			long second = oneATwoB.getTimePassed();
			int guesses = oneATwoB.getGuesses();
			String replyString = JsonHandle.getStringFromJsonKey(userID, "one_a_two_b.game_over").formatted(shouldReply, answer, second / 60, second % 60, guesses);

			if (second <= MAX_MINUTE * 60L && guesses <= MAX_GUESSES)
			{
				replyString += JsonHandle.getStringFromJsonKey(userID, "one_a_two_b.reward").formatted(MAX_MINUTE, MAX_GUESSES, REWARD);
				CommandBlocksHandle.getLotteryData(userID).addBlocks(REWARD);
			}

			event.reply(replyString).queue();
			commandCore.getGames().remove(userID);
		}
	}
}