package cartoland.events;

import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

/**
 * {@code ReceiveModal} is a listener that triggers when a user interact with a modal. This class was registered in
 * {@link cartoland.Cartoland#main(String[])}, with the build of JDA.
 *
 * @since 2.0
 * @author Alex Cai
 */
public class ReceiveModal extends ListenerAdapter
{
	public static final String NEW_TITLE_MODAL_ID = "new_title";
	public static final String NEW_TITLE_TEXT = "new_title";

	@Override
	public void onModalInteraction(ModalInteractionEvent event)
	{
		User user = event.getUser();
		long userID = user.getIdLong();
		if (!NEW_TITLE_MODAL_ID.equals(event.getModalId())) //不是要命名討論串
			return;

		ModalMapping newTitle = event.getValue(NEW_TITLE_TEXT);
		if (newTitle == null)
		{
			event.reply("Impossible, this is required!").setEphemeral(true).queue();
			return;
		}

		String newTitleString = newTitle.getAsString(); //新標題
		event.reply(JsonHandle.getString(userID, "rename_thread.changed", user.getEffectiveName(), newTitleString)).queue();
		event.getGuildChannel().getManager().setName(newTitleString).queue();
	}
}