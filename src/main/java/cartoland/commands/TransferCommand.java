package cartoland.commands;

import cartoland.events.CommandUsage;
import cartoland.utilities.Algorithm;
import cartoland.utilities.CommandBlocksHandle;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

/**
 * {@code TransferCommand} is an execution when a user uses /transfer command. This class implements {@link ICommand}
 * interface, which is for the commands HashMap in {@link CommandUsage}.
 *
 * @since 1.5
 * @author Alex Cai
 */
public class TransferCommand implements ICommand
{
	private final CommandUsage commandCore;

	public TransferCommand(CommandUsage commandUsage)
	{
		commandCore = commandUsage;
	}

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		long userID = commandCore.getUserID();
		User target = event.getOption("target", OptionMapping::getAsUser);
		long nowHave = CommandBlocksHandle.getCommandBlocks(userID);
		if (target == null)
		{
			event.reply("Impossible, this is required!").queue();
			return;
		}
		long targetID = target.getIdLong();

		String transferAmountString = event.getOption("amount", OptionMapping::getAsString);
		if (transferAmountString == null)
		{
			event.reply("Impossible, this is required!").queue();
			return;
		}

		long transferAmount;
		if (transferAmountString.matches("\\d+"))
			transferAmount = Long.parseLong(transferAmountString);
		else if (transferAmountString.matches("\\d+%"))
		{
			long percentage = Long.parseLong(transferAmountString.substring(0, transferAmountString.length() - 1));
			if (percentage > 100L) //超過100%
			{
				event.reply(JsonHandle.getJsonKey(userID, "transfer.wrong_percent").formatted(percentage)).queue();
				return;
			}
			transferAmount = nowHave * percentage / 100;
		}
		else
		{
			event.reply(JsonHandle.getJsonKey(userID, "transfer.wrong_argument")).queue();
			return;
		}

		if (nowHave < transferAmount)
		{
			event.reply(JsonHandle.getJsonKey(userID, "transfer.not_enough").formatted(transferAmount, nowHave)).queue();
			return;
		}

		CommandBlocksHandle.setCommandBlocks(target.getIdLong(), Algorithm.safeAdd(CommandBlocksHandle.getCommandBlocks(targetID), transferAmount));
		CommandBlocksHandle.setCommandBlocks(userID, nowHave - transferAmount);
	}
}