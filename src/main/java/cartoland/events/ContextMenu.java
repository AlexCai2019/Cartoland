package cartoland.events;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * {@code ContextMenu} is a listener that triggers when a user uses right click command. This class was registered
 * in {@link cartoland.Cartoland#main}, with the build of JDA.
 *
 * @since 1.5
 * @author Alex Cai
 */
public class ContextMenu extends ListenerAdapter
{
	@Override
	public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event)
	{
		if (event.getName().equals("Raw Text"))
			event.reply("```\n" + event.getTarget().getContentRaw() + "```").setEphemeral(true).queue();
	}
}