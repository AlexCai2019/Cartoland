package cartoland.commands;

import cartoland.events.CommandUsage;
import cartoland.utilities.*;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

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
	private final HashMap<String, ICommand> subCommands = new HashMap<>();

	public LotteryCommand()
	{
		subCommands.put("get", new Get());
		subCommands.put("bet", new Bet());
		subCommands.put("ranking", new Ranking());
	}

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		ICommand subCommand = subCommands.get(event.getSubcommandName());
		if (subCommand != null)
			subCommand.commandProcess(event);
	}
}

/**
 * @since 1.6
 * @author Alex Cai
 */
class Get implements ICommand
{
	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		User user = event.getUser();
		User target = event.getOption("target", OptionMapping::getAsUser);
		if (target == null) //沒有填 預設是自己
			target = user;
		else if (target.isBot() || target.isSystem())
		{
			event.reply(JsonHandle.getJsonKey(user.getIdLong(), "lottery.invalid_get")).queue();
			return;
		}

		event.reply(JsonHandle.getJsonKey(user.getIdLong(), "lottery.query")
							.formatted(target.getAsTag(), CommandBlocksHandle.get(target.getIdLong()))).queue();
	}
}

/**
 * @since 1.6
 * @author Alex Cai
 */
class Bet implements ICommand
{
	private final Pattern number = Pattern.compile("\\d+");
	private final Pattern percent = Pattern.compile("\\d+%");

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		long userID = event.getUser().getIdLong();
		long nowHave = CommandBlocksHandle.get(userID);
		String betString = event.getOption("bet", OptionMapping::getAsString);

		if (betString == null) //不帶參數
		{
			event.reply("Impossible, this is required!").queue();
			return;
		}

		long bet;

		if (number.matcher(betString).matches()) //賭數字
			bet = Long.parseLong(betString);
		else if (percent.matcher(betString).matches()) //賭%數
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
		if (Algorithm.chance(50)) //賭贏
		{
			afterBet = Algorithm.safeAdd(nowHave, bet);
			result = JsonHandle.getJsonKey(userID, "lottery.win");
		}
		else //賭輸
		{
			afterBet = nowHave - bet;
			result = JsonHandle.getJsonKey(userID, "lottery.lose");
		}

		String replyMessage = JsonHandle.getJsonKey(userID, "lottery.result").formatted(bet, result, afterBet);
		if (afterBet == 0)
			replyMessage += "\n" + JsonHandle.getJsonKey(userID, "lottery.play_with_your_limit");

		final long finalAfterBet = afterBet;
		event.reply(replyMessage).queue(interactionHook -> CommandBlocksHandle.set(userID, finalAfterBet));
	}
}

/**
 * @since 1.6
 * @author Alex Cai
 */
class Ranking implements ICommand
{
	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		Integer pageBox = event.getOption("page", OptionMapping::getAsInt);
		int page = pageBox != null ? pageBox : 1; //page從1開始

		//假設總共只有27位使用者 但是卻填了第4頁 那麼30 > 27
		if ((page - 1) * 10 > CommandBlocksHandle.length()) //超出範圍
			page = CommandBlocksHandle.length() / 10 + 1; //同上例子 就改成顯示第3頁

		List<CommandBlocksHandle.userAndBlocks> ranking = CommandBlocksHandle.ranking();
		long blocks = CommandBlocksHandle.get(event.getUser().getIdLong());

		StringBuilder rankBuilder = new StringBuilder()
				.append("```ansi\nCommand blocks in ")
				.append(IDAndEntities.cartolandServer.getName())
				.append("\n--------------------\nYou are rank #")
				.append(ranking.indexOf(new CommandBlocksHandle.userAndBlocks(event.getUser(), blocks)) + 1)
				.append(", with ")
				.append(blocks)
				.append(" command blocks.\n");

		//page 從1開始
		int startElement = (page - 1) * 10; //開始的那個元素
		int endElement = startElement + 10; //結束的那個元素
		if (endElement > ranking.size()) //結束的那個元素比list總長還長
			endElement = ranking.size();
		ranking = ranking.subList(startElement, endElement);

		for (int i = 0; i < ranking.size(); i++)
		{
			CommandBlocksHandle.userAndBlocks rank = ranking.get(i);
			rankBuilder.append("[\u001B[36m")
					.append(page + i)
					.append("\u001B[0m]\t")
					.append(rank.user().getAsTag())
					.append(": \u001B[36m")
					.append(rank.blocks())
					.append("\u001B[0m\n");
		}

		rankBuilder.append("\nPage ")
				.append(page)
				.append("\n```");

		event.reply(rankBuilder.toString()).queue();
	}
}