package cartoland.events;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

public class ContextMenu extends ListenerAdapter
{
	@Override
	public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event)
	{
		if (event.getName().equals("Copy context"))
		{
			Toolkit.getDefaultToolkit().getSystemClipboard()
					.setContents(new StringSelection(event.getTarget().getContentRaw()), null);
			event.reply("Copied!").setEphemeral(true).queue();
		}
	}
}