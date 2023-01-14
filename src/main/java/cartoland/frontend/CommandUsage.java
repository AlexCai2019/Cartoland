package cartoland.frontend;

import cartoland.backend.CommandHandleInterface;
import cartoland.backend.EveryoneCommandHandle;
import cartoland.backend.AdminCommandHandle;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class CommandUsage extends ListenerAdapter
{
    EveryoneCommandHandle everyoneCommandHandle = new EveryoneCommandHandle();
    AdminCommandHandle adminCommandHandle = new AdminCommandHandle();

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event)
    {
        super.onSlashCommandInteraction(event);
        Member member = event.getMember();
        if (member == null || member.getUser().isBot() || event.getChannel().getIdLong() == Main.ZH_CHAT_CHANNEL_ID) //不是機器人 and 不要在主頻道用
            return;

        sendMessage(event, everyoneCommandHandle);
        if (member.hasPermission(Permission.ADMINISTRATOR))
            sendMessage(event, adminCommandHandle);
    }

    private void sendMessage(SlashCommandInteractionEvent event, CommandHandleInterface commandHandle)
    {
        String ret = commandHandle.commandProcess(event);
        if (ret != null)
            event.getChannel().sendMessage(ret).queue();
    }
}
