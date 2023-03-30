package cartoland.messages;

/**
 * {@code IMessage} is an interface that deals with message event. Subclasses store in an array which is a field of
 * {@link cartoland.events.MessageEvent}. This class can't be instantiated via lambda.
 *
 * @since 2.0
 * @author Alex Cai
 */
public interface IMessage
{
	boolean messageCondition(net.dv8tion.jda.api.events.message.MessageReceivedEvent event);
	void messageProcess(net.dv8tion.jda.api.events.message.MessageReceivedEvent event);
}