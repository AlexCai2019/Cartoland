package cartoland.events.commands;

import cartoland.mini_games.IMiniGame;
import cartoland.mini_games.OneATwoBGame;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import static cartoland.events.commands.CommandUsage.*;

public class OneATwoBCommand implements ICommand
{
    @Override
    public void commandProcess(@NotNull SlashCommandInteractionEvent event)
    {
        String argument = event.getOption("answer", OptionMapping::getAsString);
        if (argument == null) //不帶參數
        {
            if (games.containsKey(userID)) //已經有在玩遊戲
                event.reply("You are already in " + games.get(userID).gameName() + " game.").queue();
            else
            {
                event.reply("Start 1A2B game! type `/oneatwob <answer>` to make a guess.").queue();
                games.put(userID, new OneATwoBGame());
            }
        }
        else //帶參數
        {
            IMiniGame playing = games.get(userID);
            if (playing != null) //已經有在玩遊戲
            {
                if (playing.gameName().equals("1A2B")) //是1A2B沒錯
                {
                    OneATwoBGame oneATwoB = (OneATwoBGame) playing;
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
                        games.remove(userID);
                    }
                    else
                        event.reply(shouldReply).queue();
                }
            }
            else
                event.reply("Please run `/oneatwob` without arguments to start the game.").queue();
        }
    }
}
