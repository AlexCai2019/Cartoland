package cartoland.messages;

public interface IMessage
{
	void messageProcess(net.dv8tion.jda.api.events.message.MessageReceivedEvent event);
}