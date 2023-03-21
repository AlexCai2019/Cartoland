package cartoland.messages;

/**
 * @since 2.0
 * @author Alex Cai
 */
public interface IMessage
{
	void messageProcess(net.dv8tion.jda.api.events.message.MessageReceivedEvent event);
}