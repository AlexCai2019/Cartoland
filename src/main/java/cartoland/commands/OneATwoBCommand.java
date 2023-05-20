package cartoland.commands;

import cartoland.events.CommandUsage;
import cartoland.mini_games.IMiniGame;
import cartoland.mini_games.OneATwoBGame;
import cartoland.utilities.CommandBlocksHandle;
import cartoland.utilities.JsonHandle;
import cartoland.utilities.CommonFunctions;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

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
	private final CommandUsage commandCore;
	private static final int MAX_MINUTE = 2;
	private static final int MAX_GUESSES = 7;
	private static final int REWARD = 100;

	public OneATwoBCommand(CommandUsage commandUsage)
	{
		commandCore = commandUsage;
	}

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		Integer argument = event.getOption("answer", CommonFunctions.getAsInt);
		long userID = event.getUser().getIdLong();
		IMiniGame playing = commandCore.getGames().get(userID);

		if (argument == null) //不帶參數
		{
			if (playing == null) //沒有在玩遊戲 開始1A2B
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "one_a_two_b.start")).queue();
				commandCore.getGames().put(userID, new OneATwoBGame());
			}
			else //已經有在玩遊戲
				event.reply(JsonHandle.getStringFromJsonKey(userID, "one_a_two_b.playing_another_game").formatted(playing.gameName())).setEphemeral(true).queue();
			return;
		}
		int answer = argument; //拆箱

		//帶參數
		if (playing == null) //沒有在玩遊戲 但指令還是帶了引數
		{
			event.reply(JsonHandle.getStringFromJsonKey(userID, "one_a_two_b.too_much_arguments")).setEphemeral(true).queue();
			return;
		}

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
			event.reply(shouldReply + "\n</one_a_two_b:1074401556113403914>").setEphemeral(true).queue();
			return;
		}

		//猜出ANSWER_LENGTH個A 遊戲結束
		long second = oneATwoB.getTimePassed();
		int guesses = oneATwoB.getGuesses();
		String replyString = JsonHandle.getStringFromJsonKey(userID, "one_a_two_b.game_over").formatted(shouldReply, answer, second / 60, second % 60, guesses);

		if (second <= MAX_MINUTE * 60L && guesses <= MAX_GUESSES)
		{
			replyString += JsonHandle.getStringFromJsonKey(userID, "one_a_two_b.reward").formatted(MAX_MINUTE, MAX_GUESSES, REWARD);
			CommandBlocksHandle.add(userID, REWARD);
		}

		event.reply(replyString).queue();
		commandCore.getGames().remove(userID);
	}
}