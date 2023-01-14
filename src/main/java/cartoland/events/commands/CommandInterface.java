package cartoland.events.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface CommandInterface
{
    void commandProcess(SlashCommandInteractionEvent event); //指令
}