package cartoland.buttons;

import cartoland.events.ReceiveModal;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class RenameThreadButton extends ShowcaseThreadButtons
{
	public RenameThreadButton()
	{
		super("rename_thread");
	}

	@Override
	public void hasPermission(ButtonInteractionEvent event, ThreadChannel channel, Member member)
	{
		long userID = member.getIdLong();
		TextInput newTitleInput = TextInput.create(ReceiveModal.NEW_TITLE_TEXT, JsonHandle.getString(userID, "rename_thread.new_title"), TextInputStyle.SHORT)
				.setRequiredRange(0, 100)
				.setValue(channel.getName())
				.build();
		event.replyModal(Modal.create(ReceiveModal.NEW_TITLE_MODAL_ID, JsonHandle.getString(userID, "rename_thread.set_new_thread_title"))
				.addComponents(ActionRow.of(newTitleInput))
				.build()).queue(); //如果Modal可以事先建好就好了
	}
}