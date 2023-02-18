package cartoland.events.commands;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface ICommandAndComplete extends ICommand, IComplete
{}

/**
 * {@code ICommand} is an interface that can be implemented with the actual execution of a slash command. This was
 * used being a value of a {@code HashMap} in {@link CommandUsage}, which mostly lambdas to implement this interface.
 *
 * @since 1.3
 * @author Alex Cai
 */
interface ICommand
{
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
 * {@code IComplete} is an interface that can be implemented with the actual complete of typing a slash command.
 *
 * @since 1.5
 * @author Alex Cai
 */
interface IComplete
{
	void completing(CommandAutoCompleteInteractionEvent event); //自動完成
}