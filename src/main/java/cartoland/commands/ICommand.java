package cartoland.commands;

import cartoland.mini_games.MiniGame;
import cartoland.utilities.TimerHandle;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;

import java.util.HashMap;
import java.util.Map;

/**
 * {@code ICommand} is an interface that can be implemented with the actual execution of a slash command. This was
 * used being a value of a {@code HashMap} in {@link cartoland.events.CommandUsage}, which mostly lambdas to
 * implement this interface.
 *
 * @since 1.3
 * @author Alex Cai
 */
public interface ICommand
{
	String INVITE = "invite";
	String HELP = "help";
	String CMD = "cmd";
	String MCC = "mcc";
	String COMMAND = "command";
	String FAQ = "faq";
	String QUESTION = "question";
	String DTP = "dtp";
	String DATAPACK = "datapack";
	String JIRA = "jira";
	String BUG = "bug";
	String TOOL = "tool";
	String LANG = "lang";
	String LANGUAGE = "language";
	String QUOTE = "quote";
	String YOUTUBER = "youtuber";
	String INTRODUCE = "introduce";
	String BIRTHDAY = "birthday";
	String ROLL = "roll";
	String MEGUMIN = "megumin";
	String SHUTDOWN = "shutdown";
	String RELOAD = "reload";
	String MUTE = "mute";
	String TEMP_BAN = "temp_ban";
	String CLEAR_MESSAGE = "clear_message";
	String SLOW_MODE = "slow_mode";
	String SCHEDULE = "schedule";
	String ONE_A_TWO_B = "one_a_two_b";
	String LOTTERY = "lottery";
	String TRANSFER = "transfer";
	String TIC_TAC_TOE = "tic_tac_toe";
	String CONNECT_FOUR = "connect_four";
	String LIGHT_OUT = "light_out";

	/**
	 * The execution of a slash command.
	 *
	 * @param event The event that carries information of the user and the command.
	 * @since 1.3
	 * @author Alex Cai
	 */
	void commandProcess(SlashCommandInteractionEvent event); //指令
}

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
class HasSubcommands implements ICommand
{
	protected final Map<String, ICommand> subcommands; //子指令們

	protected HasSubcommands(int subcommandsCount)
	{
		subcommands = HashMap.newHashMap(subcommandsCount);
	}

	/**
	 * The execution of the main command, used for get execution object from {@link #subcommands} through
	 * the name of the subcommand. This should not be overridden.
	 *
	 * @param event The event that carries information of the user and the command.
	 * @since 2.1
	 * @author Alex Cai
	 */
	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		subcommands.get(event.getSubcommandName()).commandProcess(event); //透過HashMap選擇子指令
	}
}

abstract class GameSubcommand implements ICommand
{
	protected final MiniGame.MiniGameMap games;

	protected GameSubcommand(MiniGame.MiniGameMap games)
	{
		this.games = games;
	}

	protected void gameOver(SlashCommandInteractionEvent event, String replyMessage)
	{
		User user = event.getUser();
		MiniGame game = games.remove(user.getIdLong());
		int[] time = TimerHandle.getTime();
		event.reply(replyMessage) //回覆訊息
			.addFiles(FileUpload.fromData(game.getRecord().getBytes(), //附上紀錄
					user.getEffectiveName() + '_' + TimerHandle.getDateString() + '_' + String.format("%02d%02d%02d", time[0], time[1], time[2]) + ".txt"))
			.queue(); //傳送最終結果並上傳紀錄
	}
}