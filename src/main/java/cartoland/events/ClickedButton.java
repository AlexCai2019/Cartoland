package cartoland.events;

import cartoland.buttons.*;
import cartoland.utilities.FileHandle;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;

import static cartoland.buttons.IButton.*;

/**
 * {@code ReceiveModal} is a listener that triggers when a user interact with a button. This class was registered in
 * {@link cartoland.Cartoland#main(String[])}, with the build of JDA.
 *
 * @since 2.0
 * @author Alex Cai
 */
public class ClickedButton extends ListenerAdapter
{
	private final Map<String, IButton> buttons = HashMap.newHashMap(3);

	public ClickedButton()
	{
		buttons.put(ARCHIVE_THREAD, new ArchiveThreadButton());
		buttons.put(DELETE_THREAD, new DeleteThreadButton());
		buttons.put(RENAME_THREAD, new RenameThreadButton());
	}

	@Override
	public void onButtonInteraction(ButtonInteractionEvent event)
	{
		String componentName = event.getComponentId();
		buttons.get(componentName).buttonProcess(event);
		User user = event.getUser();
		FileHandle.log(user.getName(), '(', user.getId(), ") [", componentName + ']');
	}
}