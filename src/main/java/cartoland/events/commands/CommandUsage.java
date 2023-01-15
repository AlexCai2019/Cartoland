package cartoland.events.commands;

import cartoland.Cartoland;
import cartoland.utility.FileHandle;
import cartoland.utility.JsonHandle;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CommandUsage extends ListenerAdapter
{
    private final Map<String, CommandInterface> commands = new HashMap<>();
    private String userID;
    private String argument;

    public CommandUsage()
    {
        //初始化map 放入所有指令
        commands.put("invite", event->
                event.reply("https://discord.gg/UMYxwHyRNE").queue());
        commands.put("datapack", event ->
                event.reply(minecraftCommandRelated("dtp", event)).queue());
        commands.put("language", event ->
                event.reply(minecraftCommandRelated("lang", event)).queue());

        String[] askCommands = { "help", "cmd", "faq", "dtp", "lang" };
        for (String s : askCommands)
            commands.put(s, event ->
                    event.reply(minecraftCommandRelated(s, event)).queue());

        commands.put("megumin", event ->
                event.reply("https://twitter.com/" + FileHandle.readRandomMeguminUrl()).queue());

        commands.put("shutdown", event ->
        {
            event.reply("Shutting down...").queue();
            TextChannel channel = event.getJDA().getChannelById(TextChannel.class, Cartoland.BOT_CHANNEL_ID);
            if (channel != null)
                channel.sendMessage("Cartoland bot is now offline").queue();
            event.getJDA().shutdown();
        });
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event)
    {
        super.onSlashCommandInteraction(event);
        Member member = event.getMember();
        if (member == null || member.getUser().isBot() || event.getChannel().getIdLong() == Cartoland.ZH_CHAT_CHANNEL_ID) //不是機器人 and 不要在主頻道用
            return;

        userID = member.getId();
        commands.get(event.getName()).commandProcess(event);

        if (argument != null)
        {
            System.out.println(member.getUser().getName() + "(" + userID + ") used /" + event.getName() + " " + argument);
            FileHandle.logIntoFile(member.getUser().getName() + "(" + userID + ") used /" + event.getName() + " " + argument);
        }
        else
        {
            System.out.println(member.getUser().getName() + "(" + userID + ") used /" + event.getName());
            FileHandle.logIntoFile(member.getUser().getName() + "(" + userID + ") used /" + event.getName());
        }
    }

    String minecraftCommandRelated(String jsonKey, @NotNull SlashCommandInteractionEvent event)
    {
        argument = event.getOption(jsonKey + "_name", OptionMapping::getAsString); //獲得參數
        if (argument == null) //沒有參數
            return JsonHandle.command(userID, jsonKey);
        return JsonHandle.command(userID, jsonKey, argument);
    }
}
