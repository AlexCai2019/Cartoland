package cartoland.events;

import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@code AutoComplete} is a listener that triggers when a user is typing a command. This class was registered
 * in {@link cartoland.Cartoland#main}, with the build of JDA. It uses {@link #commands} to store every commands that
 * needs auto complete as keys, and {@link GenericComplete} instances as values.
 *
 * @since 1.5
 * @see GenericComplete
 * @author Alex Cai
 */
public class AutoComplete extends ListenerAdapter
{
	private final Map<String, GenericComplete> commands = new HashMap<>(); //指令們

	public AutoComplete()
	{
		GenericComplete alias;

		alias = new JsonBasedComplete("cmd");
		commands.put("cmd", alias);
		commands.put("mcc", alias);
		commands.put("command", alias);

		alias = new JsonBasedComplete("faq");
		commands.put("faq", alias);
		commands.put("question", alias);

		alias = new JsonBasedComplete("dtp");
		commands.put("dtp", alias);
		commands.put("datapack", alias);

		commands.put("youtuber", new YouTuberComplete());

		commands.put("introduce", new IntroduceComplete());
	}

	@Override
	public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event)
	{
		GenericComplete complete = commands.get(event.getName());
		if (complete != null)
			complete.completeProcess(event);
	}
}

/**
 * {@code GenericComplete} is a parent class that has subclasses that can process auto complete of typing a slash
 * command. The subclasses of this class will be initial in the constructor of {@link AutoComplete}, which is
 * {@link AutoComplete#AutoComplete()}.
 *
 * @since 1.5
 * @see AutoComplete
 * @author Alex Cai
 */
abstract class GenericComplete
{
	protected static final int CHOICES_LIMIT = 25;

	abstract void completeProcess(CommandAutoCompleteInteractionEvent event);
}

/**
 * {@code JsonBasedComplete} is a subclass of {@code GenericComplete}, which handles the auto complete of command
 * /cmd, /faq, /dtp and their alias. This class use {@link JsonHandle#commandList} to get this information.
 *
 * @since 1.6
 * @author Alex Cai
 */
class JsonBasedComplete extends GenericComplete
{
	private final String commandName;

	JsonBasedComplete(String commandName)
	{
		this.commandName = commandName;
	}

	@Override
	void completeProcess(CommandAutoCompleteInteractionEvent event)
	{
		AutoCompleteQuery focusedOption = event.getFocusedOption();
		if (!focusedOption.getName().equals(commandName + "_name"))
			return;

		String optionValue = focusedOption.getValue(); //獲取目前正在打的選項
		List<Command.Choice> choices = JsonHandle.commandList(commandName)
				.stream()
				.map(o -> (String) o)
				.filter(word -> word.startsWith(optionValue))
				.map(word -> new Command.Choice(word, word))
				.toList();

		event.replyChoices(choices.size() <= CHOICES_LIMIT ? choices : choices.subList(0, CHOICES_LIMIT)).queue();
	}
}

/**
 * {@code YouTuberComplete} is a subclass of {@code GenericComplete}, which handles the auto complete of command
 * /youtuber. This class use {@link #youtubers} to get every YouTubers and their channel ID.
 *
 * @since 1.6
 * @author Alex Cai
 */
class YouTuberComplete extends GenericComplete
{
	private final Map<String, String> youtubers = new HashMap<>();

	YouTuberComplete()
	{
		youtubers.put("Cloud Wolf", "@CloudWolfMinecraft");
		youtubers.put("天豹星雲", "@nebulirion");
		youtubers.put("惡靈oreki", "@oreki20");
		youtubers.put("收音機", "@radio0529");
		youtubers.put("SethBling", "@SethBling");
		youtubers.put("slicedlime", "@slicedlime");
		youtubers.put("Phoenix SC", "@PhoenixSC");
	}

	@Override
	void completeProcess(CommandAutoCompleteInteractionEvent event)
	{
		String optionValue = event.getFocusedOption().getValue();
		List<Command.Choice> choices = new ArrayList<>(CHOICES_LIMIT);
		youtubers.forEach((youtuberName, youtuberID) ->
		{
			if (youtuberName.contains(optionValue))
				choices.add(new Command.Choice(youtuberName, youtuberID));
		});

		event.replyChoices(choices.size() <= CHOICES_LIMIT ? choices : choices.subList(0, CHOICES_LIMIT)).queue();
	}
}

/**
 * {@code IntroduceComplete} is a subclass of {@code GenericComplete}, which handles the auto complete of command
 * /introduce. This class only provide /introduce update delete.
 *
 * @since 2.0
 * @author Alex Cai
 */
class IntroduceComplete extends GenericComplete
{
	private final List<Command.Choice> delete = new ArrayList<>(1);
	private final List<Command.Choice> empty = new ArrayList<>();

	IntroduceComplete()
	{
		delete.add(new Command.Choice("delete", "delete"));
	}

	@Override
	void completeProcess(CommandAutoCompleteInteractionEvent event)
	{
		String subCommandName = event.getSubcommandName();
		if (subCommandName == null || !subCommandName.equals("update"))
			event.replyChoices(empty).queue();
		else
			event.replyChoices("delete".startsWith(event.getFocusedOption().getValue()) ? delete : empty).queue();
	}
}