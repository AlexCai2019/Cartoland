package cartoland.modals;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

public interface IModal
{
	String NEW_TITLE_MODAL_ID = "new_title";
	String UPDATE_INTRODUCE_ID = "update_introduce";

	void modalProcess(ModalInteractionEvent event);
}