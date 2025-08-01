package cartoland.modals;

import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

public class NewTitleModal implements IModal
{
	public static final String NEW_TITLE_TEXT = "new_title";

	@Override
	public void modalProcess(ModalInteractionEvent event)
	{
		ModalMapping newTitle = event.getValue(NEW_TITLE_TEXT);
		if (newTitle == null)
		{
			event.reply("Impossible, this is required!").setEphemeral(true).queue();
			return;
		}

		User user = event.getUser();
		String newTitleString = newTitle.getAsString(); //新標題

		event.reply(JsonHandle.getString(user.getIdLong(), "rename_thread.changed", user.getEffectiveName(), newTitleString)).queue();
		event.getGuildChannel().getManager().setName(newTitleString).queue();
	}
}