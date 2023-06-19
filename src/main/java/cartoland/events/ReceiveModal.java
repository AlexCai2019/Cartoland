package cartoland.events;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.jetbrains.annotations.NotNull;

public class ReceiveModal extends ListenerAdapter
{
	@Override
	public void onModalInteraction(@NotNull ModalInteractionEvent event)
	{
		if (event.getModalId().equals("new_title"))
		{
			ThreadChannel channel = event.getChannel().asThreadChannel();
			Member member = event.getMember();
			if (member == null || (!member.hasPermission(Permission.MANAGE_THREADS) && member.getIdLong() != channel.getOwnerIdLong()))
			{
				event.reply("You don't have the permission to rename this thread!").setEphemeral(true).queue();
				return;
			}
			ModalMapping newTitle = event.getValue("new_title");
			if (newTitle != null)
			{
				String newTitleString = newTitle.getAsString();
				channel.getManager().setName(newTitleString).queue();
				event.reply(member.getEffectiveName() + " changed thread title to " + newTitleString + ".").queue();
			}
			else
				event.reply("Something went wrong...").setEphemeral(true).queue();
		}
	}
}