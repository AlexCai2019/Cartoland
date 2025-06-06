package cartoland.commands;

import cartoland.utilities.CommandBlocksHandle;
import cartoland.utilities.JsonHandle;
import cartoland.utilities.RegularExpressions;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

/**
 * {@code TransferCommand} is an execution when a user uses /transfer command. This class implements {@link ICommand}
 * interface, which is for the commands HashMap in {@link cartoland.events.CommandUsage}.
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

		User target = event.getOption("target", OptionMapping::getAsUser);
		if (target == null)
		{
			event.reply("Impossible, this is required!").queue();
			return;
		}
		if (target.isBot() || target.isSystem()) //是機器人或系統
		{
			event.reply(JsonHandle.getString(userID, "transfer.wrong_user")).queue(); //不能轉帳
			return;
		}

		long targetID = target.getIdLong();
		if (userID == targetID)
		{
			event.reply(JsonHandle.getString(userID, "transfer.self_transfer")).queue();
			return;
		}

		String transferAmountString = event.getOption("amount", "", OptionMapping::getAsString);

		CommandBlocksHandle.LotteryData myData = CommandBlocksHandle.getLotteryData(userID);
		CommandBlocksHandle.LotteryData targetData = CommandBlocksHandle.getLotteryData(targetID);

		long nowHave = myData.getBlocks();
		long transferAmount;
		if (RegularExpressions.BET_NUMBER_REGEX.matcher(transferAmountString).matches()) //轉數字
			transferAmount = Long.parseLong(transferAmountString);
		else if (RegularExpressions.BET_PERCENT_REGEX.matcher(transferAmountString).matches()) //轉%數
		{
			short percentage = Short.parseShort(transferAmountString.substring(0, transferAmountString.length() - 1));
			if (percentage > 100) //百分比格式錯誤 不能賭超過100%
			{
				event.reply(JsonHandle.getString(userID, "transfer.wrong_percent", transferAmountString)).setEphemeral(true).queue();
				return;
			}
			transferAmount = nowHave * percentage / 100;
		}
		else if ("all".equalsIgnoreCase(transferAmountString))
			transferAmount = nowHave;
		else if ("half".equalsIgnoreCase(transferAmountString))
			transferAmount = nowHave >> 1;
		else //都不是
		{
			event.reply(JsonHandle.getString(userID, "transfer.wrong_argument")).setEphemeral(true).queue(); //格式錯誤
			return;
		}

		if (transferAmount == 0L) //不能轉0
		{
			event.reply(JsonHandle.getString(userID, "transfer.wrong_argument")).queue();
			return;
		}

		if (nowHave < transferAmount) //不夠轉
		{
			event.reply(JsonHandle.getString(userID, "transfer.not_enough", transferAmount, nowHave)).queue();
			return;
		}

		long afterHave = nowHave - transferAmount;
		event.reply(JsonHandle.getString(userID, "transfer.success", transferAmount, target.getEffectiveName(), afterHave)).queue();

		targetData.addBlocks(transferAmount);
		myData.setBlocks(afterHave);
	}
}