package cartoland.commands;

import cartoland.utilities.CommonFunctions;
import cartoland.utilities.JsonHandle;
import cartoland.utilities.TimerHandle;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class BirthdayCommand implements ICommand
{
	private final ICommand setSubCommand = new SetSubCommand();
	private final ICommand deleteSubCommand = event ->
	{
		long userID = event.getUser().getIdLong();
		TimerHandle.deleteBirthday(userID);
		event.reply(JsonHandle.getStringFromJsonKey(userID, "birthday.delete")).queue();
	};

	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		("set".equals(event.getSubcommandName()) ? setSubCommand : deleteSubCommand).commandProcess(event);
	}

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

			TimerHandle.setBirthday(event.getUser().getIdLong(), month, date);
			event.reply(JsonHandle.getStringFromJsonKey(userID, "birthday.set.result")
								.formatted(
										JsonHandle.getStringFromJsonKey(userID, "birthday.month_" + month),
										JsonHandle.getStringFromJsonKey(userID, "birthday.date_" + date))).queue();
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