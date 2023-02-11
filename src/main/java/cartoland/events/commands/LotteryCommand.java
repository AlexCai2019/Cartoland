package cartoland.events.commands;

import cartoland.utilities.FileHandle;
import cartoland.utilities.IDAndEntities;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

/**
 * {@code LotteryCommand} is an execution when a user uses /lottery command. This class doesn't have a backend
 * class since all the works can be done here.
 *
 * @since 1.4
 * @author Alex Cai
 */
public class LotteryCommand implements ICommand
{
	private final CommandUsage commandCore;

	LotteryCommand(CommandUsage commandUsage)
	{
		commandCore = commandUsage;
	}

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		long nowHave = JsonHandle.getCommandBlocks(commandCore.userID);
		String betString = event.getOption("bet", OptionMapping::getAsString);

		if (betString != null)
		{
			long bet;

			if (betString.matches("\\d+"))
				bet = Long.parseLong(betString);
			else if (betString.matches("\\d+%"))
			{
				long percentage = Long.parseLong(betString.substring(0, betString.length() - 1));
				if (percentage <= 100L)
					bet = nowHave * percentage / 100;
				else
				{
					event.reply("You can't bet " + percentage + "% of your command blocks!").queue();
					return;
				}
			}
			else
			{
				event.reply("Usage: `/lottery [<integer>]` or `/lottery [<percentage>]%`, and don't use negative number.").queue();
				return;
			}

			FileHandle.log(commandCore.userName + "(" + commandCore.userID + ") used /lottery " + betString + ".");
			if (nowHave >= bet)
			{
				long afterBet;
				String result;
				if (IDAndEntities.random.nextBoolean())
				{
					afterBet = nowHave + bet;
					if (afterBet < 0)
						afterBet = Long.MAX_VALUE; //避免溢位
					result = "win!";
				}
				else
				{
					afterBet = nowHave - bet;
					result = "lose...";
				}
				JsonHandle.setCommandBlocks(commandCore.userID, afterBet);
				event.reply("You bet " + bet + " command blocks and " + result + "\nYou now have " + afterBet + " command blocks").queue();
			}
			else
				event.reply("You don't have enough command blocks!\nYou bet " + bet + " command blocks, but you only have " + nowHave + " command blocks.").queue();
		}
		else
		{
			FileHandle.log(commandCore.userName + "(" + commandCore.userID + ") used /lottery.");
			event.reply("You have " + nowHave + " command blocks.").queue();
		}
	}
}