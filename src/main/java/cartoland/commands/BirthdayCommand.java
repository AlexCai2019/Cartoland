package cartoland.commands;

import cartoland.utilities.JsonHandle;
import cartoland.utilities.TimerHandle;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.time.LocalDate;

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
public class BirthdayCommand extends HasSubcommands
{
	public static final String SET = "set";
	public static final String GET = "get";
	public static final String DELETE = "delete";

	public BirthdayCommand()
	{
		super(3);
		subcommands.put(SET, new SetSubCommand());
		subcommands.put(GET, event ->
		{
			User user = event.getUser(); //自己
			long userID = user.getIdLong();
			User target = event.getOption("target", user, OptionMapping::getAsUser); //要查詢的使用者 如果沒有指定查詢的對象 預設是自己

			LocalDate birthday = TimerHandle.getBirthday(target.getIdLong());
			if (birthday == null) //查不到生日
				event.reply(JsonHandle.getString(userID, "birthday.get.no_set", target.getEffectiveName())).queue();
			else //查到生日
				event.reply(JsonHandle.getString(userID, "birthday.get.set_on", target.getEffectiveName(),
							JsonHandle.getString(userID, "birthday.month_" + birthday.getMonthValue()), //月
							JsonHandle.getString(userID, "birthday.date_" + birthday.getDayOfMonth()))) //日
						.queue();
		});
		subcommands.put(DELETE, event ->
		{
			long userID = event.getUser().getIdLong();
			TimerHandle.setBirthday(userID, 0, 0); //刪除自己的生日
			event.reply(JsonHandle.getString(userID, "birthday.delete")).queue();
		});
	}

	/**
	 * {@code SetSubCommand} is a class that handles one of the subcommands of {@code /birthday} command, which is
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
			long userID = event.getUser().getIdLong();

			int month = event.getOption("month", 1, OptionMapping::getAsInt); //月
			int date = event.getOption("date", 1, OptionMapping::getAsInt); //日
			if (month < 1 || month > 12) //月份不在1 ~ 12的區間
			{
				event.reply(JsonHandle.getString(userID, "birthday.set.wrong_month")).setEphemeral(true).queue();
				return;
			}

			if (date < 1 || date > 31) //日期不在1 ~ 31的區間
			{
				event.reply(JsonHandle.getString(userID, "birthday.set.wrong_date")).setEphemeral(true).queue();
				return;
			}

			if (switch (month) //如果日期對不上月份 例如2月30日、4月31日等
			{
				case 4, 6, 9, 11 -> date == 31; //小月不得超過30

				case 2 -> date > 29; //2月不得超過29

				default -> false; //因為前面已經檢查過31了 所以大月是不會錯的
			})
			{
				event.reply(JsonHandle.getString(userID, "birthday.set.wrong_date_in_month",
						JsonHandle.getString(userID, "birthday.month_" + month),
						JsonHandle.getString(userID, "birthday.date_" + date))).setEphemeral(true).queue();
				return;
			}

			event.reply(JsonHandle.getString(userID, "birthday.set.result",
					JsonHandle.getString(userID, "birthday.month_" + month),
					JsonHandle.getString(userID, "birthday.date_" + date))).queue();
			TimerHandle.setBirthday(userID, month, date);
		}
	}
}