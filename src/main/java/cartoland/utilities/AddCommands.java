package cartoland.utilities;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.*;

import java.util.HashMap;

/**
 * @since 1.5
 * @author Alex Cai
 */
public class AddCommands
{
	private AddCommands() {}

	public static CommandData[] getCommands()
	{
		return new CommandData[]
		{
			Commands.slash("invite", "Get invite link of Cartoland")
					.setDescriptionLocalization(DiscordLocale.CHINESE_TAIWAN, "獲得創世聯邦的邀請連結")
					.setDescriptionLocalization(DiscordLocale.CHINESE_CHINA, "获得创世联邦的邀请连结"),

			Commands.slash("help", "Get help of bot commands")
					.addOptions(
							new OptionData(OptionType.STRING, "help_name", "The command that want check", false, false)
									.setDescriptionLocalization(DiscordLocale.CHINESE_TAIWAN, "想確認的指令")
									.setDescriptionLocalization(DiscordLocale.CHINESE_CHINA, "想确认的命令")
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
					.addSubcommands(
							new SubcommandData("uuid_string", "Get uuid data from raw uuid string")
									.addOption(OptionType.STRING, "raw_uuid", "The raw uuid that you want to convert", true, false),
							new SubcommandData("uuid_array", "Get uuid data from uuid integer array")
									.addOptions(
											new OptionData(OptionType.INTEGER, "0", "The [0] of the array", true, false)
													.setDescriptionLocalization(DiscordLocale.CHINESE_TAIWAN, "陣列的第[0]項")
													.setDescriptionLocalization(DiscordLocale.CHINESE_CHINA, "数组的第[0]项"),
											new OptionData(OptionType.INTEGER, "1", "The [1] of the array", true, false)
													.setDescriptionLocalization(DiscordLocale.CHINESE_TAIWAN, "陣列的第[1]項")
													.setDescriptionLocalization(DiscordLocale.CHINESE_CHINA, "数组的第[1]项"),
											new OptionData(OptionType.INTEGER, "2", "The [2] of the array", true, false)
													.setDescriptionLocalization(DiscordLocale.CHINESE_TAIWAN, "陣列的第[2]項")
													.setDescriptionLocalization(DiscordLocale.CHINESE_CHINA, "数组的第[2]项"),
											new OptionData(OptionType.INTEGER, "3", "The [3] of the array", true, false)
													.setDescriptionLocalization(DiscordLocale.CHINESE_TAIWAN, "陣列的第[3]項")
													.setDescriptionLocalization(DiscordLocale.CHINESE_CHINA, "数组的第[3]项")),
							new SubcommandData("pack_mcmeta", "Generate a pack.mcmeta")
									.addOptions(
											new OptionData(OptionType.STRING, "pack_type", "Data pack or Resource pack", true, false)
													.addChoice("Data pack", "d")
													.addChoice("Resource pack", "r"))),

			Lang.lang,
			Lang.language,

			Commands.slash("megumin", "The best anime girl")
					.setDescriptionLocalization(DiscordLocale.CHINESE_TAIWAN, "最讚的動漫女孩")
					.setDescriptionLocalization(DiscordLocale.CHINESE_CHINA, "最赞的动漫女孩")
					.setDescriptionLocalization(DiscordLocale.JAPANESE, "最高のアニメの女の子"),

			Commands.slash("shutdown", "Use this to shutdown the bot")
					.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
			Commands.slash("reload", "Reload all JSON files")
					.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),

			Commands.slash("one_a_two_b", "Play 1A2B game")
					.addOption(OptionType.STRING, "answer", "The answer that you think", false, false),
			Commands.slash("lottery", "Play a lottery")
					.addOption(OptionType.STRING, "bet", "The bet amount that you want to offer", false, false),

			Commands.message("Raw Text")
					.setNameLocalization(DiscordLocale.CHINESE_TAIWAN, "原始文字")
					.setNameLocalization(DiscordLocale.CHINESE_CHINA, "原始文本")
		};
	}
}

class Cmd
{
	private static final HashMap<DiscordLocale, String> cmdDescriptions = new HashMap<>();

	static
	{
		cmdDescriptions.put(DiscordLocale.CHINESE_TAIWAN, "獲得Minecraft指令的協助");
		cmdDescriptions.put(DiscordLocale.CHINESE_CHINA, "获得Minecraft命令的协助");
	}

	private static final OptionData cmdOption =
			new OptionData(OptionType.STRING, "cmd_name", "The name of a Minecraft command", false, true)
					.setDescriptionLocalization(DiscordLocale.CHINESE_TAIWAN, "Minecraft指令的名字")
					.setDescriptionLocalization(DiscordLocale.CHINESE_CHINA, "Minecraft命令的名字");

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

class Faq
{
	private static final HashMap<DiscordLocale, String> faqDescriptions = new HashMap<>();

	static
	{
		faqDescriptions.put(DiscordLocale.CHINESE_TAIWAN, "獲得地圖製作的協助");
		faqDescriptions.put(DiscordLocale.CHINESE_CHINA, "获得地图制作的协助");
	}

	private static final OptionData faqOption =
			new OptionData(OptionType.STRING, "faq_name", "The question of map making", false, true)
					.setDescriptionLocalization(DiscordLocale.CHINESE_TAIWAN, "地圖製作的問題")
					.setDescriptionLocalization(DiscordLocale.CHINESE_CHINA, "地图制作的问题");

	static final SlashCommandData faq = Commands.slash("faq", "Get help of map making")
			.setDescriptionLocalizations(faqDescriptions)
			.addOptions(faqOption);

	static final SlashCommandData question = Commands.slash("question", "Get help of map making")
			.setDescriptionLocalizations(faqDescriptions)
			.addOptions(faqOption);
}

class Dtp
{
	private static final HashMap<DiscordLocale, String> dtpDescriptions = new HashMap<>();

	static
	{
		dtpDescriptions.put(DiscordLocale.CHINESE_TAIWAN, "獲得Minecraft資料包的協助");
		dtpDescriptions.put(DiscordLocale.CHINESE_CHINA, "获得Minecraft数据包的协助");
	}

	private static final OptionData dtpOption =
			new OptionData(OptionType.STRING, "dtp_name", "The feature of a datapack", false, true)
					.setDescriptionLocalization(DiscordLocale.CHINESE_TAIWAN, "Minecraft資料包的功能")
					.setDescriptionLocalization(DiscordLocale.CHINESE_CHINA, "Minecraft数据包的功能");

	static final SlashCommandData dtp = Commands.slash("dtp", "Get help of Minecraft datapack")
			.setDescriptionLocalizations(dtpDescriptions)
			.addOptions(dtpOption);

	static final SlashCommandData datapack = Commands.slash("datapack", "Get help of Minecraft datapack")
			.setDescriptionLocalizations(dtpDescriptions)
			.addOptions(dtpOption);
}

class Lang
{
	private static final HashMap<DiscordLocale, String> langDescriptions = new HashMap<>();

	private static final OptionData langOptions =
			new OptionData(OptionType.STRING, "lang_name", "The language that user want to change", true, false)
					.setDescriptionLocalization(DiscordLocale.CHINESE_TAIWAN, "要切換的語言")
					.setDescriptionLocalization(DiscordLocale.CHINESE_CHINA, "要切换的语言")
					.addChoice("English", IDAndEntities.Languages.ENGLISH)
					.addChoice("台灣正體", IDAndEntities.Languages.TW_MANDARIN)
					.addChoice("台語文字", IDAndEntities.Languages.TAIWANESE)
					.addChoice("粵語漢字", IDAndEntities.Languages.CANTONESE)
					.addChoice("简体中文", IDAndEntities.Languages.CHINESE);

	static
	{
		langDescriptions.put(DiscordLocale.CHINESE_TAIWAN, "切換語言");
		langDescriptions.put(DiscordLocale.CHINESE_CHINA, "切换语言");
	}

	static final SlashCommandData lang = Commands.slash("lang", "Change language")
			.setDescriptionLocalizations(langDescriptions)
			.addOptions(langOptions);

	static final SlashCommandData language = Commands.slash("language", "Change language")
			.setDescriptionLocalizations(langDescriptions)
			.addOptions(langOptions);
}