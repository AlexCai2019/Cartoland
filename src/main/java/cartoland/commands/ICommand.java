package cartoland.commands;

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
	String MEGUMIN = "megumin";
	String SHUTDOWN = "shutdown";
	String RELOAD = "reload";
	String ADMIN = "admin";
	String ONE_A_TWO_B = "one_a_two_b";
	String LOTTERY = "lottery";
	String TRANSFER = "transfer";
	String TIC_TAC_TOE = "tic_tac_toe";

	/**
	 * The execution of a slash command.
	 *
	 * @param event The event that carries information of the user and the command.
	 * @since 1.3
	 * @author Alex Cai
	 */
	void commandProcess(net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent event); //指令
}