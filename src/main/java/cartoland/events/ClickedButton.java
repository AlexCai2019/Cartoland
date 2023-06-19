package cartoland.events;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class ClickedButton extends ListenerAdapter
{
	public static final String ARCHIVE_THREAD = "archive_thread";
	public static final String RENAME_THREAD = "rename_thread";

	private final TextInput.Builder newTitleInputBuilder = TextInput.create("new_title", "New Title", TextInputStyle.SHORT)
			.setRequiredRange(0, 100);

	@Override
	public void onButtonInteraction(ButtonInteractionEvent event)
	{
		switch (event.getComponentId())
		{
			case ARCHIVE_THREAD ->
			{
				ThreadChannel channel = event.getChannel().asThreadChannel();
				if (channel.isArchived())
				{
					event.reply("This thread is already archived!").setEphemeral(true).queue();
					return;
				}
				Member member = event.getMember();
				if (member == null || (!member.hasPermission(Permission.MANAGE_THREADS) && member.getIdLong() != channel.getOwnerIdLong()))
				{
					event.reply("You don't have the permission to archive this thread!").setEphemeral(true).queue();
					return;
				}
				event.reply(member.getEffectiveName() + " archived this thread.")
						.queue(interactionHook -> channel.getManager().setArchived(true).queue());
			}

			case RENAME_THREAD ->
			{
				newTitleInputBuilder.setValue(event.getChannel().getName());
				event.replyModal(
						Modal.create("new_title", "Set New Thread Title")
								.addComponents(ActionRow.of(newTitleInputBuilder.build()))
								.build()).queue();
			}
		}
	}
}