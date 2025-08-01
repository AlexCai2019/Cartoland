package cartoland.buttons;

import cartoland.modals.IModal;
import cartoland.modals.NewTitleModal;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public final class RenameThreadButton extends ShowcaseThreadButtons
{
	public RenameThreadButton()
	{
		super(RENAME_THREAD);
	}

	@Override
	public void authorizedOperation(ButtonInteractionEvent event, ThreadChannel channel)
	{
		long userID = event.getUser().getIdLong();
		TextInput newTitleInput = TextInput.create(NewTitleModal.NEW_TITLE_TEXT, JsonHandle.getString(userID, "rename_thread.new_title"), TextInputStyle.SHORT)
				.setRequiredRange(0, 100)
				.setValue(channel.getName())
				.build();
		event.replyModal(Modal.create(IModal.NEW_TITLE_MODAL_ID, JsonHandle.getString(userID, "rename_thread.set_new_thread_title"))
				.addComponents(ActionRow.of(newTitleInput))
				.build()).queue(); //如果Modal可以事先建好就好了
	}
}