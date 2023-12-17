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
 * in {@link cartoland.Cartoland#main(String[])}, with the build of JDA. It uses {@link #commands} to store every commands that
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

		//birthday
		commands.put(BIRTHDAY, new BirthdayComplete());
	}

	@Override
	public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event)
	{
		GenericComplete complete = commands.get(event.getName());
		if (complete != null)
			complete.completeProcess(event);
	}

	/**
	 * {@code GenericComplete} is a parent class that has subclasses that can process auto complete of typing a
	 * slash command. The subclasses of this class will be initial in the constructor of {@link AutoComplete}, which is
	 * {@link AutoComplete#AutoComplete()}.
	 *
	 * @see AutoComplete
	 * @since 1.5
	 * @author Alex Cai
	 */
	private static abstract class GenericComplete
	{
		protected static final int CHOICES_LIMIT = 25; //最多只能有25個建議

		abstract void completeProcess(CommandAutoCompleteInteractionEvent event);
	}

	/**
	 * {@code JsonBasedComplete} is a subclass of {@code GenericComplete}, which handles the auto complete of command
	 * /cmd, /faq, /dtp and their alias. This class use {@link JsonHandle#commandList(String)} to get this information.
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
			if (!commandNameKey.equals(focusedOption.getName()))
				return;

			String optionValue = focusedOption.getValue(); //獲取目前正在打的選項
			event.replyChoices( //將字串串流轉換為選項列表
					commandList.stream()
							.filter(word -> word.startsWith(optionValue))
							.limit(CHOICES_LIMIT)
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
		//這是TreeMap的排序方式 若要新增YouTuber 必須寫一個小程式測試TreeMap會怎麼排序
		private final List<YouTuber> youtubers = Arrays.asList(
				new YouTuber("Cloud Wolf", "@CloudWolfMinecraft"),
				new YouTuber("Phoenix SC", "@PhoenixSC"),
				new YouTuber("SethBling", "@SethBling"),
				new YouTuber("kingbdogz", "@kingbdogz"),
				new YouTuber("slicedlime", "@slicedlime"),
				new YouTuber("天豹星雲", "@nebulirion"),
				new YouTuber("惡靈oreki", "@oreki20"),
				new YouTuber("收音機", "@radio0529"));

		@Override
		void completeProcess(CommandAutoCompleteInteractionEvent event)
		{
			String optionValue = event.getFocusedOption().getValue();
			event.replyChoices( //將字串串流轉換為選項列表
					youtubers.stream()
							.filter(youtuber -> youtuber.name.contains(optionValue))
							.limit(CHOICES_LIMIT)
							.map(youtuber -> new Command.Choice(youtuber.name, youtuber.ID))
							.toList()).queue();
		}

		/**
		 * An YouTuber.
		 *
		 * @param name The youtuber's channel name, such as "Alex Cai"
		 * @param ID The youtuber's channel ID, such as "@alexcai3002"
		 * @since 2.2
		 * @author Alex Cai
		 */
		private record YouTuber(String name, String ID) {}
	}

	/**
	 * {@code BirthdayComplete} is a subclass of {@code GenericComplete}, which handles the auto complete of command
	 * /birthday. This class helps users to enter the date of their birthdays.
	 *
	 * @since 2.2
	 * @author Alex Cai
	 */
	private static class BirthdayComplete extends GenericComplete
	{
		private final String[] dates =
		{
			"1","2","3","4","5","6","7","8","9","10",
			"11","12","13","14","15","16","17","18","19","20",
			"21","22","23","24","25","26","27","28","29","30","31"
		};
		private final List<Command.Choice> twentyFive;

		BirthdayComplete()
		{
			Command.Choice[] twentyFiveArray = new Command.Choice[CHOICES_LIMIT]; //1 ~ 25
			for (byte b = 0; b < CHOICES_LIMIT; b++)
				twentyFiveArray[b] = new Command.Choice(dates[b], b + 1L); //dates[0] ~ dates[24] 對應到1 ~ 25
			twentyFive = Arrays.asList(twentyFiveArray); //直接轉成固定大小的List 應該會比ArrayList快
		}

		@Override
		void completeProcess(CommandAutoCompleteInteractionEvent event)
		{
			AutoCompleteQuery focusedOption = event.getFocusedOption();
			if (!"date".equals(focusedOption.getName())) //必須要是date
				return;
			String optionValue = focusedOption.getValue();
			if (optionValue.isEmpty()) //代表沒有填值
			{
				event.replyChoices(twentyFive).queue(); //直接給出1 ~ 25
				return;
			}

			List<Command.Choice> choices = new ArrayList<>(); //選項
			int choicesCount = 0;
			for (byte b = 0; b < 31; b++) //或能用Arrays.stream 但new Choice的地方應該就要用parseLong了
			{
				if (!dates[b].contains(optionValue)) //如果日期字串內沒有包含
					continue; //下一個日期
				choices.add(new Command.Choice(dates[b], b + 1L)); //給予選項 dates[0] 對應到1 依此類推
				if (++choicesCount == CHOICES_LIMIT) //不得超過25個
					break;
			}
			event.replyChoices(choices).queue();
		}
	}
}