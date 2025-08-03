package cartoland.contexts;

import cartoland.methods.IQuotable;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class QuoteContext implements IContext, IQuotable
{
	@Override
	public void contextProcess(MessageContextInteractionEvent event)
	{
		long userID = event.getUser().getIdLong();

		Message message = event.getTarget();
		event.replyEmbeds(quoteMessage(message))
			.addComponents(ActionRow.of(Button.link(message.getJumpUrl(), JsonHandle.getString(userID, "quote.jump_message"))))
			.queue();
	}
}