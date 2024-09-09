package cartoland.utilities;

import cartoland.commands.*;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.*;

import java.util.Map;

import static cartoland.commands.ICommand.*;
import static cartoland.events.ContextMenu.*;
import static net.dv8tion.jda.api.interactions.DiscordLocale.*;

/**
 * {@code AddCommands} is a utility that holds every command. The only purpose and usage is for
 * {@link cartoland.Cartoland#main(String[])} to get all commands when registering JDA. Can not be instantiated or
 * inherited.
 *
 * @since 1.5
 * @see cartoland.Cartoland
 * @author Alex Cai
 */
public final class AddCommands
{
	private AddCommands()
	{
		throw new AssertionError(IDs.YOU_SHALL_NOT_ACCESS);
	}

	public static CommandData[] commands()
	{
		//這裡真的很亂
		//沒有十足的信心 不要編輯這裡的程式碼

		//cmd指令及其變種的說明和選項名稱
		Map<DiscordLocale, String> cmdDescriptions = Map.of(CHINESE_TAIWAN, "獲得Minecraft指令的協助", CHINESE_CHINA, "获得Minecraft命令的协助");

		OptionData cmdOption = new OptionData(OptionType.STRING, "cmd_name", "The name of a Minecraft command", false, true)
						.setNameLocalization(CHINESE_TAIWAN, "指令名字")
						.setNameLocalization(CHINESE_CHINA, "命令名字")
						.setDescriptionLocalization(CHINESE_TAIWAN, "Minecraft指令的名字")
						.setDescriptionLocalization(CHINESE_CHINA, "Minecraft命令的名字");

		//faq指令及其變種的說明和選項名稱
		Map<DiscordLocale, String> faqDescriptions = Map.of(CHINESE_TAIWAN, "獲得地圖製作或遊戲資訊的協助", CHINESE_CHINA, "获得地图制作或游戏资讯的协助");

		OptionData faqOption = new OptionData(OptionType.STRING, "faq_name", "A question about map making or game information.", false, true)
						.setNameLocalization(CHINESE_TAIWAN, "問題")
						.setNameLocalization(CHINESE_CHINA, "问题")
						.setDescriptionLocalization(CHINESE_TAIWAN, "地圖製作或遊戲資訊的問題")
						.setDescriptionLocalization(CHINESE_CHINA, "地图制作或游戏资讯的问题");

		//dtp指令及其變種的說明和選項名稱
		Map<DiscordLocale, String> dtpDescriptions = Map.of(CHINESE_TAIWAN, "獲得Minecraft資料包的協助", CHINESE_CHINA, "获得Minecraft数据包的协助");

		OptionData dtpOption = new OptionData(OptionType.STRING, "dtp_name", "Minecraft datapack features", false, true)
						.setNameLocalization(CHINESE_TAIWAN, "資料包功能")
						.setNameLocalization(CHINESE_CHINA, "数据包功能")
						.setDescriptionLocalization(CHINESE_TAIWAN, "Minecraft資料包的功能")
						.setDescriptionLocalization(CHINESE_CHINA, "Minecraft数据包的功能");

		//lang指令及其變種的說明和選項名稱
		Map<DiscordLocale, String> langDescriptions = Map.of(CHINESE_TAIWAN, "切換語言", CHINESE_CHINA, "切换语言");

		OptionData langOptions = new OptionData(OptionType.STRING, "lang_name", "The language you want to switch to", true, false)
						.setNameLocalization(CHINESE_TAIWAN, "語言名字")
						.setNameLocalization(CHINESE_CHINA, "语言名字")
						.setDescriptionLocalization(CHINESE_TAIWAN, "要切換的語言")
						.setDescriptionLocalization(CHINESE_CHINA, "要切换的语言")
						.addChoice("English", Languages.ENGLISH)
						.addChoice("台灣正體", Languages.TW_MANDARIN)
						.addChoice("台語文字", Languages.TAIWANESE)
						.addChoice("粵語漢字", Languages.CANTONESE)
						.addChoice("简体中文", Languages.CHINESE)
						.addChoice("Español", Languages.ESPANOL)
						.addChoice("日本語", Languages.JAPANESE);

		//jira指令及其變種的說明和選項名稱
		Map<DiscordLocale, String> jiraDescriptions = Map.of(CHINESE_TAIWAN, "Minecraft漏洞", CHINESE_CHINA, "Minecraft漏洞");

		OptionData jiraOptions = new OptionData(OptionType.STRING, "bug_link", "Link of the bug", true, false)
						.setNameLocalization(CHINESE_TAIWAN, "漏洞連結")
						.setNameLocalization(CHINESE_CHINA, "漏洞链接");

		return new CommandData[]
		{
			Commands.slash(INVITE, "Get invite link of Cartoland")
					.setDescriptionLocalization(CHINESE_TAIWAN, "獲得伺服器的邀請連結")
					.setDescriptionLocalization(CHINESE_CHINA, "获得服务器的邀请链接")
					.addOptions(
							new OptionData(OptionType.STRING, "guild_name", "The guild", false, false)
								.setNameLocalization(CHINESE_TAIWAN, "伺服器")
								.setNameLocalization(CHINESE_CHINA, "服务器")
								.setDescriptionLocalization(CHINESE_TAIWAN, "伺服器")
								.setDescriptionLocalization(CHINESE_CHINA, "服务器")
								.addChoices(
										new Command.Choice("Cartoland", "cartoland"),
										new Command.Choice("Minecraft Commands", "minecraft_commands"),
										new Command.Choice("Spyglass", "spyglass"),
										new Command.Choice("Blockbench", "blockbench"))),

			Commands.slash(HELP, "Get help with bot commands")
					.setDescriptionLocalization(CHINESE_TAIWAN, "獲得機器人指令的介紹")
					.setDescriptionLocalization(CHINESE_CHINA, "获得机器人指令的介绍")
					.addOptions(
						new OptionData(OptionType.STRING, "help_name", "The command you want help with", false, false)
							.setDescriptionLocalization(CHINESE_TAIWAN, "想確認的指令")
							.setDescriptionLocalization(CHINESE_CHINA, "想确认的命令")),

			Commands.slash(CMD, "Get help with Minecraft commands")
					.setDescriptionLocalizations(cmdDescriptions)
					.addOptions(cmdOption),
			Commands.slash(MCC, "Get help with Minecraft commands")
					.setDescriptionLocalizations(cmdDescriptions)
					.addOptions(cmdOption),
			Commands.slash(COMMAND, "Get help with Minecraft commands")
					.setDescriptionLocalizations(cmdDescriptions)
					.addOptions(cmdOption),

			Commands.slash(FAQ, "Find answers to map making or game information questions")
					.setDescriptionLocalizations(faqDescriptions)
					.addOptions(faqOption),
			Commands.slash(QUESTION, "Find answers to map making or game information questions")
					.setDescriptionLocalizations(faqDescriptions)
					.addOptions(faqOption),

			Commands.slash(DTP, "Get help with Minecraft datapack features")
					.setDescriptionLocalizations(dtpDescriptions)
					.addOptions(dtpOption),
			Commands.slash(DATAPACK, "Get help with Minecraft datapack features")
					.setDescriptionLocalizations(dtpDescriptions)
					.addOptions(dtpOption),

			Commands.slash(LANG, "Change language")
					.setDescriptionLocalizations(langDescriptions)
					.addOptions(langOptions),
			Commands.slash(LANGUAGE, "Change language")
					.setDescriptionLocalizations(langDescriptions)
					.addOptions(langOptions),

			Commands.slash(JIRA, "Minecraft bug")
					.setDescriptionLocalizations(jiraDescriptions)
					.addOptions(jiraOptions),
			Commands.slash(BUG, "Minecraft bug")
					.setDescriptionLocalizations(jiraDescriptions)
					.addOptions(jiraOptions),

			Commands.slash(TOOL, "Various helpful utilities")
					.setDescriptionLocalization(CHINESE_TAIWAN, "能協助你的工具")
					.setDescriptionLocalization(CHINESE_CHINA, "能协助你的工具")
					.addSubcommands(
						new SubcommandData(ToolCommand.UUID_STRING, "Get UUID data from raw UUID string")
							.setDescriptionLocalization(CHINESE_TAIWAN, "從UUID字串獲得UUID資料")
							.setDescriptionLocalization(CHINESE_CHINA, "从UUID字符串获得UUID数据")
							.addOption(OptionType.STRING, "raw_uuid", "The raw UUID that you want to convert", true, false),
						new SubcommandData(ToolCommand.UUID_ARRAY, "Get UUID data from UUID integer array")
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
						new SubcommandData(ToolCommand.COLOR_RGBA, "Get color data from RGBA array")
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
								new OptionData(OptionType.INTEGER, "alpha", "Alpha (0 ~ 255)", false, false)
									.setNameLocalization(CHINESE_TAIWAN, "不透明度")
									.setNameLocalization(CHINESE_CHINA, "不透明度")
									.setDescriptionLocalization(CHINESE_TAIWAN, "不透明度 (0 ~ 255)")
									.setDescriptionLocalization(CHINESE_CHINA, "不透明度 (0 ~ 255)")),
						new SubcommandData(ToolCommand.COLOR_INTEGER, "Get color data from RGB integer")
							.setDescriptionLocalization(CHINESE_TAIWAN, "從RGBA整數獲得顏色資料")
							.setDescriptionLocalization(CHINESE_CHINA, "从RGBA整数获得颜色数据")
							.addOptions(
								new OptionData(OptionType.STRING, "rgba_or_argb", "RGBA integer, must be decimal or hexadecimal, the order can be RGBA or ARGB", true, false)
									.setDescriptionLocalization(CHINESE_TAIWAN, "RGBA整數，必須是十進位或十六進位，順序可以是RGBA或ARGB")
									.setDescriptionLocalization(CHINESE_CHINA, "RGBA整数，必须是十进制或十六进制，顺序可以是RGBA或ARGB"))),

			Commands.slash(QUOTE, "Display content from a message link")
					.setDescriptionLocalization(CHINESE_TAIWAN, "顯示一個訊息連結的內容")
					.setDescriptionLocalization(CHINESE_CHINA, "显示一个信息链接的内容")
					.addOptions(
						new OptionData(OptionType.STRING, "link", "The link to the message", true, false)
							.setNameLocalization(CHINESE_TAIWAN, "連結")
							.setNameLocalization(CHINESE_CHINA, "链接")
							.setDescriptionLocalization(CHINESE_TAIWAN, "訊息的連結")
							.setDescriptionLocalization(CHINESE_CHINA, "信息的链接"),
						new OptionData(OptionType.BOOLEAN, "mention_author", "Mention the message author or not", false, false)
							.setNameLocalization(CHINESE_TAIWAN, "提及訊息作者")
							.setNameLocalization(CHINESE_CHINA, "提及信息作者")
							.setDescriptionLocalization(CHINESE_TAIWAN, "是否要提及訊息作者")
							.setDescriptionLocalization(CHINESE_CHINA, "是否要提及信息作者")),

			Commands.slash(YOUTUBER, "Send a link of a YouTube video creator channel")
					.setDescriptionLocalization(CHINESE_TAIWAN, "傳送一個YouTube影片創作者的頻道連結")
					.setDescriptionLocalization(CHINESE_CHINA, "发送一个YouTube视频博主的频道链接")
					.addOptions(
						new OptionData(OptionType.STRING, "youtuber_name", "The name of the YouTuber", true, true)
							.setNameLocalization(CHINESE_TAIWAN, "名字")
							.setNameLocalization(CHINESE_CHINA, "名字")
							.setDescriptionLocalization(CHINESE_TAIWAN, "YouTuber的名字")
							.setDescriptionLocalization(CHINESE_CHINA, "YouTuber的名字")),

			Commands.slash(INTRODUCE, "Introduce a user or update your introduction")
					.setDescriptionLocalization(CHINESE_TAIWAN, "獲取一名使用者的介紹，或更新你的自我介紹")
					.setDescriptionLocalization(CHINESE_CHINA, "获取一名用户的介绍，或更新你的自我介绍")
					.addSubcommands(
						new SubcommandData(IntroduceCommand.USER, "User")
							.setDescriptionLocalization(CHINESE_TAIWAN, "使用者")
							.setDescriptionLocalization(CHINESE_CHINA, "用户")
							.addOptions(
								new OptionData(OptionType.USER, "user", "The user that you want to know", false, false)
									.setNameLocalization(CHINESE_TAIWAN, "使用者")
									.setNameLocalization(CHINESE_CHINA, "用户")
									.setDescriptionLocalization(CHINESE_TAIWAN, "你想認識的使用者")
									.setDescriptionLocalization(CHINESE_CHINA, "你想认识的用户")),
						new SubcommandData(IntroduceCommand.UPDATE, "Update your introduction")
							.setDescriptionLocalization(CHINESE_TAIWAN, "更新你的自我介紹")
							.setDescriptionLocalization(CHINESE_CHINA, "更新你的自我介绍")
							.addOptions(
								new OptionData(OptionType.STRING, "content", "Your self introduction", true, false)
									.setNameLocalization(CHINESE_TAIWAN, "內容")
									.setNameLocalization(CHINESE_CHINA, "内容")
									.setDescriptionLocalization(CHINESE_TAIWAN, "你的自我介紹")
									.setDescriptionLocalization(CHINESE_CHINA, "你的自我介绍")),
						new SubcommandData(IntroduceCommand.DELETE, "Delete your introduction")
							.setDescriptionLocalization(CHINESE_TAIWAN, "刪除你的自我介紹")
							.setDescriptionLocalization(CHINESE_CHINA, "删除你的自我介绍")),

			Commands.slash(BIRTHDAY, "Set your birthday to make the bot bless you")
					.setDescriptionLocalization(CHINESE_TAIWAN, "設定你的生日，好讓機器人可以祝福你")
					.setDescriptionLocalization(CHINESE_CHINA, "设置你的生日，好让机器人可以祝福你")
					.addSubcommands(
						new SubcommandData(BirthdayCommand.SET, "Set your birthday")
							.setDescriptionLocalization(CHINESE_TAIWAN, "設定你的生日")
							.setDescriptionLocalization(CHINESE_CHINA, "设置你的生日")
							.addOptions(
								new OptionData(OptionType.INTEGER, "month", "Your birth month", true, false)
									.setNameLocalization(CHINESE_TAIWAN, "月")
									.setNameLocalization(CHINESE_CHINA, "月")
									.setDescriptionLocalization(CHINESE_TAIWAN, "你的生日月份")
									.setDescriptionLocalization(CHINESE_CHINA, "你的生日月份")
									.addChoices(
										new Command.Choice("January", 1)
											.setNameLocalization(CHINESE_TAIWAN, "一月")
											.setNameLocalization(CHINESE_CHINA, "一月"),
										new Command.Choice("February", 2)
											.setNameLocalization(CHINESE_TAIWAN, "二月")
											.setNameLocalization(CHINESE_CHINA, "二月"),
										new Command.Choice("March", 3)
											.setNameLocalization(CHINESE_TAIWAN, "三月")
											.setNameLocalization(CHINESE_CHINA, "三月"),
										new Command.Choice("April", 4)
											.setNameLocalization(CHINESE_TAIWAN, "四月")
											.setNameLocalization(CHINESE_CHINA, "四月"),
										new Command.Choice("May", 5)
											.setNameLocalization(CHINESE_TAIWAN, "五月")
											.setNameLocalization(CHINESE_CHINA, "五月"),
										new Command.Choice("June", 6)
											.setNameLocalization(CHINESE_TAIWAN, "六月")
											.setNameLocalization(CHINESE_CHINA, "六月"),
										new Command.Choice("July", 7)
											.setNameLocalization(CHINESE_TAIWAN, "七月")
											.setNameLocalization(CHINESE_CHINA, "七月"),
										new Command.Choice("August", 8)
											.setNameLocalization(CHINESE_TAIWAN, "八月")
											.setNameLocalization(CHINESE_CHINA, "八月"),
										new Command.Choice("September", 9)
											.setNameLocalization(CHINESE_TAIWAN, "九月")
											.setNameLocalization(CHINESE_CHINA, "九月"),
										new Command.Choice("October", 10)
											.setNameLocalization(CHINESE_TAIWAN, "十月")
											.setNameLocalization(CHINESE_CHINA, "十月"),
										new Command.Choice("November", 11)
											.setNameLocalization(CHINESE_TAIWAN, "十一月")
											.setNameLocalization(CHINESE_CHINA, "十一月"),
										new Command.Choice("December", 12)
											.setNameLocalization(CHINESE_TAIWAN, "十二月")
											.setNameLocalization(CHINESE_CHINA, "十二月")),
								new OptionData(OptionType.INTEGER, "date", "Your birthday", true, true)
									.setNameLocalization(CHINESE_TAIWAN, "日")
									.setNameLocalization(CHINESE_CHINA, "日")
									.setDescriptionLocalization(CHINESE_TAIWAN, "你的生日日期")
									.setDescriptionLocalization(CHINESE_CHINA, "你的生日日期")),
						new SubcommandData(BirthdayCommand.GET, "Get other user's birthday")
							.setDescriptionLocalization(CHINESE_TAIWAN, "獲取其他使用者們的生日")
							.setDescriptionLocalization(CHINESE_CHINA, "获取其他用户们的生日")
							.addOptions(
								new OptionData(OptionType.USER, "target", "The user that you want to check", false, false)
									.setNameLocalization(CHINESE_TAIWAN, "目標")
									.setNameLocalization(CHINESE_CHINA, "目标")
									.setDescriptionLocalization(CHINESE_TAIWAN, "想確認的使用者")
									.setDescriptionLocalization(CHINESE_CHINA, "想确认的用户")),
						new SubcommandData(BirthdayCommand.DELETE, "Delete your birthday setting")
							.setDescriptionLocalization(CHINESE_TAIWAN, "刪除你的生日設定")
							.setDescriptionLocalization(CHINESE_CHINA, "删除你的生日设置")),

			Commands.slash(ROLL, "Rolling")
					.setDescriptionLocalization(CHINESE_TAIWAN, "抽出一個結果")
					.setDescriptionLocalization(CHINESE_CHINA, "抽出一个结果")
					.addSubcommands(
						new SubcommandData(RollCommand.MEMBER, "Roll a member")
							.setDescriptionLocalization(CHINESE_TAIWAN, "抽出一名成員")
							.setDescriptionLocalization(CHINESE_CHINA, "抽出一名成员")
							.addOptions(
								new OptionData(OptionType.ROLE, "role", "Target members will need to have this role", false, false)
									.setNameLocalization(CHINESE_TAIWAN, "身分組")
									.setNameLocalization(CHINESE_CHINA, "身份组")
									.setDescriptionLocalization(CHINESE_TAIWAN, "目標成員需要有這個身分組")
									.setDescriptionLocalization(CHINESE_CHINA, "目标成员需要有这个身份组")),
						new SubcommandData(RollCommand.NUMBER, "Roll a number")
							.setDescriptionLocalization(CHINESE_TAIWAN, "抽出一個數字")
							.setDescriptionLocalization(CHINESE_CHINA, "抽出一个数字")
							.addOptions(
								new OptionData(OptionType.INTEGER, "minimum", "The minimum of the range", true, false)
									.setNameLocalization(CHINESE_TAIWAN, "最小值")
									.setNameLocalization(CHINESE_CHINA, "最小值")
									.setDescriptionLocalization(CHINESE_TAIWAN, "區間的最小值")
									.setDescriptionLocalization(CHINESE_CHINA, "区间的最小值"),
								new OptionData(OptionType.INTEGER, "maximum", "The maximum of the range", true, false)
									.setNameLocalization(CHINESE_TAIWAN, "最大值")
									.setNameLocalization(CHINESE_CHINA, "最大值")
									.setDescriptionLocalization(CHINESE_TAIWAN, "區間的最大值")
									.setDescriptionLocalization(CHINESE_CHINA, "区间的最大值"))),

			Commands.slash(MEGUMIN, "The best anime girl")
					.setDescriptionLocalization(CHINESE_TAIWAN, "最讚的動漫女孩")
					.setDescriptionLocalization(CHINESE_CHINA, "最赞的动漫女孩")
					.setDescriptionLocalization(JAPANESE, "最高のアニメの女の子"),

			Commands.slash(SHUTDOWN, "Use this to shut down the bot")
					.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
					.addOption(OptionType.BOOLEAN, "reboot", "Is for reboot", false, false),
			Commands.slash(RELOAD, "Reload all JSON files")
					.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
			Commands.slash(ADMIN, "Admin commands")
					.setDescriptionLocalization(CHINESE_TAIWAN, "管理員專用指令")
					.setDescriptionLocalization(CHINESE_CHINA, "管理员专用命令")
					.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.KICK_MEMBERS, Permission.BAN_MEMBERS, Permission.MODERATE_MEMBERS, Permission.MANAGE_CHANNEL))
					.setGuildOnly(true)
					.addSubcommands(
						new SubcommandData(AdminCommand.MUTE, "Mute a user")
							.setDescriptionLocalization(CHINESE_TAIWAN, "禁言一名使用者")
							.setDescriptionLocalization(CHINESE_CHINA, "禁言一名用户")
							.addOptions(
								new OptionData(OptionType.USER, "target", "The member that you want to mute", true, false)
									.setNameLocalization(CHINESE_TAIWAN, "目標")
									.setNameLocalization(CHINESE_CHINA, "目标")
									.setDescriptionLocalization(CHINESE_TAIWAN, "你想禁言的成員")
									.setDescriptionLocalization(CHINESE_CHINA, "你想禁言的成员"),
								new OptionData(OptionType.NUMBER, "duration", "Duration that the member is going to be muted", true, false)
									.setNameLocalization(CHINESE_TAIWAN, "時間")
									.setNameLocalization(CHINESE_CHINA, "时长")
									.setDescriptionLocalization(CHINESE_TAIWAN, "成員將被禁言的時間")
									.setDescriptionLocalization(CHINESE_CHINA, "成员将被禁言的时长"),
								new OptionData(OptionType.STRING, "unit", "The time unit of the duration", true, false)
									.setNameLocalization(CHINESE_TAIWAN, "單位")
									.setNameLocalization(CHINESE_CHINA, "单位")
									.setDescriptionLocalization(CHINESE_TAIWAN, "時間的單位")
									.setDescriptionLocalization(CHINESE_CHINA, "时长的单位")
									.addChoices(
										new Command.Choice("Millisecond", "millisecond")
											.setNameLocalization(CHINESE_TAIWAN, "毫秒")
											.setNameLocalization(CHINESE_CHINA, "毫秒"),
										new Command.Choice("Second", "second")
											.setNameLocalization(CHINESE_TAIWAN, "秒")
											.setNameLocalization(CHINESE_CHINA, "秒"),
										new Command.Choice("Minute", "minute")
											.setNameLocalization(CHINESE_TAIWAN, "分鐘")
											.setNameLocalization(CHINESE_CHINA, "分钟"),
										new Command.Choice("Quarter", "quarter")
											.setNameLocalization(CHINESE_TAIWAN, "刻")
											.setNameLocalization(CHINESE_CHINA, "刻"),
										new Command.Choice("Hour", "hour")
											.setNameLocalization(CHINESE_TAIWAN, "小時")
											.setNameLocalization(CHINESE_CHINA, "小时"),
										new Command.Choice("Double Hour", "double_hour")
											.setNameLocalization(CHINESE_TAIWAN, "時辰")
											.setNameLocalization(CHINESE_CHINA, "时辰"),
										new Command.Choice("Day", "day")
											.setNameLocalization(CHINESE_TAIWAN, "天")
											.setNameLocalization(CHINESE_CHINA, "天"),
										new Command.Choice("Week", "week")
											.setNameLocalization(CHINESE_TAIWAN, "星期")
											.setNameLocalization(CHINESE_CHINA, "星期")),
								new OptionData(OptionType.STRING, "reason", "Reason of mute", false, false)
									.setNameLocalization(CHINESE_TAIWAN, "理由")
									.setNameLocalization(CHINESE_CHINA, "理由")
									.setDescriptionLocalization(CHINESE_TAIWAN, "禁言的理由")
									.setDescriptionLocalization(CHINESE_CHINA, "禁言的理由")),
						new SubcommandData(AdminCommand.TEMP_BAN, "Temporary ban a member")
							.setDescriptionLocalization(CHINESE_TAIWAN, "暫時停權一名成員")
							.setDescriptionLocalization(CHINESE_CHINA, "暂时封锁一名成员")
							.addOptions(
								new OptionData(OptionType.USER, "target", "The member that you want to ban", true, false)
									.setNameLocalization(CHINESE_TAIWAN, "目標")
									.setNameLocalization(CHINESE_CHINA, "目标")
									.setDescriptionLocalization(CHINESE_TAIWAN, "你想停權的成員")
									.setDescriptionLocalization(CHINESE_CHINA, "你想封锁的成员"),
								new OptionData(OptionType.NUMBER, "duration", "Duration that the member is going to be banned", true, false)
									.setNameLocalization(CHINESE_TAIWAN, "時間")
									.setNameLocalization(CHINESE_CHINA, "时长")
									.setDescriptionLocalization(CHINESE_TAIWAN, "成員將被停權的時間")
									.setDescriptionLocalization(CHINESE_CHINA, "成员将被封锁的时长"),
								new OptionData(OptionType.STRING, "unit", "The time unit of the duration", true, false)
									.setNameLocalization(CHINESE_TAIWAN, "單位")
									.setNameLocalization(CHINESE_CHINA, "单位")
									.setDescriptionLocalization(CHINESE_TAIWAN, "時間的單位")
									.setDescriptionLocalization(CHINESE_CHINA, "时长的单位")
									.addChoices(
										new Command.Choice("Hour", "hour")
											.setNameLocalization(CHINESE_TAIWAN, "小時")
											.setNameLocalization(CHINESE_CHINA, "小时"),
										new Command.Choice("Double Hour", "double_hour")
											.setNameLocalization(CHINESE_TAIWAN, "時辰")
											.setNameLocalization(CHINESE_CHINA, "时辰"),
										new Command.Choice("Day", "day")
											.setNameLocalization(CHINESE_TAIWAN, "天")
											.setNameLocalization(CHINESE_CHINA, "天"),
										new Command.Choice("Week", "week")
											.setNameLocalization(CHINESE_TAIWAN, "星期")
											.setNameLocalization(CHINESE_CHINA, "星期"),
										new Command.Choice("Month", "month")
											.setNameLocalization(CHINESE_TAIWAN, "月")
											.setNameLocalization(CHINESE_CHINA, "月"),
										new Command.Choice("Season", "season")
											.setNameLocalization(CHINESE_TAIWAN, "季")
											.setNameLocalization(CHINESE_CHINA, "季"),
										new Command.Choice("Year", "year")
											.setNameLocalization(CHINESE_TAIWAN, "年")
											.setNameLocalization(CHINESE_CHINA, "年"),
										new Command.Choice("Decade", "decade")
											.setNameLocalization(CHINESE_TAIWAN, "年代")
											.setNameLocalization(CHINESE_CHINA, "年代"),
										new Command.Choice("Wood Rat", "wood_rat")
											.setNameLocalization(CHINESE_TAIWAN, "甲子")
											.setNameLocalization(CHINESE_CHINA, "甲子"),
										new Command.Choice("Century", "century")
											.setNameLocalization(CHINESE_TAIWAN, "世紀")
											.setNameLocalization(CHINESE_CHINA, "世纪"))),
						new SubcommandData(AdminCommand.SLOW_MODE, "Set the slow mode of a channel")
							.setDescriptionLocalization(CHINESE_TAIWAN, "設定頻道的慢速模式")
							.setDescriptionLocalization(CHINESE_CHINA, "设定频道的慢速模式")
							.addOptions(
								new OptionData(OptionType.CHANNEL, "channel", "The channel that you want to modify", true, false)
									.setNameLocalization(CHINESE_TAIWAN, "頻道")
									.setNameLocalization(CHINESE_CHINA, "频道")
									.setDescriptionLocalization(CHINESE_TAIWAN, "要修改的頻道")
									.setDescriptionLocalization(CHINESE_CHINA, "要修改的频道"),
								new OptionData(OptionType.NUMBER, "time", "The time that you want to delay", true, false)
									.setNameLocalization(CHINESE_TAIWAN, "時間")
									.setNameLocalization(CHINESE_CHINA, "时间")
									.setDescriptionLocalization(CHINESE_TAIWAN, "要延遲的時間")
									.setDescriptionLocalization(CHINESE_CHINA, "要延迟的时间"),
								new OptionData(OptionType.STRING, "unit", "The time unit of delay", true, false)
									.setNameLocalization(CHINESE_TAIWAN, "單位")
									.setNameLocalization(CHINESE_CHINA, "单位")
									.setDescriptionLocalization(CHINESE_TAIWAN, "時間的單位")
									.setDescriptionLocalization(CHINESE_CHINA, "时间的单位")
									.addChoices(
										new Command.Choice("Second", "second")
											.setNameLocalization(CHINESE_TAIWAN, "秒")
											.setNameLocalization(CHINESE_CHINA, "秒"),
										new Command.Choice("Minute", "minute")
											.setNameLocalization(CHINESE_TAIWAN, "分鐘")
											.setNameLocalization(CHINESE_CHINA, "分钟"),
										new Command.Choice("Quarter", "quarter")
											.setNameLocalization(CHINESE_TAIWAN, "刻")
											.setNameLocalization(CHINESE_CHINA, "刻"),
										new Command.Choice("Hour", "hour")
											.setNameLocalization(CHINESE_TAIWAN, "小時")
											.setNameLocalization(CHINESE_CHINA, "小时"),
										new Command.Choice("Double Hour", "double_hour")
											.setNameLocalization(CHINESE_TAIWAN, "時辰")
											.setNameLocalization(CHINESE_CHINA, "时辰")))),
			Commands.slash(SCHEDULE, "Schedule a message to be send to a channel")
					.setDescriptionLocalization(CHINESE_TAIWAN, "排程一則訊息發送至一個頻道")
					.setDescriptionLocalization(CHINESE_CHINA, "排程一则信息发送至一个频道")
					.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE))
					.setGuildOnly(true)
					.addSubcommands(
						new SubcommandData(ScheduleCommand.CREATE, "Create a scheduled message")
							.setDescriptionLocalization(CHINESE_TAIWAN, "建立一個訊息排程")
							.setDescriptionLocalization(CHINESE_CHINA, "建立一个信息排程")
							.addOptions(
								new OptionData(OptionType.INTEGER, "time", "The time to send message", true, false)
									.setNameLocalization(CHINESE_TAIWAN, "時間")
									.setNameLocalization(CHINESE_CHINA, "时间")
									.setDescriptionLocalization(CHINESE_TAIWAN, "要發送訊息的時間")
									.setDescriptionLocalization(CHINESE_CHINA, "要发送信息的时间")
									.addChoices(
										new Command.Choice("12 a.m.", 0L)
											.setNameLocalization(CHINESE_TAIWAN, "半夜十二點")
											.setNameLocalization(CHINESE_CHINA, "半夜十二点"),
										new Command.Choice("1 a.m.", 1L)
											.setNameLocalization(CHINESE_TAIWAN, "上午一點")
												.setNameLocalization(CHINESE_CHINA, "上午一点"),
										new Command.Choice("2 a.m.", 2L)
											.setNameLocalization(CHINESE_TAIWAN, "上午兩點")
												.setNameLocalization(CHINESE_CHINA, "上午两点"),
										new Command.Choice("3 a.m.", 3L)
											.setNameLocalization(CHINESE_TAIWAN, "上午三點")
												.setNameLocalization(CHINESE_CHINA, "上午三点"),
										new Command.Choice("4 a.m.", 4L)
											.setNameLocalization(CHINESE_TAIWAN, "上午四點")
												.setNameLocalization(CHINESE_CHINA, "上午四点"),
										new Command.Choice("5 a.m.", 5L)
											.setNameLocalization(CHINESE_TAIWAN, "上午五點")
												.setNameLocalization(CHINESE_CHINA, "上午五点"),
										new Command.Choice("6 a.m.", 6L)
											.setNameLocalization(CHINESE_TAIWAN, "上午六點")
												.setNameLocalization(CHINESE_CHINA, "上午六点"),
										new Command.Choice("7 a.m.", 7L)
											.setNameLocalization(CHINESE_TAIWAN, "上午七點")
											.setNameLocalization(CHINESE_CHINA, "上午七点"),
										new Command.Choice("8 a.m.", 8L)
											.setNameLocalization(CHINESE_TAIWAN, "上午八點")
											.setNameLocalization(CHINESE_CHINA, "上午八点"),
										new Command.Choice("9 a.m.", 9L)
											.setNameLocalization(CHINESE_TAIWAN, "上午九點")
											.setNameLocalization(CHINESE_CHINA, "上午九点"),
										new Command.Choice("10 a.m.", 10L)
											.setNameLocalization(CHINESE_TAIWAN, "上午十點")
											.setNameLocalization(CHINESE_CHINA, "上午十点"),
										new Command.Choice("11 a.m.", 11L)
											.setNameLocalization(CHINESE_TAIWAN, "上午十一點")
											.setNameLocalization(CHINESE_CHINA, "上午十一点"),
										new Command.Choice("12 p.m.", 12L)
											.setNameLocalization(CHINESE_TAIWAN, "中午十二點")
											.setNameLocalization(CHINESE_CHINA, "中午十二点"),
										new Command.Choice("1 p.m.", 13L)
											.setNameLocalization(CHINESE_TAIWAN, "下午一點")
											.setNameLocalization(CHINESE_CHINA, "下午一点"),
										new Command.Choice("2 p.m.", 14L)
											.setNameLocalization(CHINESE_TAIWAN, "下午兩點")
											.setNameLocalization(CHINESE_CHINA, "下午两点"),
										new Command.Choice("3 p.m.", 15L)
											.setNameLocalization(CHINESE_TAIWAN, "下午三點")
											.setNameLocalization(CHINESE_CHINA, "下午三点"),
										new Command.Choice("4 p.m.", 16L)
											.setNameLocalization(CHINESE_TAIWAN, "下午四點")
											.setNameLocalization(CHINESE_CHINA, "下午四点"),
										new Command.Choice("5 p.m.", 17L)
											.setNameLocalization(CHINESE_TAIWAN, "下午五點")
											.setNameLocalization(CHINESE_CHINA, "下午五点"),
										new Command.Choice("6 p.m.", 18L)
											.setNameLocalization(CHINESE_TAIWAN, "下午六點")
											.setNameLocalization(CHINESE_CHINA, "下午六点"),
										new Command.Choice("7 p.m.", 19L)
											.setNameLocalization(CHINESE_TAIWAN, "下午七點")
											.setNameLocalization(CHINESE_CHINA, "下午七点"),
										new Command.Choice("8 p.m.", 20L)
											.setNameLocalization(CHINESE_TAIWAN, "下午八點")
											.setNameLocalization(CHINESE_CHINA, "下午八点"),
										new Command.Choice("9 p.m.", 21L)
											.setNameLocalization(CHINESE_TAIWAN, "下午九點")
											.setNameLocalization(CHINESE_CHINA, "下午九点"),
										new Command.Choice("10 p.m.", 22L)
											.setNameLocalization(CHINESE_TAIWAN, "下午十點")
											.setNameLocalization(CHINESE_CHINA, "下午十点"),
										new Command.Choice("11 p.m.", 23L)
											.setNameLocalization(CHINESE_TAIWAN, "下午十一點")
											.setNameLocalization(CHINESE_CHINA, "下午十一点")),
								new OptionData(OptionType.CHANNEL, "channel", "The channel to send message", true, false)
									.setNameLocalization(CHINESE_TAIWAN, "頻道")
									.setNameLocalization(CHINESE_CHINA, "频道")
									.setDescriptionLocalization(CHINESE_TAIWAN, "要發送訊息的頻道")
									.setDescriptionLocalization(CHINESE_CHINA, "要发送信息的频道"),
								new OptionData(OptionType.STRING, "content", "The string to send", true, false)
									.setNameLocalization(CHINESE_TAIWAN, "內容")
									.setNameLocalization(CHINESE_CHINA, "内容")
									.setDescriptionLocalization(CHINESE_TAIWAN, "訊息的內容")
									.setDescriptionLocalization(CHINESE_CHINA, "信息的内容"),
								new OptionData(OptionType.BOOLEAN, "once", "Whether the message send is only once", false, false)
									.setNameLocalization(CHINESE_TAIWAN, "一次性")
									.setNameLocalization(CHINESE_CHINA, "一次性")
									.setDescriptionLocalization(CHINESE_TAIWAN, "訊息發送是否為一次性的")
									.setDescriptionLocalization(CHINESE_CHINA, "信息发送是否为一次性的")),
						new SubcommandData(ScheduleCommand.DELETE, "Delete a scheduled message")
							.setDescriptionLocalization(CHINESE_TAIWAN, "刪除一個訊息排程")
							.setDescriptionLocalization(CHINESE_CHINA, "删除一个信息排程")
							.addOptions(new OptionData(OptionType.STRING, "name", "The name of a scheduled event", true, true)
								.setNameLocalization(CHINESE_TAIWAN, "名字")
								.setNameLocalization(CHINESE_CHINA, "名字")
								.setDescriptionLocalization(CHINESE_TAIWAN, "訊息排程的名字")
								.setDescriptionLocalization(CHINESE_CHINA, "信息排程的名字")),
						new SubcommandData(ScheduleCommand.LIST, "List of scheduled messages")
							.setDescriptionLocalization(CHINESE_TAIWAN, "訊息排程清單")
							.setDescriptionLocalization(CHINESE_CHINA, "信息排程清单")),

			Commands.slash(ONE_A_TWO_B, "Play a game of 1A2B")
					.setDescriptionLocalization(CHINESE_TAIWAN, "玩一場1A2B遊戲")
					.setDescriptionLocalization(CHINESE_CHINA, "玩一场1A2B游戏")
					.addSubcommands(
						new SubcommandData(OneATwoBCommand.START, "Start a game of 1A2B")
							.setDescriptionLocalization(CHINESE_TAIWAN, "開始一場1A2B遊戲")
							.setDescriptionLocalization(CHINESE_CHINA, "开始一场1A2B游戏"),
						new SubcommandData(OneATwoBCommand.PLAY, "Play 1A2B")
							.setDescriptionLocalization(CHINESE_TAIWAN, "玩一場1A2B遊戲")
							.setDescriptionLocalization(CHINESE_CHINA, "玩一场1A2B游戏")
							.addOptions(
								new OptionData(OptionType.INTEGER, "answer", "The answer that you think", true, false)
									.setNameLocalization(CHINESE_TAIWAN, "答案")
									.setNameLocalization(CHINESE_CHINA, "答案")
									.setDescriptionLocalization(CHINESE_TAIWAN, "你認為的答案")
									.setDescriptionLocalization(CHINESE_CHINA, "你认为的答案")),
						new SubcommandData(OneATwoBCommand.GIVE_UP, "Give up the game")
							.setDescriptionLocalization(CHINESE_TAIWAN, "放棄遊戲")
							.setDescriptionLocalization(CHINESE_CHINA, "放弃游戏")),
			Commands.slash(LOTTERY, "Play the lottery game")
					.setDescriptionLocalization(CHINESE_TAIWAN, "抽獎")
					.setDescriptionLocalization(CHINESE_CHINA, "抽奖")
					.addSubcommands(
						new SubcommandData(LotteryCommand.GET, "Gets the amount of command blocks the user owns")
							.setDescriptionLocalization(CHINESE_TAIWAN, "獲得使用者擁有的指令方塊數量")
							.setDescriptionLocalization(CHINESE_CHINA, "获得用户拥有的命令方块数量")
							.addOptions(
								new OptionData(OptionType.USER, "target", "The user that you want to check", false, false)
									.setNameLocalization(CHINESE_TAIWAN, "目標")
									.setNameLocalization(CHINESE_CHINA, "目标")
									.setDescriptionLocalization(CHINESE_TAIWAN, "想確認的使用者")
									.setDescriptionLocalization(CHINESE_CHINA, "想确认的用户"),
								new OptionData(OptionType.BOOLEAN, "display_detail", "Whether the command shows bet records", false, false)
									.setNameLocalization(CHINESE_TAIWAN, "顯示細節")
									.setNameLocalization(CHINESE_CHINA, "显示细节")
									.setDescriptionLocalization(CHINESE_TAIWAN, "指令是否顯示賭注紀錄")
									.setDescriptionLocalization(CHINESE_CHINA, "命令是否显示赌注纪录")),
						new SubcommandData(LotteryCommand.BET, "Bet some command blocks")
							.setDescriptionLocalization(CHINESE_TAIWAN, "賭上一些指令方塊")
							.setDescriptionLocalization(CHINESE_CHINA, "赌上一些命令方块")
							.addOptions(
								new OptionData(OptionType.STRING, "bet", "The amount of your command blocks you want to bet", true, false)
									.setNameLocalization(CHINESE_TAIWAN, "賭注")
									.setNameLocalization(CHINESE_CHINA, "賭注")
									.setDescriptionLocalization(CHINESE_TAIWAN, "想賭上的數量")
									.setDescriptionLocalization(CHINESE_CHINA, "想赌上的数量")),
						new SubcommandData(LotteryCommand.RANKING, "Rank all users")
							.setDescriptionLocalization(CHINESE_TAIWAN, "獲得所有使用者們的排行")
							.setDescriptionLocalization(CHINESE_CHINA, "获得所有用户们的排行")
							.addOptions(
								new OptionData(OptionType.INTEGER, "page", "The page of the ranking list", false, false)
									.setNameLocalization(CHINESE_TAIWAN, "頁數")
									.setNameLocalization(CHINESE_CHINA, "页数")
									.setDescriptionLocalization(CHINESE_TAIWAN, "排名清單的頁數")
									.setDescriptionLocalization(CHINESE_CHINA, "排名清单的页数")),
						new SubcommandData(LotteryCommand.DAILY, "Daily rewards")
							.setDescriptionLocalization(CHINESE_TAIWAN, "每日獎勵")
							.setDescriptionLocalization(CHINESE_CHINA, "每日奖励"),
						new SubcommandData(LotteryCommand.SLOT, "Play the slot machine once")
							.setDescriptionLocalization(CHINESE_TAIWAN, "玩一次角子老虎機")
							.setDescriptionLocalization(CHINESE_CHINA, "玩一次角子老虎机")
							.addOptions(
								new OptionData(OptionType.STRING, "bet", "The amount of your command blocks you want to bet", true, false)
									.setNameLocalization(CHINESE_TAIWAN, "賭注")
									.setNameLocalization(CHINESE_CHINA, "賭注")
									.setDescriptionLocalization(CHINESE_TAIWAN, "想賭上的數量")
									.setDescriptionLocalization(CHINESE_CHINA, "想赌上的数量"))),
			Commands.slash(TRANSFER, "Transfer your command blocks")
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
			Commands.slash(TIC_TAC_TOE, "Play a game of Tic-Tac-Toe")
					.setDescriptionLocalization(CHINESE_TAIWAN, "玩一場井字遊戲")
					.setDescriptionLocalization(CHINESE_CHINA, "玩一场井字游戏")
					.addSubcommands(
						new SubcommandData(TicTacToeCommand.START, "Start a game of Tic-Tac-Toe")
							.setDescriptionLocalization(CHINESE_TAIWAN, "開始一場井字遊戲")
							.setDescriptionLocalization(CHINESE_CHINA, "开始一场井字游戏")
							.addOptions(
								new OptionData(OptionType.INTEGER, "difficulty", "Difficulty of the game", true, false)
									.setNameLocalization(CHINESE_TAIWAN, "難度")
									.setNameLocalization(CHINESE_CHINA, "难度")
									.setDescriptionLocalization(CHINESE_TAIWAN, "遊戲的難度")
									.setDescriptionLocalization(CHINESE_CHINA, "游戏的难度")
									.addChoices(
										new Command.Choice("Baby", 0)
											.setNameLocalization(CHINESE_TAIWAN, "寶寶")
											.setNameLocalization(CHINESE_CHINA, "宝宝"),
										new Command.Choice("Easy", 1)
											.setNameLocalization(CHINESE_TAIWAN, "簡單")
											.setNameLocalization(CHINESE_CHINA, "简单"),
										new Command.Choice("Normal", 2)
											.setNameLocalization(CHINESE_TAIWAN, "普通")
											.setNameLocalization(CHINESE_CHINA, "普通"),
										new Command.Choice("Hard", 3)
											.setNameLocalization(CHINESE_TAIWAN, "困難")
											.setNameLocalization(CHINESE_CHINA, "困难"),
										new Command.Choice("Hell", 4)
											.setNameLocalization(CHINESE_TAIWAN, "地獄")
											.setNameLocalization(CHINESE_CHINA, "地狱"))),
						new SubcommandData(TicTacToeCommand.PLAY, "Play a game of Tic-Tac-Toe")
							.setDescriptionLocalization(CHINESE_TAIWAN, "玩一場井字遊戲")
							.setDescriptionLocalization(CHINESE_CHINA, "玩一场井字游戏")
							.addOptions(
								new OptionData(OptionType.INTEGER, "row", "The row of the board", true, false)
									.setNameLocalization(CHINESE_TAIWAN, "橫列")
									.setNameLocalization(CHINESE_CHINA, "横行")
									.setDescriptionLocalization(CHINESE_TAIWAN, "棋盤上的橫列")
									.setDescriptionLocalization(CHINESE_CHINA, "棋盘上的横行"),
								new OptionData(OptionType.INTEGER, "column", "The column of the board", true, false)
									.setNameLocalization(CHINESE_TAIWAN, "直行")
									.setNameLocalization(CHINESE_CHINA, "直列")
									.setDescriptionLocalization(CHINESE_TAIWAN, "棋盤上的直行")
									.setDescriptionLocalization(CHINESE_CHINA, "棋盘上的直列")),
						new SubcommandData(TicTacToeCommand.BOARD, "Get the current board")
							.setDescriptionLocalization(CHINESE_TAIWAN, "獲得目前的棋盤")
							.setDescriptionLocalization(CHINESE_CHINA, "获得目前的棋盘")),
			Commands.slash(CONNECT_FOUR, "Play a game of Connect Four")
					.setDescriptionLocalization(CHINESE_TAIWAN, "玩一場四子棋")
					.setDescriptionLocalization(CHINESE_CHINA, "玩一场四子棋")
					.addSubcommands(
						new SubcommandData(ConnectFourCommand.START, "Start a game of Connect Four")
							.setDescriptionLocalization(CHINESE_TAIWAN, "開始一場四子棋")
							.setDescriptionLocalization(CHINESE_CHINA, "开始一场四子棋"),
						new SubcommandData(ConnectFourCommand.PLAY, "Play a game of Connect Four")
							.setDescriptionLocalization(CHINESE_TAIWAN, "玩一場四子棋")
							.setDescriptionLocalization(CHINESE_CHINA, "玩一场四子棋")
							.addOptions(
								new OptionData(OptionType.INTEGER, "column", "The column that is going to place piece", true, false)
									.setNameLocalization(CHINESE_TAIWAN, "直行")
									.setNameLocalization(CHINESE_CHINA, "直列")
									.setDescriptionLocalization(CHINESE_TAIWAN, "棋盤上的直行")
									.setDescriptionLocalization(CHINESE_CHINA, "棋盘上的直列")),
						new SubcommandData(ConnectFourCommand.BOARD, "Get the current board")
							.setDescriptionLocalization(CHINESE_TAIWAN, "獲得目前的棋盤")
							.setDescriptionLocalization(CHINESE_CHINA, "获得目前的棋盘"),
						new SubcommandData(LightOutCommand.GIVE_UP, "Give up the game")
							.setDescriptionLocalization(CHINESE_TAIWAN, "放棄遊戲")
							.setDescriptionLocalization(CHINESE_CHINA, "放弃游戏")),
			Commands.slash(LIGHT_OUT, "Play a game of Light Out")
					.setDescriptionLocalization(CHINESE_TAIWAN, "玩一場關燈遊戲")
					.setDescriptionLocalization(CHINESE_CHINA, "玩一场关灯游戏")
					.addSubcommands(
						new SubcommandData(LightOutCommand.START, "Start a game of Light Out")
							.setDescriptionLocalization(CHINESE_TAIWAN, "開始一場關燈遊戲")
							.setDescriptionLocalization(CHINESE_CHINA, "开始一场关灯游戏"),
						new SubcommandData(LightOutCommand.FLIP, "Flip a slot on board")
							.setDescriptionLocalization(CHINESE_TAIWAN, "切換棋盤上的一個格子")
							.setDescriptionLocalization(CHINESE_CHINA, "切换棋盘上的一个格子")
							.addOptions(
								new OptionData(OptionType.INTEGER, "row", "The row of the board", true, false)
									.setNameLocalization(CHINESE_TAIWAN, "橫列")
									.setNameLocalization(CHINESE_CHINA, "横行")
									.setDescriptionLocalization(CHINESE_TAIWAN, "棋盤上的橫列")
									.setDescriptionLocalization(CHINESE_CHINA, "棋盘上的横行"),
								new OptionData(OptionType.INTEGER, "column", "The column of the board", true, false)
									.setNameLocalization(CHINESE_TAIWAN, "直行")
									.setNameLocalization(CHINESE_CHINA, "直列")
									.setDescriptionLocalization(CHINESE_TAIWAN, "棋盤上的直行")
									.setDescriptionLocalization(CHINESE_CHINA, "棋盘上的直列")),
						new SubcommandData(LightOutCommand.BOARD, "Get the current board")
							.setDescriptionLocalization(CHINESE_TAIWAN, "獲得目前的棋盤")
							.setDescriptionLocalization(CHINESE_CHINA, "获得目前的棋盘"),
						new SubcommandData(LightOutCommand.GIVE_UP, "Give up the game")
							.setDescriptionLocalization(CHINESE_TAIWAN, "放棄遊戲")
							.setDescriptionLocalization(CHINESE_CHINA, "放弃游戏")),

			Commands.message(RAW_TEXT)
					.setNameLocalization(CHINESE_TAIWAN, "原始文字")
					.setNameLocalization(CHINESE_CHINA, "原始文本"),

			Commands.message(REACTIONS)
					.setNameLocalization(CHINESE_TAIWAN, "反應")
					.setNameLocalization(CHINESE_CHINA, "反应"),

			Commands.message(CODE_BLOCK)
					.setNameLocalization(CHINESE_TAIWAN, "程式碼區塊")
					.setNameLocalization(CHINESE_CHINA, "代码区块"),

			Commands.message(QUOTE_)
					.setNameLocalization(CHINESE_TAIWAN, "引用內容")
					.setNameLocalization(CHINESE_CHINA, "引用內容"),

			Commands.message(PIN)
					.setNameLocalization(CHINESE_TAIWAN, "釘選/解釘")
					.setNameLocalization(CHINESE_CHINA, "标注/移除")
		};
	}

	/*
	.setNameLocalization(CHINESE_TAIWAN, "")
	.setNameLocalization(CHINESE_CHINA, "")
	.setDescriptionLocalization(CHINESE_TAIWAN, "")
	.setDescriptionLocalization(CHINESE_CHINA, "")
	 */
}