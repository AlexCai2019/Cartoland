package cartoland.commands;

import cartoland.utilities.*;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

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
		subCommands.put("get", new Get());
		subCommands.put("bet", new Bet());
		subCommands.put("ranking", new Ranking());
	}

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		subCommands.get(event.getSubcommandName()).commandProcess(event);
	}
}

/**
 * {@code Get} is a class that handles one of the sub commands of {@code /lottery} command, which is {@code /lottery get}.
 *
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
			event.reply(JsonHandle.getStringFromJsonKey(user.getIdLong(), "lottery.get.invalid_get")).queue();
			return;
		}

		event.reply(JsonHandle.getStringFromJsonKey(user.getIdLong(), "lottery.get.query")
							.formatted(target.getAsTag(), CommandBlocksHandle.get(target.getIdLong()))).queue();
	}
}

/**
 * {@code Bet} is a class that handles one of the sub commands of {@code /lottery} command, which is {@code /lottery bet}.
 *
 * @since 1.6
 * @author Alex Cai
 */
class Bet implements ICommand
{
	private final Pattern numberRegex = Pattern.compile("\\d+");
	private final Pattern percentRegex = Pattern.compile("\\d+%");
	private int win = 0;
	private int lose = 0;
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

		long afterBet;
		String result;
		if (Algorithm.chance(50)) //賭贏 可用random.nextBoolean()
		{
			afterBet = Algorithm.safeAdd(nowHave, bet);
			result = JsonHandle.getStringFromJsonKey(userID, "lottery.bet.win");
			win++;
		}
		else //賭輸
		{
			afterBet = nowHave - bet;
			result = JsonHandle.getStringFromJsonKey(userID, "lottery.bet.lose");
			lose++;
		}

		String replyMessage = JsonHandle.getStringFromJsonKey(userID, "lottery.bet.result").formatted(bet, result, afterBet);
		if (afterBet == 0)
			replyMessage += "\n" + JsonHandle.getStringFromJsonKey(userID, "lottery.bet.play_with_your_limit");

		final long finalAfterBet = afterBet;
		event.reply(replyMessage).queue(interactionHook -> CommandBlocksHandle.set(userID, finalAfterBet));

		FileHandle.log(win + " / " + lose);
	}
}

/**
 * {@code Ranking} is a class that handles one of the sub commands of {@code /lottery} command, which is {@code /lottery ranking}.
 *
 * @since 1.6
 * @author Alex Cai
 */
class Ranking implements ICommand
{
	private final List<UserNameAndBlocks> forSort = new ArrayList<>(); //需要排序的list
	private String lastReply = ""; //上一次回覆過的字串

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		Integer pageBox = event.getOption("page", OptionMapping::getAsInt);
		int page = pageBox != null ? pageBox : 1; //page從1開始

		//假設總共有27位使用者 (27 - 1) / 10 + 1 = 3 總共有3頁
		int maxPage = (CommandBlocksHandle.size() - 1) / 10 + 1;
		if (page > maxPage) //超出範圍
			page = maxPage; //同上例子 就改成顯示第3頁

		User user = event.getUser(); //這次事件的使用者

		if (!CommandBlocksHandle.changed) //距離上一次排序 沒有任何變動
		{
			event.reply(lastReply).queue();
			return;
		}

		final int finalPage = page; //lambda要用
		event.deferReply().queue(interactionHook -> //發送機器人正在思考中 並在回呼函式內執行排序等行為
		{
			forSort.clear(); //清除forSort
			CommandBlocksHandle.getKeySet().forEach(userID -> //走訪所有的ID
			{
				String userName = IDAndEntities.idAndNames.get(userID); //透過ID從JSON資料庫內獲得名字
				if (userName != null)
					forSort.add(new UserNameAndBlocks(userName, CommandBlocksHandle.get(userID))); //新增一對名字和方塊數量
			});

			//排序
			forSort.sort((user1, user2) -> Long.compare(user2.blocks(), user1.blocks())); //方塊較多的在前面 方塊較少的在後面

			CommandBlocksHandle.changed = false; //已經排序過了
			interactionHook.sendMessage(lastReply = replyString(user, finalPage, maxPage)).queue();
		});
	}

	private final StringBuilder rankBuilder = new StringBuilder();

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
				.append("\n--------------------\nYou are rank #")
				.append(forSort.indexOf(new UserNameAndBlocks(user.getAsTag(), blocks)) + 1)
				.append(", with ")
				.append(blocks)
				.append(" command blocks.\n\n");

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

		rankBuilder.append("\n--------------------\nPage ")
				.append(page)
				.append(" / ")
				.append(maxPage)
				.append("\n```");

		return rankBuilder.toString();
	}
}

/**
 * {@code UserNameAndBlocks} has a String userName and long blocks.
 *
 * @since 1.6
 * @author Alex Cai
 */
record UserNameAndBlocks(String userName, long blocks)
{
	@Override
	public boolean equals(Object o)
	{ //不需要this == o 因為幾乎不會碰到這樣的情況
		return o instanceof UserNameAndBlocks that && this.userName.equals(that.userName);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(userName, blocks);
	}
}