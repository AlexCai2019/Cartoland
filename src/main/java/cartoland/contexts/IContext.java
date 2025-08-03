package cartoland.contexts;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;

public interface IContext
{
	void contextProcess(MessageContextInteractionEvent event);
}