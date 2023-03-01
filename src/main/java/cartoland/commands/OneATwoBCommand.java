package cartoland.commands;

import cartoland.events.CommandUsage;
import cartoland.mini_games.IMiniGame;
import cartoland.mini_games.OneATwoBGame;
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
		long userID = commandCore.getUserID();
		IMiniGame playing = commandCore.getGames().get(userID);

		if (argument == null) //不帶參數
		{
			if (playing == null) //沒有在玩遊戲 開始1A2B
			{
				event.reply("Started a game of 1A2B! Type `/one_a_two_b <answer>` to make a guess.").queue();
				commandCore.getGames().put(userID, new OneATwoBGame());
			}
			else //已經有在玩遊戲
				event.reply("You are already in a " + playing.gameName() + " game.").setEphemeral(true).queue();
			return;
		}

		//帶參數
		if (playing == null) //沒有在玩遊戲 但指令還是帶了引數
		{
			event.reply("Please run `/one_a_two_b` without any arguments to start a new game.").setEphemeral(true).queue();
			return;
		}

		//已經有在玩遊戲
		if (!(playing instanceof OneATwoBGame oneATwoB)) //不是在玩1A2B
		{
			event.reply("You are already playing a " + playing.gameName() + " game.").setEphemeral(true).queue();
			return;
		}

		int ab = oneATwoB.calculateAAndB(argument);
		String shouldReply = switch (ab)
		{
			case OneATwoBGame.ErrorCode.INVALID -> "Invalid guess: please enter a " + OneATwoBGame.ANSWER_LENGTH + "-digit integer.";
			case OneATwoBGame.ErrorCode.NOT_UNIQUE -> "Invalid guess: please enter " + OneATwoBGame.ANSWER_LENGTH + " unique digits.";
			default -> argument + " = " + ab / 10 + " A " + ab % 10 + " B";
		};

		if (ab / 10 != OneATwoBGame.ANSWER_LENGTH)//沒有猜出ANSWER_LENGTH個A 遊戲繼續
		{
			event.reply(shouldReply).setEphemeral(true).queue();
			return;
		}

		//猜出ANSWER_LENGTH個A 遊戲結束
		long second = oneATwoB.getTimePassed();
		event.reply(shouldReply + "\nGame over! The answer is **" + argument + "**.\n" +
							"Time elapsed: " + second / 60 + " minutes, " + second % 60 + " seconds\n" +
							"Guesses: " + oneATwoB.getGuesses() + " times").queue();
		commandCore.getGames().remove(userID);
	}
}