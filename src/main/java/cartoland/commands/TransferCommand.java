package cartoland.commands;

import cartoland.events.CommandUsage;
import cartoland.utilities.CommandBlocksHandle;
import cartoland.utilities.CommonFunctions;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * {@code TransferCommand} is an execution when a user uses /transfer command. This class implements {@link ICommand}
 * interface, which is for the commands HashMap in {@link CommandUsage}.
 *
 * @since 1.5
 * @author Alex Cai
 */
public class TransferCommand implements ICommand
{
	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		long userID = event.getUser().getIdLong();

		User target = event.getOption("target", CommonFunctions.getAsUser);
		if (target == null)
		{
			event.reply("Impossible, this is required!").queue();
			return;
		}
		if (target.isBot() || target.isSystem()) //是機器人或系統
		{
			event.reply(JsonHandle.getStringFromJsonKey(userID, "transfer.wrong_user")).queue(); //不能轉帳
			return;
		}

		long targetID = target.getIdLong();
		if (userID == targetID)
		{
			event.reply(JsonHandle.getStringFromJsonKey(userID, "transfer.self_transfer")).queue();
			return;
		}

		String transferAmountString = event.getOption("amount", CommonFunctions.getAsString);
		if (transferAmountString == null)
		{
			event.reply("Impossible, this is required!").queue();
			return;
		}

		CommandBlocksHandle.LotteryData myData = CommandBlocksHandle.getLotteryData(userID);
		CommandBlocksHandle.LotteryData targetData = CommandBlocksHandle.getLotteryData(targetID);

		long nowHave = myData.getBlocks();
		long transferAmount = CommandBlocksHandle.analyzeCommandString(transferAmountString, nowHave);
		if (transferAmount == CommandBlocksHandle.WRONG_PERCENT)
		{
			event.reply(JsonHandle.getStringFromJsonKey(userID, "transfer.wrong_percent").formatted(transferAmountString)).queue();
			return;
		}
		if (transferAmount == CommandBlocksHandle.WRONG_ARGUMENT)
		{
			event.reply(JsonHandle.getStringFromJsonKey(userID, "transfer.wrong_argument")).queue();
			return;
		}

		if (transferAmount == 0L) //不能轉0
		{
			event.reply(JsonHandle.getStringFromJsonKey(userID, "transfer.wrong_argument")).queue();
			return;
		}

		if (nowHave < transferAmount) //不夠轉
		{
			event.reply(JsonHandle.getStringFromJsonKey(userID, "transfer.not_enough").formatted(transferAmount, nowHave)).queue();
			return;
		}

		long afterHave = nowHave - transferAmount;
		event.reply(JsonHandle.getStringFromJsonKey(userID, "transfer.success").formatted(transferAmount, target.getEffectiveName(), afterHave)).queue();

		targetData.addBlocks(transferAmount);
		myData.setBlocks(afterHave);
	}
}