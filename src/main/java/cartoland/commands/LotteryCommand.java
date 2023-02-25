package cartoland.commands;

import cartoland.events.CommandUsage;
import cartoland.utilities.Algorithm;
import cartoland.utilities.CommandBlocksHandle;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.Random;

/**
 * {@code LotteryCommand} is an execution when a user uses /lottery command. This class implements {@link ICommand}
 * interface, which is for the commands HashMap in {@link CommandUsage}. This class doesn't have a backend class
 * since all the works can be done here.
 *
 * @since 1.4
 * @author Alex Cai
 */
public class LotteryCommand implements ICommand
{
	private final CommandUsage commandCore;
	private final Random random = new Random();

	public LotteryCommand(CommandUsage commandUsage)
	{
		commandCore = commandUsage;
	}

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		long userID = commandCore.getUserID();
		long nowHave = CommandBlocksHandle.getCommandBlocks(userID);
		String betString = event.getOption("bet", OptionMapping::getAsString);

		if (betString == null) //不帶參數
		{
			event.reply(JsonHandle.getJsonKey(userID, "lottery.query").formatted(nowHave)).queue();
			return;
		}

		long bet;

		if (betString.matches("\\d+")) //賭數字
			bet = Long.parseLong(betString);
		else if (betString.matches("\\d+%")) //賭%數
		{
			long percentage = Long.parseLong(betString.substring(0, betString.length() - 1));
			if (percentage > 100L) //超過100%
			{
				event.reply(JsonHandle.getJsonKey(userID, "lottery.wrong_percent").formatted(percentage)).queue();
				return;
			}
			bet = nowHave * percentage / 100;
		}
		else //都不是
		{
			event.reply(JsonHandle.getJsonKey(userID, "lottery.wrong_argument")).queue();
			return;
		}

		if (nowHave < bet) //如果現有的比要賭的還少
		{
			event.reply(JsonHandle.getJsonKey(userID, "lottery.not_enough").formatted(bet, nowHave)).queue();
			return;
		}

		long afterBet;
		String result;
		if (random.nextBoolean()) //賭贏
		{
			afterBet = Algorithm.safeAdd(nowHave, bet);
			result = JsonHandle.getJsonKey(userID, "lottery.win");
		}
		else //賭輸
		{
			afterBet = nowHave - bet;
			result = JsonHandle.getJsonKey(userID, "lottery.lose");
		}
		long finalAfterBet = afterBet;
		event.reply(JsonHandle.getJsonKey(userID, "lottery.result").formatted(bet, result, afterBet))
				.queue(interactionHook -> CommandBlocksHandle.setCommandBlocks(userID, finalAfterBet));
	}
}