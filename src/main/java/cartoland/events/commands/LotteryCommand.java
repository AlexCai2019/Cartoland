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
		Long betLong = event.getOption("bet", OptionMapping::getAsLong);

		if (betLong != null)
		{
			long bet = betLong;
			if (nowHave > bet)
			{
				JsonHandle.subCommandBlocks(commandCore.userID, bet);
				if (IDAndEntities.random.nextBoolean())
				{
					JsonHandle.addCommandBlocks(commandCore.userID, bet);
					event.reply("You win! You now have " + (nowHave + bet) + " command blocks").queue();
				}
				else
					event.reply("You Lose... you now have " + (nowHave - bet) + " command blocks").queue();
			}
			else
				event.reply("You don't have enough command blocks! You now have " + nowHave + " command blocks.").queue();
		}
		else
			event.reply("You now have " + nowHave + " command blocks.").queue();
	}
}