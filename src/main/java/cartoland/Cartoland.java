package cartoland;

import cartoland.events.BotOffline;
import cartoland.events.BotOnline;
import cartoland.events.ChannelMessage;
import cartoland.events.PrivateMessage;
import cartoland.events.commands.CommandUsage;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;

import static cartoland.utilities.IDAndEntities.jda;

public class Cartoland
{
	public static void main(String[] args) throws InterruptedException
	{
		if (args.length < 1)
			return;

		jda = JDABuilder.createDefault(args[0])
				.addEventListeners(new BotOnline(), //當機器人上線的時候
								   new BotOffline(), //當機器人下線的時候
								   new ChannelMessage(), //當有人在群組傳訊息
								   new PrivateMessage(), //當有人傳私訊給機器人
								   new CommandUsage()) //當有人使用指令
				.enableIntents(GatewayIntent.MESSAGE_CONTENT)
				.setActivity(Activity.playing("Use /help to check more information")) //正在玩
				.build();

		jda.updateCommands().addCommands(
				Commands.slash("invite", "Get invite link of Cartoland"),
				Commands.slash("help", "Get help of bot commands")
						.addOption(OptionType.STRING, "help_name", "The command that want check", false),
				Commands.slash("cmd", "Get help of Minecraft commands")
						.addOption(OptionType.STRING, "cmd_name", "The name of a Minecraft command", false),
				Commands.slash("faq", "Get help of map making")
						.addOption(OptionType.STRING, "faq_name", "The question of map making", false),
				Commands.slash("dtp", "Get help of Minecraft datapack")
						.addOption(OptionType.STRING, "dtp_name", "The feature of a datapack", false),
				Commands.slash("datapack", "Get help of Minecraft datapack")
						.addOption(OptionType.STRING, "dtp_name", "The feature of a datapack", false),
				Commands.slash("lang", "Change language or check current languages")
						.addOption(OptionType.STRING, "lang_name", "The language that user want to change", false),
				Commands.slash("language", "Change language or check current languages")
						.addOptions(new OptionData(OptionType.STRING, "lang_name", "The language that user want to change", false)
											.addChoice("English", "en")
											.addChoice("台灣正體", "tw")
											.addChoice("台語文字", "ta")
											.addChoice("粵語漢字", "hk")
											.addChoice("简体中文", "cn")),
				Commands.slash("megumin", "The best anime girl"),

				Commands.slash("shutdown", "Use this to shutdown the bot"),
				Commands.slash("whosyourdaddy", "Reveal this bot's dad"),

				Commands.slash("oneatwob", "Play 1A2B game")
						.addOption(OptionType.STRING, "answer", "The answer that you think", false)
		).queue();

		jda.awaitReady();
	}
}