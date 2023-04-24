package cartoland.commands;

import cartoland.events.CommandUsage;
import cartoland.utilities.Algorithm;
import cartoland.utilities.CommandBlocksHandle;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.regex.Pattern;

/**
 * {@code TransferCommand} is an execution when a user uses /transfer command. This class implements {@link ICommand}
 * interface, which is for the commands HashMap in {@link CommandUsage}.
 *
 * @since 1.5
 * @author Alex Cai
 */
public class TransferCommand implements ICommand
{
	private final Pattern numberRegex = Pattern.compile("\\d+");
	private final Pattern percentRegex = Pattern.compile("\\d+%");

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		long userID = event.getUser().getIdLong();

		User target = event.getOption("target", OptionMapping::getAsUser);
		if (target == null)
		{
			event.reply("Impossible, this is required!").queue();
			return;
		}
		if (target.isBot() || target.isSystem())
		{
			event.reply(JsonHandle.getStringFromJsonKey(userID, "transfer.wrong_user")).queue();
			return;
		}

		long targetID = target.getIdLong();
		if (userID == targetID)
		{
			event.reply(JsonHandle.getStringFromJsonKey(userID, "transfer.self_transfer")).queue();
			return;
		}

		String transferAmountString = event.getOption("amount", OptionMapping::getAsString);
		if (transferAmountString == null)
		{
			event.reply("Impossible, this is required!").queue();
			return;
		}

		long nowHave = CommandBlocksHandle.get(userID);
		long transferAmount;
		if (numberRegex.matcher(transferAmountString).matches())
			transferAmount = Long.parseLong(transferAmountString);
		else if (percentRegex.matcher(transferAmountString).matches())
		{
			long percentage = Long.parseLong(transferAmountString.substring(0, transferAmountString.length() - 1));
			if (percentage > 100L) //超過100%
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "transfer.wrong_percent").formatted(percentage)).queue();
				return;
			}
			transferAmount = nowHave * percentage / 100;
		}
		else
		{
			event.reply(JsonHandle.getStringFromJsonKey(userID, "transfer.wrong_argument")).queue();
			return;
		}

		if (nowHave < transferAmount)
		{
			event.reply(JsonHandle.getStringFromJsonKey(userID, "transfer.not_enough").formatted(transferAmount, nowHave)).queue();
			return;
		}

		long afterHave = nowHave - transferAmount;
		event.reply(JsonHandle.getStringFromJsonKey(userID, "transfer.success").formatted(transferAmount, target.getAsTag(), afterHave))
				.queue(interactionHook ->
				{
					CommandBlocksHandle.set(targetID, Algorithm.safeAdd(CommandBlocksHandle.get(targetID), transferAmount));
					CommandBlocksHandle.set(userID, afterHave);
				});
	}
}