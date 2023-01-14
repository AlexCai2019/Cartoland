package cartoland.backend;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class AdminCommandHandle implements CommandHandleInterface
{
    @Override
    public String commandProcess(SlashCommandInteractionEvent event)
    {
        String commandName = event.getName();
        Member member = event.getMember();
        if (member == null)
            return null;
        String id = member.getId();
        String argument;
        System.out.println("Admin " + id + " used " + commandName);
        FileHandle.logIntoFile("Admin " + id + " used " + commandName);
        return null;
    }
}
