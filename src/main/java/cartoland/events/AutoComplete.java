package cartoland.events;

import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@code AutoComplete} is a listener that triggers when a user is typing a command. This class was registered
 * in {@link cartoland.Cartoland#main}, with the build of JDA.
 *
 * @since 1.5
 * @author Alex Cai
 */
public class AutoComplete extends ListenerAdapter
{
	private final HashMap<String, Complete> commands = new HashMap<>();

	public AutoComplete()
	{
		Complete alias;

		alias = new Complete("cmd");
		commands.put("cmd", alias);
		commands.put("mcc", alias);
		commands.put("command", alias);

		alias = new Complete("faq");
		commands.put("faq", alias);
		commands.put("question", alias);

		alias = new Complete("dtp");
		commands.put("dtp", alias);
		commands.put("datapack", alias);
	}

	@Override
	public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event)
	{
		String commandName = event.getName();
		if (commands.containsKey(commandName))
			commands.get(commandName).completeProcess(event);
	}
}

/**
 * {@code Complete} is a class that process auto complete of typing a slash command.
 *
 * @since 1.5
 * @author Alex Cai
 */
record Complete(String commandName)
{
	public void completeProcess(CommandAutoCompleteInteractionEvent event)
	{
		if (event.getFocusedOption().getName().equals(commandName + "_name"))
		{
			List<Choice> choices = JsonHandle.commandList(commandName)
					.stream()
					.filter(word -> ((String) word).startsWith(event.getFocusedOption().getValue()))
					.map(word -> new Choice((String) word, (String) word))
					.collect(Collectors.toList());

			event.replyChoices((choices.size() <= 25) ? choices : choices.subList(0, 25)).queue();
		}
	}
}