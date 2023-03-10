package cartoland.commands;

import cartoland.utilities.Algorithm;
import cartoland.utilities.CommandBlocksHandle;
import cartoland.utilities.IDAndEntities;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * {@code LotteryCommand} is an execution when a user uses /lottery command. This class implements {@link ICommand}
 * interface, which is for the commands HashMap in {@link cartoland.events.CommandUsage}. This class doesn't
 * have a backend class since all the works can be done here.
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
	private static final long MAXIMUM = 1000000L;

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

		if (bet > MAXIMUM)
		{
			event.reply(JsonHandle.getJsonKey(userID, "lottery.too_much").formatted(bet, MAXIMUM)).queue();
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
	private final StringBuilder rankBuilder = new StringBuilder();

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		Integer pageBox = event.getOption("page", OptionMapping::getAsInt);
		int page = pageBox != null ? pageBox : 1; //page從1開始

		//假設總共有27位使用者 (27 - 1) / 10 + 1 = 3 總共有3頁
		final int maxPage = (CommandBlocksHandle.length() - 1) / 10 + 1;
		if (page > maxPage) //超出範圍
			page = maxPage; //同上例子 就改成顯示第3頁
		final int finalPage = page; //lambda要用

		User user = event.getUser(); //這次事件的使用者
		String userName = user.getAsTag(); //這次事件使用者的名字
		JsonHandle.setUserName(user.getId(), userName); //更新他的名字 這裡獲得的是字串型的ID 和本程式的慣例不同

		event.deferReply().queue(interactionHook -> //發送機器人正在思考中 並在回呼函式內執行排序等行為
		{
			final List<UserNameAndBlocks> forSort = new ArrayList<>(); //需要排序的list
			CommandBlocksHandle.getMap().forEach((userID, blocks) -> //走訪所有的ID和方塊對 注意ID是字串 與慣例不同
			{
				String userNameFromJSON = JsonHandle.getUserName(userID); //透過ID從JSON資料庫內獲得的名字
				forSort.add(new UserNameAndBlocks(userNameFromJSON, ((Number) blocks).longValue())); //新增一對名字和方塊數量
			});

			forSort.sort((user1, user2) -> //排序
			{
				 return Long.compare(user2.blocks(), user1.blocks()); //方塊較多的在前面 方塊較少的在後面
			});

			//page 從1開始
			int startElement = (finalPage - 1) * 10; //開始的那個元素
			int endElement = startElement + 10; //結束的那個元素
			if (endElement > forSort.size()) //結束的那個元素比list總長還長
				endElement = forSort.size();

			List<UserNameAndBlocks> ranking = forSort.subList(startElement, endElement); //要查看的那一頁

			int longestNameLength = ranking.stream()
					.map(rankingUser -> rankingUser.userName().length())
					.max(Integer::compare)
					.orElse(20); //找出名字最長的那個人的字數 找不到的話就用20代替

			long blocks = CommandBlocksHandle.get(user.getIdLong()); //本使用者擁有的方塊數

			rankBuilder.setLength(0);
			rankBuilder.append("```ansi\nCommand blocks in ")
					.append(IDAndEntities.cartolandServer.getName())
					.append("\n--------------------\nYou are rank #")
					.append(forSort.indexOf(new UserNameAndBlocks(userName, blocks)) + 1)
					.append(", with ")
					.append(blocks)
					.append(" command blocks.\n\n");

			for (int i = 0, add = finalPage * 10 - 9; i < ranking.size(); i++) //add = (finalPage - 1) * 10 + 1
			{
				UserNameAndBlocks rank = ranking.get(i);
				rankBuilder.append("[\u001B[36m")
						.append(String.format("%03d", add + i))
						.append("\u001B[0m]\t")
						.append(String.format("%-" + longestNameLength + "s", rank.userName() + ':'))
						.append(" \u001B[36m")
						.append(rank.blocks())
						.append("\u001B[0m\n");
			}

			rankBuilder.append("\n--------------------\nPage ")
					.append(finalPage)
					.append(" / ")
					.append(maxPage)
					.append("\n```");

			interactionHook.sendMessage(rankBuilder.toString()).queue();
		});
	}
}

record UserNameAndBlocks(String userName, long blocks) {}