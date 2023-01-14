package cartoland.frontend;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main
{
    public static final long BOT_CHANNEL_ID = 891703579289718814L;
    public static final long ZH_CHAT_CHANNEL_ID = 886936474723950611L;

    public static void main(String[] args) throws InterruptedException
    {
        if (args.length < 1)
            return;

        JDA jda = JDABuilder.createDefault(args[0])
                .addEventListeners(new BotOnline())
                .addEventListeners(new ChannelMessage())
                //.addEventListeners(new PrivateMessage())
                .addEventListeners(new CommandUsage())
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setActivity(Activity.playing("Please use /help"))
                .build();

        jda.updateCommands().addCommands(
                Commands.slash("invite", "Get invite link of Cartoland"),
                Commands.slash("help","Get help of bot commands")
                        .addOption(OptionType.STRING, "helpName", "The command that want check", false),
                Commands.slash("cmd", "Get help of Minecraft commands")
                        .addOption(OptionType.STRING, "cmdName", "The name of a Minecraft command", false),
                Commands.slash("faq", "Get help of map making")
                        .addOption(OptionType.STRING,"faqName", "The question of map making", false),
                Commands.slash("dtp", "Get help of Minecraft datapack")
                        .addOption(OptionType.STRING,"dtpName", "The feature of a datapack", false),
                Commands.slash("datapack", "Get help of Minecraft datapack")
                        .addOption(OptionType.STRING,"dtpName", "The feature of a datapack", false),
                Commands.slash("lang", "Change language or check current languages")
                        .addOption(OptionType.INTEGER,"langName", "The language that user want to change", false),
                Commands.slash("language", "Change language or check current languages")
                        .addOption(OptionType.INTEGER,"langName", "The language that user want to change", false),

                Commands.slash("shutdown", "Use this to shutdown the bot")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
        ).queue();

        jda.awaitReady();
    }
}