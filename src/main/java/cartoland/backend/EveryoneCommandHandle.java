package cartoland.backend;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class EveryoneCommandHandle implements CommandHandleInterface
{
    @Override
    public String commandProcess(SlashCommandInteractionEvent event)
    {
        String commandName = event.getName();
        Member member = event.getMember();
        if (member == null)
            return null;
        String id = member.getId();
        String jsonKey;
        String argument;
        System.out.println("User " + id + " used " + commandName + " " + event.getOptions());
        FileHandle.logIntoFile("User " + id + " used " + commandName + " " + event.getOptions());

        switch (commandName)
        {
            case "invite":
                return "https://discord.gg/UMYxwHyRNE";

            case "help":
            case "cmd":
            case "faq":
            case "dtp":
            case "lang":
                jsonKey = commandName;
                break;

            case "datapack":
                jsonKey = "dtp";
                break;
            case "language":
                jsonKey = "lang";
                break;

            default:
                return null;
        }

        JsonHandle.lastUse(id);
        argument = event.getOption(jsonKey + "Name", OptionMapping::getAsString);
        if (argument == null)
            return JsonHandle.command(jsonKey);
        return JsonHandle.command(jsonKey, argument);
    }
}
