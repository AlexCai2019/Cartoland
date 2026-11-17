package cartoland.buttons;

import cartoland.modals.IModal;
import cartoland.modals.NewTitleModal;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.modals.Modal;

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
		TextInput newTitleInput = TextInput.create(NewTitleModal.NEW_TITLE_TEXT, TextInputStyle.SHORT)
				.setRequiredRange(0, 100)
				.setValue(channel.getName())
				.build();
		Modal modal = Modal.create(IModal.NEW_TITLE_MODAL_ID, JsonHandle.getString(userID, "rename_thread.set_new_thread_title"))
				.addComponents(Label.of(JsonHandle.getString(userID, "rename_thread.new_title"), "", newTitleInput))
				.build();
		event.replyModal(modal).queue(); //如果Modal可以事先建好就好了
	}
}