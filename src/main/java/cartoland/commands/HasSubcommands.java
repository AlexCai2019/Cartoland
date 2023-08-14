package cartoland.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * {@code HasSubcommands} is a class that helps commands that have subcommands to handle with subcommands.
 * This class implements {@link ICommand} interface, which make all the subclasses doesn't need to implement
 * {@link #commandProcess(SlashCommandInteractionEvent)} method. Notice that some commands which only
 * have 2 subcommands don't extend this class, this is because they can use a single {@link String#equals} to
 * choose subcommand handle class.
 *
 * @since 2.1
 * @author Alex Cai
 */
public class HasSubcommands implements ICommand
{
	protected final Map<String, ICommand> subcommands; //子指令們

	public HasSubcommands(int initialCapacity)
	{
		subcommands = new HashMap<>(initialCapacity);
	}

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		subcommands.get(event.getSubcommandName()).commandProcess(event); //透過HashMap選擇子指令
	}
}