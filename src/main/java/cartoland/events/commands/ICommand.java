package cartoland.events.commands;

/**
 * {@code ICommand} is an interface that can be implemented with the actual execution of a slash command. This was
 * used being a value of a {@code HashMap} in {@link cartoland.events.CommandUsage}, which mostly lambdas to implement this interface.
 *
 * @since 1.3
 * @author Alex Cai
 */
public interface ICommand
{
	/**
	 * The execution of a slash command.
	 *
	 * @param event The event that carries information of the user and the command.
	 * @since 1.3
	 * @author Alex Cai
	 */
	void commandProcess(net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent event); //指令
}