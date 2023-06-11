package cartoland.commands;

import cartoland.utilities.*;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.*;
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
	private final Map<String, ICommand> subCommands = new HashMap<>();

	public LotteryCommand()
	{
		subCommands.put("get", event ->
		{
			User user = event.getUser();
			User target = event.getOption("target", CommonFunctions.getAsUser);
			if (target == null) //沒有填 預設是自己
				target = user;
			else if (target.isBot() || target.isSystem())
			{
				event.reply(JsonHandle.getStringFromJsonKey(user.getIdLong(), "lottery.get.invalid_get")).queue();
				return;
			}

			event.reply(JsonHandle.getStringFromJsonKey(user.getIdLong(), "lottery.get.query")
								.formatted(target.getName(), CommandBlocksHandle.get(target.getIdLong()))).queue();
		});
		subCommands.put("bet", new BetSubCommand());
		subCommands.put("ranking", new RankingSubCommand());
	}

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		subCommands.get(event.getSubcommandName()).commandProcess(event);
	}
}

/**
 * {@code BetSubCommand} is a class that handles one of the sub commands of {@code /lottery} command, which is
 * {@code /lottery bet}.
 *
 * @since 1.6
 * @author Alex Cai
 */
class BetSubCommand implements ICommand
{
	private final Random random = new Random(); //不使用Algorithm.chance
	private final Pattern numberRegex = Pattern.compile("\\d+");
	private final Pattern percentRegex = Pattern.compile("\\d+%");
	private static final long MAXIMUM = 1000000L;

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		long userID = event.getUser().getIdLong();
		long nowHave = CommandBlocksHandle.get(userID);
		String betString = event.getOption("bet", CommonFunctions.getAsString);

		if (betString == null) //不帶參數
		{
			event.reply("Impossible, this is required!").queue();
			return;
		}

		long bet;

		if (numberRegex.matcher(betString).matches()) //賭數字
			bet = Long.parseLong(betString);
		else if (percentRegex.matcher(betString).matches()) //賭%數
		{
			long percentage = Long.parseLong(betString.substring(0, betString.length() - 1));
			if (percentage > 100L) //超過100%
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "lottery.bet.wrong_percent").formatted(percentage)).queue();
				return;
			}
			bet = nowHave * percentage / 100;
		}
		else //都不是
		{
			event.reply(JsonHandle.getStringFromJsonKey(userID, "lottery.bet.wrong_argument")).queue();
			return;
		}

		if (bet > MAXIMUM)
		{
			event.reply(JsonHandle.getStringFromJsonKey(userID, "lottery.bet.too_much").formatted(bet, MAXIMUM)).queue();
			return;
		}

		if (nowHave < bet) //如果現有的比要賭的還少
		{
			event.reply(JsonHandle.getStringFromJsonKey(userID, "lottery.bet.not_enough").formatted(bet, nowHave)).queue();
			return;
		}

		final long afterBet;
		String result;
		boolean win = random.nextBoolean();

		if (win) //賭贏
		{
			afterBet = Algorithm.safeAdd(nowHave, bet);
			result = JsonHandle.getStringFromJsonKey(userID, "lottery.bet.win");
		}
		else //賭輸
		{
			afterBet = nowHave - bet;
			result = JsonHandle.getStringFromJsonKey(userID, "lottery.bet.lose");
		}

		String replyMessage = JsonHandle.getStringFromJsonKey(userID, "lottery.bet.result").formatted(bet, result, afterBet);
		if (bet == nowHave) //梭哈
			replyMessage += "\n" + (win ? "https://www.youtube.com/watch?v=RbMjxQEZ1IQ" : JsonHandle.getStringFromJsonKey(userID, "lottery.bet.play_with_your_limit"));

		event.reply(replyMessage).queue(interactionHook -> CommandBlocksHandle.set(userID, afterBet));
	}
}

/**
 * {@code RankingSubCommand} is a class that handles one of the sub commands of {@code /lottery} command, which is
 * {@code /lottery ranking}.
 *
 * @since 1.6
 * @author Alex Cai
 */
class RankingSubCommand implements ICommand
{
	private final List<UserNameAndBlocks> forSort = new ArrayList<>(100); //需要排序的list
	private String lastReply; //上一次回覆過的字串
	private int lastPage = -1; //上一次查看的頁面

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		Integer pageBox = event.getOption("page", CommonFunctions.getAsInt);
		int page = pageBox != null ? pageBox : 1; //page從1開始

		//假設總共有27位使用者 (27 - 1) / 10 + 1 = 3 總共有3頁
		int maxPage = (CommandBlocksHandle.size() - 1) / 10 + 1;
		if (page > maxPage) //超出範圍
			page = maxPage; //同上例子 就改成顯示第3頁

		if (!CommandBlocksHandle.changed) //指令方塊 距離上一次排序 沒有任何變動
		{
			if (page != lastPage) //有換頁
				lastReply = replyString(event.getUser(), page, maxPage);
			event.reply(lastReply).queue();
			return;
		}

		forSort.clear(); //清除forSort
		CommandBlocksHandle.getKeySet().forEach(userID -> //走訪所有的ID
		{
			String userName = IDAndEntities.idAndNames.get(userID); //透過ID從Map資料庫內獲得名字
			if (userName != null)
				forSort.add(new UserNameAndBlocks(userName, CommandBlocksHandle.get(userID))); //新增一對名字和方塊數量
		});

		//排序
		forSort.sort((user1, user2) -> Long.compare(user2.blocks(), user1.blocks())); //方塊較多的在前面 方塊較少的在後面

		CommandBlocksHandle.changed = false; //已經排序過了
		lastPage = page; //換過頁了
		event.reply(lastReply = replyString(event.getUser(), page, maxPage)).queue();
	}

	private final StringBuilder rankBuilder = new StringBuilder();

	/**
	 * Builds a page in the ranking list of command blocks.
	 *
	 * @param user The user who used the command.
	 * @param page The page that the command user want to check.
	 * @param maxPage Maximum of pages that the ranking list has.
	 * @return A page of the ranking list into a single string.
	 * @since 1.6
	 * @author Alex Cai
	 */
	private String replyString(User user, int page, int maxPage)
	{
		//page 從1開始
		int startElement = (page - 1) * 10; //開始的那個元素
		int endElement = startElement + 10; //結束的那個元素
		if (endElement > forSort.size()) //結束的那個元素比list總長還長
			endElement = forSort.size();

		List<UserNameAndBlocks> ranking = forSort.subList(startElement, endElement); //要查看的那一頁
		long blocks = CommandBlocksHandle.get(user.getIdLong()); //本使用者擁有的方塊數

		rankBuilder.setLength(0);
		rankBuilder.append("```ansi\nCommand blocks in ")
				.append(IDAndEntities.cartolandServer.getName())
				.append("\n--------------------\nYou are rank \u001B[36m#")
				.append(forSortBinarySearch(blocks))
				.append("\u001B[0m, with \u001B[36m")
				.append(blocks)
				.append("\u001B[0m command blocks.\n\n");

		for (int i = 0, add = page * 10 - 9, rankingSize = ranking.size(); i < rankingSize; i++) //add = (page - 1) * 10 + 1
		{
			UserNameAndBlocks rank = ranking.get(i);
			rankBuilder.append("[\u001B[36m")
					.append(String.format("%03d", add + i))
					.append("\u001B[0m]\t")
					.append(rank.userName())
					.append(": \u001B[36m")
					.append(rank.blocks())
					.append("\u001B[0m\n");
		}

		return rankBuilder.append("\n--------------------\n")
				.append(page)
				.append(" / ")
				.append(maxPage)
				.append("\n```")
				.toString();
	}

	/**
	 * Use binary search to find the index of the user that has these blocks in the {@link #forSort} list, in order to find
	 * the ranking of a user. These code was stole... was <i>"borrowed"</i> from {@link java.util.Collections#binarySearch(List, Object)}
	 *
	 * @param blocks The number of blocks that are used to match in the {@link #forSort} list.
	 * @return The index of the user that has these blocks in the {@link #forSort} list and add 1, because though an
	 * array is 0-indexed, but the ranking that are going to display should be 1-indexed.
	 * @since 2.0
	 * @author Alex Cai
	 */
	private int forSortBinarySearch(long blocks)
	{
		long midValue;
		for (int low = 0, middle, high = forSort.size() - 1; low < high;)
		{
			middle = (low + high) >>> 1;
			midValue = forSort.get(middle).blocks();

			if (midValue < blocks)
				high = middle - 1;
			else if (midValue > blocks)
				low = middle + 1;
			else
				return middle + 1;
		}
		return 0;
	}
}

/**
 * {@code UserNameAndBlocks} has a String userName and long blocks.
 *
 * @since 1.6
 * @author Alex Cai
 */
record UserNameAndBlocks(String userName, long blocks) {}