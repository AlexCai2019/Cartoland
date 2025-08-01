package cartoland.events;

import cartoland.modals.*;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static cartoland.modals.IModal.*;

/**
 * {@code ReceiveModal} is a listener that triggers when a user interact with a modal. This class was registered in
 * {@link cartoland.Cartoland#main(String[])}, with the build of JDA.
 *
 * @since 2.0
 * @author Alex Cai
 */
public class ReceiveModal extends ListenerAdapter
{
	private static final Logger logger = LoggerFactory.getLogger(ReceiveModal.class);

	private final Map<String, IModal> modals = new HashMap<>();

	public ReceiveModal()
	{
		modals.put(NEW_TITLE_MODAL_ID, new NewTitleModal());
		modals.put(UPDATE_INTRODUCE_ID, new UpdateIntroduceModal());
	}

	@Override
	public void onModalInteraction(ModalInteractionEvent event)
	{
		String modalID = event.getModalId();
		modals.get(modalID).modalProcess(event);

		User user = event.getUser();
		logger.info("{}({}) modal {}", user.getName(), user.getId(), modalID); //log放最後 避免超過3秒限制
	}
}