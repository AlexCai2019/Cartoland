package cartoland.events.commands;

import cartoland.mini_games.IMiniGame;
import cartoland.mini_games.OneATwoBGame;
import cartoland.utilities.FileHandle;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

/**
 * {@code OneATwoBCommand} is an execution when user uses /oneatwob command. This can be seen as a frontend of
 * the 1A2B game. This class implements {@link ICommand} interface, which is for the commands HashMap in
 * {@link CommandUsage}. This used to be a lambda in {@code CommandUsage}, until 1.3 became an independent file.
 *
 * @since 1.3
 * @see cartoland.mini_games.OneATwoBGame The backend of the 1A2B game.
 * @author Alex Cai
 */
public class OneATwoBCommand implements ICommand
{
	private final CommandUsage commandCore;

	OneATwoBCommand(CommandUsage commandUsage)
	{
		commandCore = commandUsage;
	}

	@Override
	public void commandProcess(@NotNull SlashCommandInteractionEvent event)
	{
		String argument = event.getOption("answer", OptionMapping::getAsString);
		IMiniGame playing = commandCore.games.get(commandCore.userID);

		if (argument == null) //不帶參數
		{
			if (playing != null /*games.containsKey(userID)*/) //已經有在玩遊戲
				event.reply("You are already in " + playing.gameName() + " game.").queue();
			else
			{
				event.reply("Start 1A2B game! type `/oneatwob <answer>` to make a guess.").queue();
				commandCore.games.put(commandCore.userID, new OneATwoBGame());
			}
			FileHandle.log(commandCore.userName + "(" + commandCore.userID + ") used /oneatwob.");
		}
		else //帶參數
		{
			if (playing != null) //已經有在玩遊戲
			{
				if (playing instanceof OneATwoBGame oneATwoB) //是1A2B沒錯
				{
					int ab = oneATwoB.calculateAAndB(argument);
					String shouldReply = switch (ab)
							{
								case OneATwoBGame.ErrorCode.INVALID ->
										"Not a valid answer, please enter " + OneATwoBGame.ANSWER_LENGTH + " integers.";
								case OneATwoBGame.ErrorCode.NOT_UNIQUE ->
										"Please enter " + OneATwoBGame.ANSWER_LENGTH + " unique integers.";
								default -> argument + " = " + ab / 10 + " A " + ab % 10 + " B";
							};

					//猜出ANSWER_LENGTH個A 遊戲結束
					if (ab / 10 == OneATwoBGame.ANSWER_LENGTH)
					{
						long second = oneATwoB.getTimePassed();
						event.reply(shouldReply + "\nGame Over, the answer is **" + argument + "**.\n" +
											"Used Time: " + second / 60 + " minutes " + second % 60 + " seconds.\n" +
											"Guesses: " + oneATwoB.getGuesses() + " times.").queue();
						commandCore.games.remove(commandCore.userID);
					}
					else //沒有猜出ANSWER_LENGTH個A 遊戲結束
						event.reply(shouldReply).queue();
				}
				else //不是在玩1A2B
					event.reply("You are already in " + playing.gameName() + " game.").queue();
			}
			else //沒有在玩遊戲 但指令還是帶了引數
				event.reply("Please run `/oneatwob` without arguments to start the game.").queue();

			FileHandle.log(commandCore.userName + "(" + commandCore.userID + ") used /oneatwob " + argument + ".");
		}
	}
}