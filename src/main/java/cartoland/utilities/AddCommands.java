package cartoland.utilities;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.*;
import net.dv8tion.jda.api.interactions.commands.build.*;

import static net.dv8tion.jda.api.interactions.DiscordLocale.*;

import java.util.HashMap;
import java.util.Map;

/**
 * {@code AddCommands} is a utility that holds every command. The only usage is for {@link cartoland.Cartoland#main} to
 * get all commands when registering JDA. Can not be instantiated.
 *
 * @since 1.5
 * @see cartoland.Cartoland
 * @author Alex Cai
 */
public class AddCommands
{
	private AddCommands()
	{
		throw new AssertionError(IDAndEntities.YOU_SHALL_NOT_ACCESS);
	}

	//這裡真的很亂
	//沒有十足的信心 不要編輯這裡的程式碼
	public static final CommandData[] commands =
	{
		Commands.slash("invite", "Get invite link of Cartoland")
				.setDescriptionLocalization(CHINESE_TAIWAN, "獲得創世聯邦的邀請連結")
				.setDescriptionLocalization(CHINESE_CHINA, "获得创世联邦的邀请链接"),

		Commands.slash("help", "Get help with bot commands")
				.addOptions(
						new OptionData(OptionType.STRING, "help_name", "The command you want help with", false, false)
								.setDescriptionLocalization(CHINESE_TAIWAN, "想確認的指令")
								.setDescriptionLocalization(CHINESE_CHINA, "想确认的命令")
								.addChoice("invite", "invite")
								.addChoice("cmd", "cmd")
								.addChoice("faq", "faq")
								.addChoice("dtp", "dtp")
								.addChoice("tool", "tool")
								.addChoice("lang", "lang")
								.addChoice("quote", "quote")),

		Cmd.cmd,
		Cmd.mcc,
		Cmd.command,

		Faq.faq,
		Faq.question,

		Dtp.dtp,
		Dtp.datapack,

		Jira.jira,
		Jira.bug,

		Commands.slash("tool", "Various helpful utilities")
				.setDescriptionLocalization(CHINESE_TAIWAN, "能協助你的工具")
				.setDescriptionLocalization(CHINESE_CHINA, "能协助你的工具")
				.addSubcommands(
						new SubcommandData("uuid_string", "Get UUID data from raw UUID string")
								.setDescriptionLocalization(CHINESE_TAIWAN, "從UUID字串獲得UUID資料")
								.setDescriptionLocalization(CHINESE_CHINA, "从UUID字符串获得UUID数据")
								.addOption(OptionType.STRING, "raw_uuid", "The raw UUID that you want to convert", true, false),
						new SubcommandData("uuid_array", "Get UUID data from UUID integer array")
								.setDescriptionLocalization(CHINESE_TAIWAN, "從uuid整數陣列獲得uuid資料")
								.setDescriptionLocalization(CHINESE_CHINA, "从uuid整数数组获得uuid数据")
								.addOptions(
										new OptionData(OptionType.INTEGER, "0", "The [0] of the array", true, false)
												.setDescriptionLocalization(CHINESE_TAIWAN, "陣列的第[0]項")
												.setDescriptionLocalization(CHINESE_CHINA, "数组的第[0]项"),
										new OptionData(OptionType.INTEGER, "1", "The [1] of the array", true, false)
												.setDescriptionLocalization(CHINESE_TAIWAN, "陣列的第[1]項")
												.setDescriptionLocalization(CHINESE_CHINA, "数组的第[1]项"),
										new OptionData(OptionType.INTEGER, "2", "The [2] of the array", true, false)
												.setDescriptionLocalization(CHINESE_TAIWAN, "陣列的第[2]項")
												.setDescriptionLocalization(CHINESE_CHINA, "数组的第[2]项"),
										new OptionData(OptionType.INTEGER, "3", "The [3] of the array", true, false)
												.setDescriptionLocalization(CHINESE_TAIWAN, "陣列的第[3]項")
												.setDescriptionLocalization(CHINESE_CHINA, "数组的第[3]项")),
						new SubcommandData("color_rgba", "Get color data from RGBA array")
								.setDescriptionLocalization(CHINESE_TAIWAN, "從RGBA陣列獲得顏色資料")
								.setDescriptionLocalization(CHINESE_CHINA, "从RGBA数组获得颜色数据")
								.addOptions(
										new OptionData(OptionType.INTEGER, "red", "Red (0 ~ 255)", true, false)
												.setNameLocalization(CHINESE_TAIWAN, "紅")
												.setNameLocalization(CHINESE_CHINA, "红")
												.setDescriptionLocalization(CHINESE_TAIWAN, "紅 (0 ~ 255)")
												.setDescriptionLocalization(CHINESE_CHINA, "红 (0 ~ 255)"),
										new OptionData(OptionType.INTEGER, "green", "Green (0 ~ 255)", true, false)
												.setNameLocalization(CHINESE_TAIWAN, "綠")
												.setNameLocalization(CHINESE_CHINA, "绿")
												.setDescriptionLocalization(CHINESE_TAIWAN, "綠 (0 ~ 255)")
												.setDescriptionLocalization(CHINESE_CHINA, "绿 (0 ~ 255)"),
										new OptionData(OptionType.INTEGER, "blue", "Blue (0 ~ 255)", true, false)
												.setNameLocalization(CHINESE_TAIWAN, "藍")
												.setNameLocalization(CHINESE_CHINA, "蓝")
												.setDescriptionLocalization(CHINESE_TAIWAN, "藍 (0 ~ 255)")
												.setDescriptionLocalization(CHINESE_CHINA, "蓝 (0 ~ 255)"),
										new OptionData(OptionType.INTEGER, "alpha", "Alpha (0 ~ 255)", true, false)
												.setNameLocalization(CHINESE_TAIWAN, "不透明度")
												.setNameLocalization(CHINESE_CHINA, "不透明度")
												.setDescriptionLocalization(CHINESE_TAIWAN, "不透明度 (0 ~ 255)")
												.setDescriptionLocalization(CHINESE_CHINA, "不透明度 (0 ~ 255)")),
						new SubcommandData("color_integer", "Get color data from RGB integer")
								.setDescriptionLocalization(CHINESE_TAIWAN, "從RGB整數獲得顏色資料")
								.setDescriptionLocalization(CHINESE_CHINA, "从RGB整数获得颜色数据")
								.addOptions(new OptionData(OptionType.STRING, "rgba_or_argb", "RGBA integer, must be decimal or hexadecimal, the order can be RGBA or ARGB", true, false)
													.setDescriptionLocalization(CHINESE_TAIWAN, "RGBA整數，必須是十進位或十六進位，順序可以是RGBA或ARGB")
													.setDescriptionLocalization(CHINESE_CHINA, "RGBA整数，必须是十进制或十六进制，顺序可以是RGBA或ARGB")),
						new SubcommandData("pack_mcmeta", "Generate a pack.mcmeta")
								.addOptions(new OptionData(OptionType.STRING, "pack_type", "Whether this concerns a data pack or a resource pack", true, false)
													.addChoice("Data Pack", "d")
													.addChoice("Resource Pack", "r"))),

		Lang.lang,
		Lang.language,

		Commands.slash("quote", "Display content from a message link")
				.setDescriptionLocalization(CHINESE_TAIWAN, "顯示一個訊息連結的內容")
				.setDescriptionLocalization(CHINESE_CHINA, "显示一个信息链接的内容")
				.addOptions(new OptionData(OptionType.STRING, "link", "The link to the message", true, false)
									.setNameLocalization(CHINESE_TAIWAN, "連結")
									.setNameLocalization(CHINESE_CHINA, "链接")
									.setDescriptionLocalization(CHINESE_TAIWAN, "訊息的連結")
									.setDescriptionLocalization(CHINESE_CHINA, "信息的链接")),

		Commands.slash("youtuber", "Send a link of a YouTube video creator channel")
				.setDescriptionLocalization(CHINESE_TAIWAN, "傳送一個YouTube影片創作者的頻道連結")
				.setDescriptionLocalization(CHINESE_CHINA, "发送一个YouTube视频博主的频道链接")
				.addOptions(new OptionData(OptionType.STRING, "youtuber_name", "The name of the YouTuber", false, true)
									.setNameLocalization(CHINESE_TAIWAN, "名字")
									.setNameLocalization(CHINESE_CHINA, "名字")
									.setDescriptionLocalization(CHINESE_TAIWAN, "YouTuber的名字")
									.setDescriptionLocalization(CHINESE_CHINA, "YouTuber的名字")),

		Commands.slash("introduce", "Introduce a user or update your introduction")
				.setDescriptionLocalization(CHINESE_TAIWAN, "獲取一名使用者的介紹，或更新你的自我介紹")
				.setDescriptionLocalization(CHINESE_CHINA, "获取一名用户的介绍，或更新你的自我介绍")
				.addSubcommands(
				new SubcommandData("user", "User")
						.setDescriptionLocalization(CHINESE_TAIWAN, "使用者")
						.setDescriptionLocalization(CHINESE_CHINA, "用户")
						.addOptions(new OptionData(OptionType.USER, "user", "The user that you want to know", false, false)
											.setNameLocalization(CHINESE_TAIWAN, "使用者")
											.setNameLocalization(CHINESE_CHINA, "用户")
											.setDescriptionLocalization(CHINESE_TAIWAN, "你想認識的使用者")
											.setDescriptionLocalization(CHINESE_CHINA, "你想认识的用户")),
				new SubcommandData("update", "Update your introduction")
						.setDescriptionLocalization(CHINESE_TAIWAN, "更新你的自我介紹")
						.setDescriptionLocalization(CHINESE_CHINA, "更新你的自我介绍")
						.addOptions(new OptionData(OptionType.STRING, "content", "Your self introduction", true, false)
											.setNameLocalization(CHINESE_TAIWAN, "內容")
											.setNameLocalization(CHINESE_CHINA, "内容")
											.setDescriptionLocalization(CHINESE_TAIWAN, "你的自我介紹")
											.setDescriptionLocalization(CHINESE_CHINA, "你的自我介绍"))),

		Commands.slash("megumin", "The best anime girl")
				.setDescriptionLocalization(CHINESE_TAIWAN, "最讚的動漫女孩")
				.setDescriptionLocalization(CHINESE_CHINA, "最赞的动漫女孩")
				.setDescriptionLocalization(JAPANESE, "最高のアニメの女の子"),

		Commands.slash("shutdown", "Use this to shut down the bot")
				.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
		Commands.slash("reload", "Reload all JSON files")
				.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),

		Commands.slash("one_a_two_b", "Play a game of 1A2B")
				.setDescriptionLocalization(CHINESE_TAIWAN, "玩一場1A2B遊戲")
				.setDescriptionLocalization(CHINESE_CHINA, "玩一场1A2B游戏")
				.addOptions(new OptionData(OptionType.INTEGER, "answer", "The answer that you think", false, false)
											.setNameLocalization(CHINESE_TAIWAN, "答案")
											.setNameLocalization(CHINESE_CHINA, "答案")
											.setDescriptionLocalization(CHINESE_TAIWAN, "你認為的答案")
											.setDescriptionLocalization(CHINESE_CHINA, "你认为的答案")),
		Commands.slash("lottery", "Play the lottery game")
				.setDescriptionLocalization(CHINESE_TAIWAN, "抽獎")
				.setDescriptionLocalization(CHINESE_CHINA, "抽奖")
				.addSubcommands(
						new SubcommandData("get", "Gets the amount of command blocks the user owns")
								.setDescriptionLocalization(CHINESE_TAIWAN, "獲得使用者擁有的指令方塊數量")
								.setDescriptionLocalization(CHINESE_CHINA, "获得用户拥有的命令方块数量")
								.addOptions(
										new OptionData(OptionType.USER, "target", "The user that you want to check", false, false)
												.setNameLocalization(CHINESE_TAIWAN, "目標")
												.setNameLocalization(CHINESE_CHINA, "目标")
												.setDescriptionLocalization(CHINESE_TAIWAN, "想確認的使用者")
												.setDescriptionLocalization(CHINESE_CHINA, "想确认的用户")),
						new SubcommandData("bet", "Bet some command blocks")
								.setDescriptionLocalization(CHINESE_TAIWAN, "賭上一些指令方塊")
								.setDescriptionLocalization(CHINESE_CHINA, "赌上一些命令方块")
								.addOptions(
										new OptionData(OptionType.STRING, "bet", "The amount of your command blocks you want to bet", true, false)
												.setNameLocalization(CHINESE_TAIWAN, "賭注")
												.setNameLocalization(CHINESE_CHINA, "賭注")
												.setDescriptionLocalization(CHINESE_TAIWAN, "想賭上的數量")
												.setDescriptionLocalization(CHINESE_CHINA, "想赌上的数量")),
						new SubcommandData("ranking", "Rank all users")
								.setDescriptionLocalization(CHINESE_TAIWAN, "獲得所有使用者們的排行")
								.setDescriptionLocalization(CHINESE_CHINA, "获得所有用户们的排行")
								.addOptions(
										new OptionData(OptionType.INTEGER, "page", "The page of the ranking list", false, false)
												.setNameLocalization(CHINESE_TAIWAN, "頁數")
												.setNameLocalization(CHINESE_CHINA, "页数")
												.setDescriptionLocalization(CHINESE_TAIWAN, "排名清單的頁數")
												.setDescriptionLocalization(CHINESE_CHINA, "排名清单的页数"))),
		Commands.slash("transfer", "Transfer your command blocks")
				.setDescriptionLocalization(CHINESE_TAIWAN, "轉帳你的指令方塊")
				.setDescriptionLocalization(CHINESE_CHINA, "转帐你的命令方块")
				.addOptions(
						new OptionData(OptionType.USER, "target", "The user you want to transfer to", true, false)
								.setNameLocalization(CHINESE_TAIWAN, "目標")
								.setNameLocalization(CHINESE_CHINA, "目标")
								.setDescriptionLocalization(CHINESE_TAIWAN, "想轉帳的目標")
								.setDescriptionLocalization(CHINESE_CHINA, "想转帐的目标"),
						new OptionData(OptionType.STRING, "amount", "The amount of command blocks you want to transfer", true, false)
								.setNameLocalization(CHINESE_TAIWAN, "數量")
								.setNameLocalization(CHINESE_CHINA, "数量")
								.setDescriptionLocalization(CHINESE_TAIWAN, "想轉帳的數量")
								.setDescriptionLocalization(CHINESE_CHINA, "想转帐的数量")),

			//TODO: finish cartoland.commands.MinesweeperCommand
			//TODO: stop being lazy
			//TODO: I didn't finish it on ver 1.6. Yes, very sad. Anyway...
		/*Commands.slash("minesweeper", "Play a minesweeper game")
				.setDescriptionLocalization(CHINESE_TAIWAN, "玩一場踩地雷")
				.setDescriptionLocalization(CHINESE_CHINA, "玩一场扫雷")
				.addOptions(
						new OptionData(OptionType.STRING, "difficulty", "The difficulty of game", true, false)
								.setNameLocalization(CHINESE_TAIWAN, "難度")
								.setNameLocalization(CHINESE_CHINA, "难度")
								.setDescriptionLocalization(CHINESE_TAIWAN, "遊戲的難度")
								.setDescriptionLocalization(CHINESE_CHINA, "游戏的难度")
								.addChoices(
										new Command.Choice("easy", IDAndEntities.Difficulty.EASY)
												.setNameLocalization(CHINESE_TAIWAN, "簡單")
												.setNameLocalization(CHINESE_CHINA, "简单"),
										new Command.Choice("normal", IDAndEntities.Difficulty.NORMAL)
												.setNameLocalization(CHINESE_TAIWAN, "普通")
												.setNameLocalization(CHINESE_CHINA, "普通"),
										new Command.Choice("hard", IDAndEntities.Difficulty.HARD)
												.setNameLocalization(CHINESE_TAIWAN, "困難")
												.setNameLocalization(CHINESE_CHINA, "困难"))),*/

		Commands.message("Raw Text")
				.setNameLocalization(CHINESE_TAIWAN, "原始文字")
				.setNameLocalization(CHINESE_CHINA, "原始文本"),

		Commands.message("Reactions")
				.setNameLocalization(CHINESE_TAIWAN, "反應")
				.setNameLocalization(CHINESE_CHINA, "反应")
	};
}

/**
 * /cmd, /mcc and /command. Can not be instantiated.
 *
 * @since 1.5
 * @see AddCommands
 * @author Alex Cai
 */
class Cmd
{
	private Cmd()
	{
		throw new AssertionError(IDAndEntities.YOU_SHALL_NOT_ACCESS);
	}

	private static final Map<DiscordLocale, String> cmdDescriptions = new HashMap<>();

	static
	{
		cmdDescriptions.put(CHINESE_TAIWAN, "獲得Minecraft指令的協助");
		cmdDescriptions.put(CHINESE_CHINA, "获得Minecraft命令的协助");
	}

	private static final OptionData cmdOption =
			new OptionData(OptionType.STRING, "cmd_name", "The name of a Minecraft command", false, true)
					.setNameLocalization(CHINESE_TAIWAN, "指令名字")
					.setNameLocalization(CHINESE_CHINA, "命令名字")
					.setDescriptionLocalization(CHINESE_TAIWAN, "Minecraft指令的名字")
					.setDescriptionLocalization(CHINESE_CHINA, "Minecraft命令的名字");

	static final SlashCommandData cmd = Commands.slash("cmd", "Get help with Minecraft commands")
			.setDescriptionLocalizations(cmdDescriptions)
			.addOptions(cmdOption);

	static final SlashCommandData mcc = Commands.slash("mcc", "Get help with Minecraft commands")
			.setDescriptionLocalizations(cmdDescriptions)
			.addOptions(cmdOption);

	static final SlashCommandData command = Commands.slash("command", "Get help with Minecraft commands")
			.setDescriptionLocalizations(cmdDescriptions)
			.addOptions(cmdOption);
}

/**
 * /faq and /question. Can not be instantiated.
 *
 * @since 1.5
 * @see AddCommands
 * @author Alex Cai
 */
class Faq
{
	private Faq()
	{
		throw new AssertionError(IDAndEntities.YOU_SHALL_NOT_ACCESS);
	}

	private static final Map<DiscordLocale, String> faqDescriptions = new HashMap<>();

	static
	{
		faqDescriptions.put(CHINESE_TAIWAN, "獲得地圖製作的協助");
		faqDescriptions.put(CHINESE_CHINA, "获得地图制作的协助");
	}

	private static final OptionData faqOption =
			new OptionData(OptionType.STRING, "faq_name", "A question about map making.", false, true)
					.setNameLocalization(CHINESE_TAIWAN, "問題")
					.setNameLocalization(CHINESE_CHINA, "问题")
					.setDescriptionLocalization(CHINESE_TAIWAN, "地圖製作的問題")
					.setDescriptionLocalization(CHINESE_CHINA, "地图制作的问题");

	static final SlashCommandData faq = Commands.slash("faq", "Find answers to map making questions")
			.setDescriptionLocalizations(faqDescriptions)
			.addOptions(faqOption);

	static final SlashCommandData question = Commands.slash("question", "Find answers to map making questions")
			.setDescriptionLocalizations(faqDescriptions)
			.addOptions(faqOption);
}

/**
 * /dtp and /datapack. Can not be instantiated.
 *
 * @since 1.5
 * @see AddCommands
 * @author Alex Cai
 */
class Dtp
{
	private Dtp()
	{
		throw new AssertionError(IDAndEntities.YOU_SHALL_NOT_ACCESS);
	}

	private static final Map<DiscordLocale, String> dtpDescriptions = new HashMap<>();

	static
	{
		dtpDescriptions.put(CHINESE_TAIWAN, "獲得Minecraft資料包的協助");
		dtpDescriptions.put(CHINESE_CHINA, "获得Minecraft数据包的协助");
	}

	private static final OptionData dtpOption =
			new OptionData(OptionType.STRING, "dtp_name", "Minecraft datapack features", false, true)
					.setNameLocalization(CHINESE_TAIWAN, "資料包功能")
					.setNameLocalization(CHINESE_CHINA, "数据包功能")
					.setDescriptionLocalization(CHINESE_TAIWAN, "Minecraft資料包的功能")
					.setDescriptionLocalization(CHINESE_CHINA, "Minecraft数据包的功能");

	static final SlashCommandData dtp = Commands.slash("dtp", "Get help with Minecraft datapack features")
			.setDescriptionLocalizations(dtpDescriptions)
			.addOptions(dtpOption);

	static final SlashCommandData datapack = Commands.slash("datapack", "Get help with Minecraft datapack features")
			.setDescriptionLocalizations(dtpDescriptions)
			.addOptions(dtpOption);
}

/**
 * /lang and /language. Can not be instantiated.
 *
 * @since 1.5
 * @see AddCommands
 * @author Alex Cai
 */
class Lang
{
	private Lang()
	{
		throw new AssertionError(IDAndEntities.YOU_SHALL_NOT_ACCESS);
	}

	private static final Map<DiscordLocale, String> langDescriptions = new HashMap<>();

	static
	{
		langDescriptions.put(CHINESE_TAIWAN, "切換語言");
		langDescriptions.put(CHINESE_CHINA, "切换语言");
	}

	private static final OptionData langOptions =
			new OptionData(OptionType.STRING, "lang_name", "The language you want to switch to", true, false)
					.setNameLocalization(CHINESE_TAIWAN, "語言名字")
					.setNameLocalization(CHINESE_CHINA, "语言名字")
					.setDescriptionLocalization(CHINESE_TAIWAN, "要切換的語言")
					.setDescriptionLocalization(CHINESE_CHINA, "要切换的语言")
					.addChoice("English", IDAndEntities.Languages.ENGLISH)
					.addChoice("台灣正體", IDAndEntities.Languages.TW_MANDARIN)
					.addChoice("台語文字", IDAndEntities.Languages.TAIWANESE)
					.addChoice("粵語漢字", IDAndEntities.Languages.CANTONESE)
					.addChoice("简体中文", IDAndEntities.Languages.CHINESE);

	static final SlashCommandData lang = Commands.slash("lang", "Change language")
			.setDescriptionLocalizations(langDescriptions)
			.addOptions(langOptions);

	static final SlashCommandData language = Commands.slash("language", "Change language")
			.setDescriptionLocalizations(langDescriptions)
			.addOptions(langOptions);
}

class Jira
{
	private Jira()
	{
		throw new AssertionError(IDAndEntities.YOU_SHALL_NOT_ACCESS);
	}

	private static final Map<DiscordLocale, String> jiraDescriptions = new HashMap<>();

	static
	{
		jiraDescriptions.put(CHINESE_TAIWAN, "Minecraft漏洞");
		jiraDescriptions.put(CHINESE_CHINA, "Minecraft漏洞");
	}

	private static final OptionData jiraOptions =
			new OptionData(OptionType.STRING, "bug_link", "Link of the bug", true, false)
					.setNameLocalization(CHINESE_TAIWAN, "漏洞連結")
					.setNameLocalization(CHINESE_CHINA, "漏洞链接");

	static final SlashCommandData jira = Commands.slash("jira", "Minecraft bug")
			.setDescriptionLocalizations(jiraDescriptions)
			.addOptions(jiraOptions);

	static final SlashCommandData bug = Commands.slash("bug", "Minecraft bug")
			.setDescriptionLocalizations(jiraDescriptions)
			.addOptions(jiraOptions);
}