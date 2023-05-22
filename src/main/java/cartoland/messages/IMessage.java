package cartoland.messages;

/**
 * {@code IMessage} is an interface that deals with message event. Subclasses are stored in an array which is a field of
 * {@link cartoland.events.MessageEvent}. This class can't be instantiated via lambda.
 *
 * @since 2.0
 * @author Alex Cai
 */
public interface IMessage
{
	/**
	 * The condition of a message event.
	 *
	 * @param event The event that carries information of the user and the message.
	 * @return If the event matches the condition.
	 * @since 2.0
	 * @author Alex Cai
	 */
	boolean messageCondition(net.dv8tion.jda.api.events.message.MessageReceivedEvent event);

	/**
	 * The execution of a message event.
	 *
	 * @param event The event that carries information of the user and the message.
	 * @since 2.0
	 * @author Alex Cai
	 */
	void messageProcess(net.dv8tion.jda.api.events.message.MessageReceivedEvent event);
}