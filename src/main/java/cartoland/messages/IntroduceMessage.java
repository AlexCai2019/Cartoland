package cartoland.messages;

import cartoland.commands.IntroduceCommand;
import cartoland.utilities.CommonFunctions;
import cartoland.utilities.IDs;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@code IntroduceMessage} is a listener that triggers when a user types anything in the self intro channel. This class
 * is in an array in {@link cartoland.events.MessageEvent}.
 *
 * @since 2.0
 * @author Alex Cai
 */
public class IntroduceMessage implements IMessage
{
	@Override
	public boolean messageCondition(MessageReceivedEvent event)
	{
		return event.getChannel().getIdLong() == IDs.SELF_INTRO_CHANNEL_ID;
	}

	@Override
	public void messageProcess(MessageReceivedEvent event)
	{
		Message message = event.getMessage();
		String introduction = message.getContentRaw();
		List<Message.Attachment> attachments = message.getAttachments();
		if (!attachments.isEmpty())
			introduction += attachments.stream().map(CommonFunctions.getUrl).collect(Collectors.joining("\n", "\n", ""));
		IntroduceCommand.updateIntroduction(message.getAuthor().getIdLong(), introduction); //將自介頻道內的訊息設為/introduce的內容
	}
}