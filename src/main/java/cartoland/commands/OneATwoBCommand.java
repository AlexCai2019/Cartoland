package cartoland.commands;

import cartoland.events.CommandUsage;
import cartoland.mini_games.IMiniGame;
import cartoland.mini_games.OneATwoBGame;
import cartoland.utilities.CommandBlocksHandle;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

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

	public OneATwoBCommand(CommandUsage commandUsage)
	{
		commandCore = commandUsage;
	}

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		String argument = event.getOption("answer", OptionMapping::getAsString);
		long userID = event.getUser().getIdLong();
		IMiniGame playing = commandCore.getGames().get(userID);

		if (argument == null) //不帶參數
		{
			if (playing == null) //沒有在玩遊戲 開始1A2B
			{
				event.reply(JsonHandle.getJsonKey(userID, "one_a_two_b.start")).queue();
				commandCore.getGames().put(userID, new OneATwoBGame());
			}
			else //已經有在玩遊戲
				event.reply(JsonHandle.getJsonKey(userID, "one_a_two_b.playing_another_game").formatted(playing.gameName())).setEphemeral(true).queue();
			return;
		}

		//帶參數
		if (playing == null) //沒有在玩遊戲 但指令還是帶了引數
		{
			event.reply(JsonHandle.getJsonKey(userID, "one_a_two_b.too_much_arguments")).setEphemeral(true).queue();
			return;
		}

		//已經有在玩遊戲
		if (!(playing instanceof OneATwoBGame oneATwoB)) //不是在玩1A2B
		{
			event.reply(JsonHandle.getJsonKey(userID, "one_a_two_b.playing_another_game").formatted(playing.gameName())).setEphemeral(true).queue();
			return;
		}

		int ab = oneATwoB.calculateAAndB(argument);
		String shouldReply = switch (ab)
		{
			case OneATwoBGame.ErrorCode.INVALID -> JsonHandle.getJsonKey(userID, "one_a_two_b.invalid").formatted(OneATwoBGame.ANSWER_LENGTH);
			case OneATwoBGame.ErrorCode.NOT_UNIQUE -> JsonHandle.getJsonKey(userID, "one_a_two_b.not_unique").formatted(OneATwoBGame.ANSWER_LENGTH);
			default -> argument + " = " + ab / 10 + " A " + ab % 10 + " B";
		};

		if (ab / 10 != OneATwoBGame.ANSWER_LENGTH)//沒有猜出ANSWER_LENGTH個A 遊戲繼續
		{
			event.reply(shouldReply).setEphemeral(true).queue();
			return;
		}

		//猜出ANSWER_LENGTH個A 遊戲結束
		long second = oneATwoB.getTimePassed();
		int guesses = oneATwoB.getGuesses();
		String replyString = JsonHandle.getJsonKey(userID, "one_a_two_b.game_over").formatted(shouldReply, argument, second / 60, second % 60, guesses);

		final int maxMinute = 2;
		final int maxGuesses = 7;
		if (second < maxMinute * 60L && guesses < maxGuesses)
		{
			final int reward = 100;
			replyString += JsonHandle.getJsonKey(userID, "one_a_two_b.reward").formatted(maxMinute, maxGuesses, reward);
			CommandBlocksHandle.add(userID, reward);
		}

		event.reply(replyString).queue();
		commandCore.getGames().remove(userID);
	}
}