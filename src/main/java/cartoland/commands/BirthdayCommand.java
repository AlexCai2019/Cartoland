package cartoland.commands;

import cartoland.utilities.CommonFunctions;
import cartoland.utilities.JsonHandle;
import cartoland.utilities.TimerHandle;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * {@code BirthdayCommand} is an execution when a user uses /birthday command. This class implements {@link ICommand}
 * interface, which is for the commands HashMap in {@link cartoland.events.CommandUsage}. This class doesn't
 * handle sub commands, but call other classes to deal with it. Thought this command has subcommands, it
 * doesn't extend {@link HasSubcommands} class, this is because this command only has 2 subcommands, which can
 * be determined using a single {@link String#equals(Object)} method.
 *
 * @since 2.1
 * @author Alex Cai
 */
public class BirthdayCommand implements ICommand
{
	private final ICommand setSubCommand = new SetSubCommand();
	private final ICommand deleteSubCommand = event ->
	{
		long userID = event.getUser().getIdLong();
		TimerHandle.deleteBirthday(userID);
		event.reply(JsonHandle.getStringFromJsonKey(userID, "birthday.delete")).queue();
	};

	/**
	 * The execution of a slash command. Unlike other commands that has sub commands, since this
	 * command only has 2 subcommands, it uses a single ternary operation instead of HashMap to call the
	 * class that handles the subcommand.
	 *
	 * @param event The event that carries information of the user and the command.
	 * @since 2.1
	 * @author Alex Cai
	 */
	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		("set".equals(event.getSubcommandName()) ? setSubCommand : deleteSubCommand).commandProcess(event);
	}

	/**
	 * {@code SetSubCommand} is a class that handles one of the sub commands of {@code /birthday} command, which is
	 * {@code /birthday set}.
	 *
	 * @since 2.1
	 * @author Alex Cai
	 */
	private static class SetSubCommand implements ICommand
	{
		@Override
		public void commandProcess(SlashCommandInteractionEvent event)
		{
			Integer monthBox = event.getOption("month", CommonFunctions.getAsInt);
			Integer dateBox = event.getOption("date", CommonFunctions.getAsInt);
			if (monthBox == null || dateBox == null)
			{
				event.reply("Impossible, this is required!").queue();
				return;
			}

			long userID = event.getUser().getIdLong();
			int month = monthBox, date = dateBox;
			if (month < 1 || month > 12)
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "birthday.set.wrong_month")).setEphemeral(true).queue();
				return;
			}

			if (date < 1 || date > 31)
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "birthday.set.wrong_date")).setEphemeral(true).queue();
				return;
			}

			if (isWrongDate(month, date))
			{
				event.reply(JsonHandle.getStringFromJsonKey(userID, "birthday.set.wrong_date_in_month")
									.formatted(
											JsonHandle.getStringFromJsonKey(userID, "birthday.month_" + month),
											JsonHandle.getStringFromJsonKey(userID, "birthday.date_" + date))).setEphemeral(true).queue();
				return;
			}

			event.reply(JsonHandle.getStringFromJsonKey(userID, "birthday.set.result")
								.formatted(
										JsonHandle.getStringFromJsonKey(userID, "birthday.month_" + month),
										JsonHandle.getStringFromJsonKey(userID, "birthday.date_" + date))).queue();
			TimerHandle.setBirthday(event.getUser().getIdLong(), month, date);
		}

		private boolean isWrongDate(int month, int date)
		{
			return switch (month)
			{
				case 4, 6, 9, 11 -> date > 30;

				case 2 -> date > 29;

				default -> false; //因為前面已經檢查過31了 所以大月是不會錯的
			};
		}
	}
}