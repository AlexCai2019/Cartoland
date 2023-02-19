package cartoland.events.commands;

import cartoland.events.CommandUsage;
import cartoland.mini_games.IMiniGame;
import cartoland.mini_games.OneATwoBGame;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

/**
 * {@code OneATwoBCommand} is an execution when user uses /one_a_two_b command. This can be seen as a frontend of
 * the 1A2B game. This class implements {@link ICommand} interface, which is for the commands HashMap in
 * {@link CommandUsage}. This used to be a lambda in {@code CommandUsage}, until 1.3 became an independent file.
 *
 * @author Alex Cai
 * @see cartoland.mini_games.OneATwoBGame The backend of the 1A2B game.
 * @since 1.3
 */
public class OneATwoBCommand implements ICommand
{
	private final CommandUsage commandCore;

	public OneATwoBCommand(CommandUsage commandUsage)
	{
		commandCore = commandUsage;
	}

	@Override
	public void commandProcess(@NotNull SlashCommandInteractionEvent event)
	{
		String argument = event.getOption("answer", OptionMapping::getAsString);
		long userID = commandCore.getUserID();
		IMiniGame playing = commandCore.getGames().get(userID);

		if (argument == null) //不帶參數
		{
			if (playing == null) //沒有在玩遊戲 開始1A2B
			{
				event.reply("Start 1A2B game! type `/one_a_two_b <answer>` to make a guess.").queue();
				commandCore.getGames().put(userID, new OneATwoBGame());
			}
			else //已經有在玩遊戲
				event.reply("You are already in " + playing.gameName() + " game.").queue();
			return;
		}

		//帶參數
		if (playing == null) //沒有在玩遊戲 但指令還是帶了引數
		{
			event.reply("Please run `/one_a_two_b` without arguments to start the game.").queue();
			return;
		}

		//已經有在玩遊戲
		if (!(playing instanceof OneATwoBGame oneATwoB)) //不是在玩1A2B
		{
			event.reply("You are already in " + playing.gameName() + " game.").queue();
			return;
		}

		int ab = oneATwoB.calculateAAndB(argument);
		String shouldReply = switch (ab)
		{
			case OneATwoBGame.ErrorCode.INVALID -> "Not a valid answer, please enter " + OneATwoBGame.ANSWER_LENGTH + " integers.";
			case OneATwoBGame.ErrorCode.NOT_UNIQUE -> "Please enter " + OneATwoBGame.ANSWER_LENGTH + " unique integers.";
			default -> argument + " = " + ab / 10 + " A " + ab % 10 + " B";
		};

		if (ab / 10 != OneATwoBGame.ANSWER_LENGTH)//沒有猜出ANSWER_LENGTH個A 遊戲繼續
		{
			event.reply(shouldReply).queue();
			return;
		}

		//猜出ANSWER_LENGTH個A 遊戲結束
		long second = oneATwoB.getTimePassed();
		event.reply(shouldReply + "\nGame Over, the answer is **" + argument + "**.\n" +
							"Used Time: " + second / 60 + " minutes " + second % 60 + " seconds\n" +
							"Guesses: " + oneATwoB.getGuesses() + " times").queue();
		commandCore.getGames().remove(userID);
	}
}