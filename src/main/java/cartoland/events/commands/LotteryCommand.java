package cartoland.events.commands;

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
				bet = Long.getLong(betString);
			else if (betString.matches("\\d+%"))
			{
				long percentage = Long.getLong(betString.substring(0, betString.length() - 1));
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

			if (nowHave >= bet)
			{
				if (IDAndEntities.random.nextBoolean())
				{
					JsonHandle.addCommandBlocks(commandCore.userID, bet);
					event.reply("You win! You now have " + (nowHave + bet) + " command blocks").queue();
				}
				else
				{
					JsonHandle.subCommandBlocks(commandCore.userID, bet);
					event.reply("You lose... You now have " + (nowHave - bet) + " command blocks").queue();
				}
			}
			else
				event.reply("You don't have enough command blocks! You now have " + nowHave + " command blocks.").queue();
		}
		else
			event.reply("You now have " + nowHave + " command blocks.").queue();
	}
}