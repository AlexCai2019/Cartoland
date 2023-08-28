package cartoland.events;

import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.*;

import static cartoland.commands.ICommand.*;

/**
 * {@code AutoComplete} is a listener that triggers when a user is typing a command. This class was registered
 * in {@link cartoland.Cartoland#main}, with the build of JDA. It uses {@link #commands} to store every commands that
 * needs auto complete as keys, and {@link GenericComplete} instances as values.
 *
 * @see GenericComplete
 * @since 1.5
 * @author Alex Cai
 */
public class AutoComplete extends ListenerAdapter
{
	private final Map<String, GenericComplete> commands = new HashMap<>(9); //指令們

	public AutoComplete()
	{
		GenericComplete alias;

		commands.put(HELP, new JsonBasedComplete(HELP));

		alias = new JsonBasedComplete(CMD);
		commands.put(CMD, alias);
		commands.put(MCC, alias);
		commands.put(COMMAND, alias);

		alias = new JsonBasedComplete(FAQ);
		commands.put(FAQ, alias);
		commands.put(QUESTION, alias);

		alias = new JsonBasedComplete(DTP);
		commands.put(DTP, alias);
		commands.put(DATAPACK, alias);

		//youtuber
		commands.put(YOUTUBER, new YouTuberComplete());
	}

	@Override
	public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event)
	{
		GenericComplete complete = commands.get(event.getName());
		if (complete != null)
			complete.completeProcess(event);
	}


	/**
	 * {@code GenericComplete} is a parent class that has subclasses that can process auto complete of typing a slash
	 * command. The subclasses of this class will be initial in the constructor of {@link AutoComplete}, which is
	 * {@link AutoComplete#AutoComplete()}.
	 *
	 * @see AutoComplete
	 * @since 1.5
	 * @author Alex Cai
	 */
	private static abstract class GenericComplete
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
	private static class JsonBasedComplete extends GenericComplete
	{
		private final String commandNameKey;
		private final List<String> commandList;

		JsonBasedComplete(String commandName)
		{
			commandNameKey = commandName + "_name";
			commandList = JsonHandle.commandList(commandName);
		}

		@Override
		void completeProcess(CommandAutoCompleteInteractionEvent event)
		{
			AutoCompleteQuery focusedOption = event.getFocusedOption();
			if (!focusedOption.getName().equals(commandNameKey))
				return;

			String optionValue = focusedOption.getValue(); //獲取目前正在打的選項
			event.replyChoices(
					commandList.stream() //將字串串流轉換為選項列表
							.limit(CHOICES_LIMIT)
							.filter(word -> word.startsWith(optionValue))
							.map(word -> new Command.Choice(word, word))
							.toList()).queue();
		}
	}

	/**
	 * {@code YouTuberComplete} is a subclass of {@code GenericComplete}, which handles the auto complete of command
	 * /youtuber. This class use {@link #youtubers} to get every YouTubers and their channel ID.
	 *
	 * @since 1.6
	 * @author Alex Cai
	 */
	private static class YouTuberComplete extends GenericComplete
	{
		private final Map<String, String> youtubers = new LinkedHashMap<>(8); //LinkedHashMap or TreeMap ?
		private final Set<Map.Entry<String, String>> youtubersEntries;

		YouTuberComplete()
		{
			//這是TreeMap的排序方式 若要新增YouTuber 必須寫一個小程式測試TreeMap會怎麼排序
			youtubers.put("Cloud Wolf", "@CloudWolfMinecraft");
			youtubers.put("Phoenix SC", "@PhoenixSC");
			youtubers.put("SethBling", "@SethBling");
			youtubers.put("kingbdogz", "@kingbdogz");
			youtubers.put("slicedlime", "@slicedlime");
			youtubers.put("天豹星雲", "@nebulirion");
			youtubers.put("惡靈oreki", "@oreki20");
			youtubers.put("收音機", "@radio0529");
			youtubersEntries = youtubers.entrySet();
		}

		@Override
		void completeProcess(CommandAutoCompleteInteractionEvent event)
		{
			String optionValue = event.getFocusedOption().getValue();
			List<Command.Choice> choices = new ArrayList<>();
			for (Map.Entry<String, String> entry : youtubersEntries)
			{
				String youtuberName = entry.getKey();
				if (youtuberName.contains(optionValue))
					choices.add(new Command.Choice(youtuberName, entry.getValue()));
			}

			event.replyChoices(choices.size() <= CHOICES_LIMIT ? choices : choices.subList(0, CHOICES_LIMIT)).queue();
		}
	}
}