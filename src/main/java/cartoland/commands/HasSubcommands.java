package cartoland.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * {@code HasSubcommands} is a class that helps commands that have subcommands to handle with subcommands.
 * This class implements {@link ICommand} interface, which make all the subclasses doesn't need to implement
 * {@link #commandProcess(SlashCommandInteractionEvent)} method.
 *
 * @since 2.1
 * @author Alex Cai
 */
public class HasSubcommands implements ICommand
{
	protected final Map<String, ICommand> subcommands;

	public HasSubcommands(int initialCapacity)
	{
		subcommands = new HashMap<>(initialCapacity);
	}

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		subcommands.get(event.getSubcommandName()).commandProcess(event);
	}
}