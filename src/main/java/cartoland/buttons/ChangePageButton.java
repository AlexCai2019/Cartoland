package cartoland.buttons;

import cartoland.utilities.CommandBlocksHandle;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class ChangePageButton implements IButton
{
	@Override
	public void buttonProcess(ButtonInteractionEvent event)
	{
		String page = event.getComponentId().substring(CHANGE_PAGE_LENGTH);
		int inputPage = Integer.parseInt(page);

		Button previous = Button.primary(IButton.CHANGE_PAGE + (inputPage - 1), Emoji.fromUnicode("◀️"));
		Button next = Button.primary(IButton.CHANGE_PAGE + (inputPage + 1), Emoji.fromUnicode("▶️"));

		event.reply(CommandBlocksHandle.rankingString(event.getUser().getIdLong(), inputPage))
				.addComponents(ActionRow.of(previous, next))
				.queue();
	}
}