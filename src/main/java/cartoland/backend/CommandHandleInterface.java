package cartoland.backend;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface CommandHandleInterface
{
    String commandProcess(SlashCommandInteractionEvent event);
}
