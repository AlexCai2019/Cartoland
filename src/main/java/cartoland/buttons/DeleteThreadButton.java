package cartoland.buttons;

import cartoland.events.ReceiveModal;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class DeleteThreadButton extends ShowcaseThreadButtons
{
	public DeleteThreadButton()
	{
		super(DELETE_THREAD);
	}

	@Override
	public void adminManage(ButtonInteractionEvent event, ThreadChannel channel)
	{
		long userID = event.getUser().getIdLong();
		TextInput confirmDeletionInput = TextInput.create(ReceiveModal.CONFIRM_DELETION_TEXT, JsonHandle.getString(userID, "delete_thread.type_thread_name"), TextInputStyle.SHORT)
				.setRequiredRange(0, 100)
				.build();
		event.replyModal(Modal.create(ReceiveModal.CONFIRM_DELETION_MODAL_ID, JsonHandle.getString(userID, "delete_thread.confirm_deletion"))
				.addComponents(ActionRow.of(confirmDeletionInput))
				.build()).queue();
	}
}