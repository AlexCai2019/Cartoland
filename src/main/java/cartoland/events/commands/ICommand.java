package cartoland.events.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface ICommand
{
    void commandProcess(SlashCommandInteractionEvent event); //指令
}