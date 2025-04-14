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
	private final Map<String, IButton> buttons = HashMap.newHashMap(4);

	public ClickedButton()
	{
		buttons.put(ARCHIVE_THREAD, new ArchiveThreadButton());
		buttons.put(DELETE_THREAD, event -> event.reply("This feature is no longer supported.").setEphemeral(true).queue());
		buttons.put(RENAME_THREAD, new RenameThreadButton());
		buttons.put(CHANGE_PAGE, new ChangePageButton());
	}

	@Override
	public void onButtonInteraction(ButtonInteractionEvent event)
	{
		String componentName = event.getComponentId();
		String buttonID = componentName.startsWith(CHANGE_PAGE) ? componentName.substring(0, CHANGE_PAGE_LENGTH) : componentName;
		buttons.get(buttonID).buttonProcess(event);

		User user = event.getUser();
		FileHandle.log(user.getName(), '(', user.getId(), ") [", componentName + ']');
	}
}