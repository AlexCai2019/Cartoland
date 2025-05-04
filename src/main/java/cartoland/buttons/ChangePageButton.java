package cartoland.buttons;

import cartoland.utilities.CommandBlocksHandle;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class ChangePageButton implements IButton
{
	@Override
	public void buttonProcess(ButtonInteractionEvent event)
	{
		String page = event.getComponentId().substring(CHANGE_PAGE_LENGTH);
		int inputPage = Integer.parseInt(page);
		event.reply(CommandBlocksHandle.rankingString(event.getUser().getIdLong(), inputPage))
				.addActionRow(Button.primary(IButton.CHANGE_PAGE + (inputPage - 1), Emoji.fromUnicode("◀️")),
						Button.primary(IButton.CHANGE_PAGE + (inputPage + 1), Emoji.fromUnicode("▶️")))
				.queue();
	}
}