package cartoland.utilities;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.*;
import net.dv8tion.jda.api.interactions.commands.build.*;

import static net.dv8tion.jda.api.interactions.DiscordLocale.*;

import java.util.HashMap;

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

	public static CommandData[] getCommands()
	{
		return new CommandData[]
		{
			Commands.slash("invite", "Get invite link of Cartoland")
					.setDescriptionLocalization(CHINESE_TAIWAN, "獲得創世聯邦的邀請連結")
					.setDescriptionLocalization(CHINESE_CHINA, "获得创世联邦的邀请连结"),

			Commands.slash("help", "Get help of bot commands")
					.addOptions(
							new OptionData(OptionType.STRING, "help_name", "The command that want check", false, false)
									.setDescriptionLocalization(CHINESE_TAIWAN, "想確認的指令")
									.setDescriptionLocalization(CHINESE_CHINA, "想确认的命令")
									.addChoice("cmd", "cmd")
									.addChoice("faq", "faq")
									.addChoice("dtp", "dtp")
									.addChoice("tool", "tool")
									.addChoice("lang", "lang")),

			Cmd.cmd,
			Cmd.mcc,
			Cmd.command,

			Faq.faq,
			Faq.question,

			Dtp.dtp,
			Dtp.datapack,

			Commands.slash("tool", "Tools that can help you")
					.setDescriptionLocalization(CHINESE_TAIWAN, "能協助你的工具")
					.setDescriptionLocalization(CHINESE_CHINA, "能协助你的工具")
					.addSubcommands(
							new SubcommandData("uuid_string", "Get uuid data from raw uuid string")
									.addOption(OptionType.STRING, "raw_uuid", "The raw uuid that you want to convert", true, false),
							new SubcommandData("uuid_array", "Get uuid data from uuid integer array")
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
							new SubcommandData("pack_mcmeta", "Generate a pack.mcmeta")
									.addOptions(
											new OptionData(OptionType.STRING, "pack_type", "Data pack or Resource pack", true, false)
													.addChoice("Data pack", "d")
													.addChoice("Resource pack", "r"))),

			Lang.lang,
			Lang.language,

			Commands.slash("megumin", "The best anime girl")
					.setDescriptionLocalization(CHINESE_TAIWAN, "最讚的動漫女孩")
					.setDescriptionLocalization(CHINESE_CHINA, "最赞的动漫女孩")
					.setDescriptionLocalization(JAPANESE, "最高のアニメの女の子"),

			Commands.slash("shutdown", "Use this to shutdown the bot")
					.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
			Commands.slash("reload", "Reload all JSON files")
					.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),

			Commands.slash("one_a_two_b", "Play 1A2B game")
					.setDescriptionLocalization(CHINESE_TAIWAN, "玩一場1A2B遊戲")
					.setDescriptionLocalization(CHINESE_CHINA, "玩一场1A2B游戏")
					.addOptions(
							new OptionData(OptionType.STRING, "answer", "The answer that you think", false, false)
									.setNameLocalization(CHINESE_TAIWAN, "答案")
									.setNameLocalization(CHINESE_CHINA, "答案")
									.setDescriptionLocalization(CHINESE_TAIWAN, "你認為的答案")
									.setDescriptionLocalization(CHINESE_CHINA, "你认为的答案")),
			Commands.slash("lottery", "Play a lottery")
					.setDescriptionLocalization(CHINESE_TAIWAN, "抽獎")
					.setDescriptionLocalization(CHINESE_CHINA, "抽奖")
					.addOptions(
							new OptionData(OptionType.STRING, "bet", "The amount of your command blocks that you want to bet", false, false)
									.setNameLocalization(CHINESE_TAIWAN, "賭注")
									.setNameLocalization(CHINESE_CHINA, "賭注")
									.setDescriptionLocalization(CHINESE_TAIWAN, "想賭上的數量")
									.setDescriptionLocalization(CHINESE_CHINA, "想赌上的数量")),
			Commands.slash("transfer", "Transfer your command blocks")
					.setDescriptionLocalization(CHINESE_TAIWAN, "轉帳你的指令方塊")
					.setDescriptionLocalization(CHINESE_CHINA, "转帐你的命令方块")
					.addOptions(
							new OptionData(OptionType.USER, "target", "The user that you want to transfer to", true, false)
									.setNameLocalization(CHINESE_TAIWAN, "目標")
									.setNameLocalization(CHINESE_CHINA, "目标")
									.setDescriptionLocalization(CHINESE_TAIWAN, "想轉帳的目標")
									.setDescriptionLocalization(CHINESE_CHINA, "想转帐的目标"),
							new OptionData(OptionType.STRING, "amount", "The amount of command blocks that you want to transfer", true, false)
									.setNameLocalization(CHINESE_TAIWAN, "數量")
									.setNameLocalization(CHINESE_CHINA, "数量")
									.setDescriptionLocalization(CHINESE_TAIWAN, "想轉帳的數量")
									.setDescriptionLocalization(CHINESE_CHINA, "想转帐的数量")),

				//TODO: finish cartoland.commands.MinesweeperCommand
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

	private static final HashMap<DiscordLocale, String> cmdDescriptions = new HashMap<>();

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

	static final SlashCommandData cmd = Commands.slash("cmd", "Get help of Minecraft commands")
			.setDescriptionLocalizations(cmdDescriptions)
			.addOptions(cmdOption);

	static final SlashCommandData mcc = Commands.slash("mcc", "Get help of Minecraft commands")
			.setDescriptionLocalizations(cmdDescriptions)
			.addOptions(cmdOption);

	static final SlashCommandData command = Commands.slash("command", "Get help of Minecraft commands")
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

	private static final HashMap<DiscordLocale, String> faqDescriptions = new HashMap<>();

	static
	{
		faqDescriptions.put(CHINESE_TAIWAN, "獲得地圖製作的協助");
		faqDescriptions.put(CHINESE_CHINA, "获得地图制作的协助");
	}

	private static final OptionData faqOption =
			new OptionData(OptionType.STRING, "faq_name", "The question of map making", false, true)
					.setNameLocalization(CHINESE_TAIWAN, "問題")
					.setNameLocalization(CHINESE_CHINA, "问题")
					.setDescriptionLocalization(CHINESE_TAIWAN, "地圖製作的問題")
					.setDescriptionLocalization(CHINESE_CHINA, "地图制作的问题");

	static final SlashCommandData faq = Commands.slash("faq", "Get help of map making")
			.setDescriptionLocalizations(faqDescriptions)
			.addOptions(faqOption);

	static final SlashCommandData question = Commands.slash("question", "Get help of map making")
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

	private static final HashMap<DiscordLocale, String> dtpDescriptions = new HashMap<>();

	static
	{
		dtpDescriptions.put(CHINESE_TAIWAN, "獲得Minecraft資料包的協助");
		dtpDescriptions.put(CHINESE_CHINA, "获得Minecraft数据包的协助");
	}

	private static final OptionData dtpOption =
			new OptionData(OptionType.STRING, "dtp_name", "The feature of a datapack", false, true)
					.setNameLocalization(CHINESE_TAIWAN, "資料包功能")
					.setNameLocalization(CHINESE_CHINA, "数据包功能")
					.setDescriptionLocalization(CHINESE_TAIWAN, "Minecraft資料包的功能")
					.setDescriptionLocalization(CHINESE_CHINA, "Minecraft数据包的功能");

	static final SlashCommandData dtp = Commands.slash("dtp", "Get help of Minecraft datapack")
			.setDescriptionLocalizations(dtpDescriptions)
			.addOptions(dtpOption);

	static final SlashCommandData datapack = Commands.slash("datapack", "Get help of Minecraft datapack")
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

	private static final HashMap<DiscordLocale, String> langDescriptions = new HashMap<>();

	static
	{
		langDescriptions.put(CHINESE_TAIWAN, "切換語言");
		langDescriptions.put(CHINESE_CHINA, "切换语言");
	}

	private static final OptionData langOptions =
			new OptionData(OptionType.STRING, "lang_name", "The language that user want to change", true, false)
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