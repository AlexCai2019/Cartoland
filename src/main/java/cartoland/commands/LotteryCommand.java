package cartoland.commands;

import cartoland.Cartoland;
import cartoland.utilities.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;
import java.util.Random;

/**
 * {@code LotteryCommand} is an execution when a user uses /lottery command. This class implements {@link ICommand}
 * interface, which is for the commands HashMap in {@link cartoland.events.CommandUsage}. This class doesn't
 * have a backend class since all the works can be done here.
 *
 * @since 1.4
 * @author Alex Cai
 */
public class LotteryCommand extends HasSubcommands
{
	private static final Random random = new Random(); //不使用Algorithm.chance
	private static final long MAXIMUM = 1000000L;
	private static final byte INVALID_BET = -1;

	public static final String GET = "get";
	public static final String BET = "bet";
	public static final String RANKING = "ranking";
	public static final String DAILY = "daily";
	public static final String SLOT = "slot";

	public LotteryCommand()
	{
		super(5);
		subcommands.put(GET, new GetSubCommand());
		subcommands.put(BET, new BetSubCommand());
		subcommands.put(RANKING, new RankingSubCommand());
		subcommands.put(DAILY, new DailySubCommand());
		subcommands.put(SLOT, new SlotSubCommand());
	}

	private static ObjectAndString createValidBet(String betString, long userID, long nowHave)
	{
		ObjectAndString validBet = new ObjectAndString();
		long bet;
		if (RegularExpressions.BET_NUMBER_REGEX.matcher(betString).matches()) //賭數字
			bet = Long.parseLong(betString);
		else if (RegularExpressions.BET_PERCENT_REGEX.matcher(betString).matches()) //賭%數
		{
			short percentage = Short.parseShort(betString.substring(0, betString.length() - 1));
			if (percentage > 100) //百分比格式錯誤 不能賭超過100%
				return validBet.string(JsonHandle.getString(userID, "lottery.bet.wrong_percent", betString));
			bet = nowHave * percentage / 100;
		}
		else if ("all".equalsIgnoreCase(betString))
			bet = nowHave;
		else if ("half".equalsIgnoreCase(betString))
			bet = nowHave >> 1;
		else if ("quarter".equalsIgnoreCase(betString))
			bet = nowHave >> 2;
		else //都不是
			return validBet.string(JsonHandle.getString(userID, "lottery.bet.wrong_argument")); //格式錯誤

		if (bet == 0L) //不能賭0
			return validBet.string(JsonHandle.getString(userID, "lottery.bet.wrong_argument")); //格式錯誤
		if (bet > MAXIMUM) //限紅
			return validBet.string(JsonHandle.getString(userID, "lottery.bet.too_much", bet, MAXIMUM));
		if (nowHave < bet) //如果現有的比要賭的還少
			return validBet.string(JsonHandle.getString(userID, "lottery.bet.not_enough", bet, nowHave));

		return validBet.object(bet);
	}

	/**
	 * {@code GetSubCommand} is a class that handles one of the subcommands of {@code /lottery} command, which is
	 * {@code /lottery get}.
	 *
	 * @since 1.6
	 * @author Alex Cai
	 */
	private static class GetSubCommand implements ICommand
	{
		@Override
		public void commandProcess(SlashCommandInteractionEvent event)
		{
			User user = event.getUser();
			User target = event.getOption("target", user, CommonFunctions.getAsUser); //目標 沒有填預設是自己
			if (target.isBot() || target.isSystem())
			{
				event.reply(JsonHandle.getString(user.getIdLong(), "lottery.get.invalid_get")).queue();
				return;
			}

			CommandBlocksHandle.LotteryData lotteryData = CommandBlocksHandle.getLotteryData(target.getIdLong());
			if (!event.getOption("display_detail", Boolean.FALSE, CommonFunctions.getAsBoolean)) //不顯示細節
			{
				event.reply(JsonHandle.getString(user.getIdLong(), "lottery.get.query", lotteryData.getName(), lotteryData.getBlocks())).queue();
				return;
			}
			int betWon = lotteryData.getBetWon(); //贏的次數
			int betLost = lotteryData.getBetLost(); //輸的次數
			int betShowHandWon = lotteryData.getBetShowHandWon(); //梭哈贏的次數
			int betShowHandLost = lotteryData.getBetShowHandLost(); //梭哈輸的次數
			int slotWon = lotteryData.getSlotWon(); //拉霸機贏的次數
			int slotLost = lotteryData.getSlotLost(); //拉霸機輸的次數
			int slotShowHandWon = lotteryData.getSlotShowHandWon(); //拉霸機梭哈贏的次數
			int slotShowHandLost = lotteryData.getSlotShowHandLost(); //拉霸機梭哈輸的次數
			event.reply(JsonHandle.getString(user.getIdLong(), "lottery.get.query_detail",
					lotteryData.getName(), lotteryData.getBlocks(),
					betWon + betLost, betWon, betLost,
					betShowHandWon + betShowHandLost, betShowHandWon, betShowHandLost,
					slotWon + slotLost, slotWon, slotLost,
					slotShowHandWon + slotShowHandLost, slotShowHandWon, slotShowHandLost)).queue();
		}
	}

	/**
	 * {@code BetSubCommand} is a class that handles one of the subcommands of {@code /lottery} command, which is
	 * {@code /lottery bet}.
	 *
	 * @since 1.6
	 * @author Alex Cai
	 */
	private static class BetSubCommand implements ICommand
	{
		@Override
		public void commandProcess(SlashCommandInteractionEvent event)
		{
			long userID = event.getUser().getIdLong();
			CommandBlocksHandle.LotteryData lotteryData = CommandBlocksHandle.getLotteryData(userID);
			long nowHave = lotteryData.getBlocks();

			ObjectAndString validBet = createValidBet(event.getOption("bet", "", CommonFunctions.getAsString), userID, nowHave);
			String errorMessage = validBet.string();
			if (!errorMessage.isEmpty()) //有錯誤訊息
			{
				event.reply(errorMessage).setEphemeral(true).queue();
				return;
			}
			long bet = (Long) validBet.object(); //沒有錯誤訊息 就轉換
			long afterBet;
			String result;
			boolean win = random.nextBoolean(); //輸贏
			boolean showHand = bet == nowHave; //梭哈

			if (win) //賭贏
			{
				afterBet = Algorithm.safeAdd(nowHave, bet);
				result = JsonHandle.getString(userID, "lottery.bet.win");
			}
			else //賭輸
			{
				afterBet = nowHave - bet;
				result = JsonHandle.getString(userID, "lottery.bet.lose");
			}

			StringBuilder replyBuilder = new StringBuilder(JsonHandle.getString(userID, "lottery.bet.result", bet, result, afterBet));
			if (showHand)
			{
				if (win)
					replyBuilder.append("\nhttps://www.youtube.com/watch?v=2s2QXGc1Ros");
				else
					replyBuilder.append('\n').append(JsonHandle.getString(userID, "lottery.bet.play_with_your_limit"));
			}
			event.reply(replyBuilder.toString()).queue(); //盡快回覆比較好

			lotteryData.addGame(win, showHand); //紀錄勝場和是否梭哈
			lotteryData.setBlocks(afterBet); //設定方塊
		}
	}

	/**
	 * {@code RankingSubCommand} is a class that handles one of the subcommands of {@code /lottery} command, which is
	 * {@code /lottery ranking}.
	 *
	 * @since 1.6
	 * @author Alex Cai
	 */
	private static class RankingSubCommand implements ICommand
	{
		private List<CommandBlocksHandle.LotteryData> forSort; //需要排序的list
		private String lastReply; //上一次回覆過的字串
		private int lastPage = -1; //上一次查看的頁面
		private long lastUser = -1L; //上一次使用指令的使用者
		private int maxPage; //目前有幾頁

		@Override
		public void commandProcess(SlashCommandInteractionEvent event)
		{
			long userID = event.getUser().getIdLong();

			int inputPage = event.getOption("page", 1, CommonFunctions.getAsInt); //page從1開始 預設1
			int page;

			//假設總共有27位使用者 (27 - 1) / 10 + 1 = 3 總共有3頁
			maxPage = (CommandBlocksHandle.lotteryDataList.size() - 1) / 10 + 1;
			if (inputPage > maxPage) //超出範圍
				page = maxPage; //同上例子 就改成顯示第3頁
			else if (inputPage < 0) //-1 = 最後一頁, -2 = 倒數第二頁 負太多就變第一頁
				page = (-inputPage < maxPage) ? maxPage + inputPage + 1 : 1;
			else
				page = inputPage;

			boolean sameUser = userID == lastUser;
			lastUser = userID;
			boolean samePage = page == lastPage;
			lastPage = page; //換過頁了

			if (!CommandBlocksHandle.changed) //指令方塊 距離上一次排序 沒有任何變動
			{
				if (!samePage || !sameUser) //有換頁 或 不是同一位使用者
					lastReply = replyString(userID, page); //重新建立字串
				event.reply(lastReply).queue();
				return; //省略排序
			}

			forSort = CommandBlocksHandle.lotteryDataList; //直接copy reference

			//排序
			forSort.sort((user1, user2) -> Long.compare(user2.getBlocks(), user1.getBlocks())); //方塊較多的在前面 方塊較少的在後面

			event.reply(lastReply = replyString(userID, page)).queue();
			CommandBlocksHandle.changed = false; //已經排序過了
		}

		private final StringBuilder rankBuilder = new StringBuilder();

		/**
		 * Builds a page in the ranking list of command blocks.
		 *
		 * @param userID The ID of the user who used the command.
		 * @param page The page that the command user want to check.
		 * @return A page of the ranking list into a single string.
		 * @since 1.6
		 * @author Alex Cai
		 */
		private String replyString(long userID, int page)
		{
			//page 從1開始
			int startElement = (page - 1) * 10; //開始的那個元素
			int endElement = Math.min(startElement + 10, forSort.size()); //結束的那個元素 不可比list總長還長

			List<CommandBlocksHandle.LotteryData> ranking = forSort.subList(startElement, endElement); //要查看的那一頁
			CommandBlocksHandle.LotteryData myData = CommandBlocksHandle.getLotteryData(userID);
			long blocks = myData.getBlocks(); //本使用者擁有的方塊數

			Guild cartoland = Cartoland.getJDA().getGuildById(IDs.CARTOLAND_SERVER_ID);
			rankBuilder.setLength(0);
			rankBuilder.append("```ansi\n")
					.append(JsonHandle.getString(userID, "lottery.ranking.title", cartoland != null ? cartoland.getName() : ""))
					.append("\n--------------------\n")
					.append(JsonHandle.getString(userID, "lottery.ranking.my_rank", forSortBinarySearch(blocks), blocks))
					.append("\n\n");

			for (int i = 0, add = page * 10 - 9, rankingSize = ranking.size(); i < rankingSize; i++) //add = (page - 1) * 10 + 1
			{
				CommandBlocksHandle.LotteryData rank = ranking.get(i);
				rankBuilder.append("[\u001B[36m")
						.append(String.format("%03d", add + i))
						.append("\u001B[0m]\t")
						.append(rank.getName())
						.append(": \u001B[36m")
						.append(String.format("%,d", rank.getBlocks()))
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
			for (int low = 0, middle, high = forSort.size() - 1; low <= high;)
			{
				middle = (low + high) >>> 1;
				midValue = forSort.get(middle).getBlocks();

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
	 * {@code DailySubCommand} is a class that handles one of the subcommands of {@code /lottery} command, which is
	 * {@code /lottery daily}.
	 *
	 * @since 2.1
	 * @author Alex Cai
	 */
	private static class DailySubCommand implements ICommand
	{
		private final byte[] until = { 0,0,0 }; //until[0]為小時 [1]為分鐘 [2]為秒
		private final boolean[] bonus = { false,false,false };

		@Override
		public void commandProcess(SlashCommandInteractionEvent event)
		{
			long userID = event.getUser().getIdLong();
			CommandBlocksHandle.LotteryData lotteryData = CommandBlocksHandle.getLotteryData(userID); //獲取指令方塊資料
			if (!lotteryData.tryClaimDaily(until)) //嘗試daily失敗了
			{
				event.reply(JsonHandle.getString(userID, "lottery.daily.not_yet",
						CommandBlocksHandle.LotteryData.DAILY, until[0], until[1], until[2])).setEphemeral(true).queue();
				return;
			}

			int streak = lotteryData.getStreak(); //連續領取天數

			StringBuilder replyBuilder = new StringBuilder(JsonHandle.getString(userID, "lottery.daily.claimed", CommandBlocksHandle.LotteryData.DAILY))
					.append('\n').append(JsonHandle.getString(userID, "lottery.daily.streak", streak));
			if (lotteryData.tryClaimBonus(bonus)) //有額外
			{
				if (bonus[0]) //週
					replyBuilder.append('\n').append(JsonHandle.getString(userID, "lottery.daily.weekly", streak / 7))
							.append(JsonHandle.getString(userID, "lottery.daily.bonus", CommandBlocksHandle.LotteryData.WEEKLY));
				if (bonus[1]) //月
					replyBuilder.append('\n').append(JsonHandle.getString(userID, "lottery.daily.monthly", streak / 30))
							.append(JsonHandle.getString(userID, "lottery.daily.bonus", CommandBlocksHandle.LotteryData.MONTHLY));
				if (bonus[2]) //年
					replyBuilder.append('\n').append(JsonHandle.getString(userID, "lottery.daily.yearly", streak / 365))
							.append(JsonHandle.getString(userID, "lottery.daily.bonus", CommandBlocksHandle.LotteryData.YEARLY));
			}

			event.reply(replyBuilder.append('\n')
								.append(JsonHandle.getString(userID, "lottery.daily.now_have", lotteryData.getBlocks()))
								.toString()).queue();
		}
	}

	/**
	 * {@code SlotSubCommand} is a class that handles one of the subcommands of {@code /lottery} command, which is
	 * {@code /lottery slot}.
	 *
	 * @since 2.1
	 * @author Alex Cai
	 */
	private static class SlotSubCommand implements ICommand
	{
		private final EmojiData[] emojis =
		{
			new EmojiData("learned",        IDs.LEARNED_EMOJI_ID), //宇宙貓貓
			new EmojiData("chaowendela",    IDs.CHAO_WEN_DE_LA_EMOJI_ID), //超穩的啦
			new EmojiData("cartoland_logo", IDs.CARTOLAND_LOGO_EMOJI_ID), //創聯logo
			new EmojiData("haha",           IDs.HAHA_EMOJI_ID), //海綿寶寶笑
			new EmojiData("pika",           IDs.PIKA_EMOJI_ID), //驚訝皮卡丘
			new EmojiData("cool_pika",      IDs.COOL_PIKA_EMOJI_ID), //墨鏡皮卡丘
			new EmojiData("ya",             IDs.YA_EMOJI_ID)  //好耶
		};

		@Override
		public void commandProcess(SlashCommandInteractionEvent event)
		{
			long userID = event.getUser().getIdLong();
			CommandBlocksHandle.LotteryData lotteryData = CommandBlocksHandle.getLotteryData(userID);
			long nowHave = lotteryData.getBlocks();

			ObjectAndString validBet = createValidBet(event.getOption("bet", "", CommonFunctions.getAsString), userID, nowHave);
			String errorMessage = validBet.string();
			if (!errorMessage.isEmpty()) //有錯誤訊息
			{
				event.reply(errorMessage).setEphemeral(true).queue();
				return;
			}
			long bet = (Long) validBet.object(); //沒有錯誤訊息 就轉換
			if (bet == INVALID_BET) //輸入有誤
				return; //直接結束 createValidBet方法內已經reply過了

			EmojiData[] slotResults =
			{
				Algorithm.randomElement(emojis),
				Algorithm.randomElement(emojis),
				Algorithm.randomElement(emojis)
			}; //轉的結果

			boolean win = (slotResults[0].ID == slotResults[1].ID && slotResults[1].ID == slotResults[2].ID); //完全相同
			boolean showHand = bet == nowHave; //梭哈
			String result;
			long afterBet;
			if (win) //賭贏
			{
				afterBet = Algorithm.safeAdd(nowHave, bet * 49); //機率 1 / 49
				//應該要先減去籌碼後獲得49倍 也就是 * 48才對
				//但是最一開始寫錯了 乾脆將錯就錯 改成50倍 當作福利
				result = JsonHandle.getString(userID, "lottery.bet.win");
			}
			else //賭輸
			{
				afterBet = nowHave - bet;
				result = JsonHandle.getString(userID, "lottery.bet.lose");
			}

			StringBuilder replyBuilder = new StringBuilder("--------------\n| ")
					.append(slotResults[0].emojiFormat)
					.append(" | ")
					.append(slotResults[1].emojiFormat)
					.append(" | ")
					.append(slotResults[2].emojiFormat)
					.append(" |\n--------------\n")
					.append(JsonHandle.getString(userID, "lottery.bet.result", bet, result, afterBet));
			if (showHand)
			{
				if (win)
					replyBuilder.append("\nhttps://www.youtube.com/watch?v=RbMjxQEZ1IQ");
				else
					replyBuilder.append('\n').append(JsonHandle.getString(userID, "lottery.bet.play_with_your_limit"));
			}
			event.reply(replyBuilder.toString()).queue(); //盡快回覆比較好

			lotteryData.addSlot(win, showHand); //紀錄勝場和是否梭哈
			lotteryData.setBlocks(afterBet); //設定方塊
		}

		private static class EmojiData
		{
			private final long ID;
			private final String emojiFormat;

			private EmojiData(String name, long ID)
			{
				emojiFormat = "<:" + name + ':' + Long.toUnsignedString(this.ID = ID) + '>';
			}
		}
	}
}