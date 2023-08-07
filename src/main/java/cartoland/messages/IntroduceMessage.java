package cartoland.messages;

import cartoland.commands.IntroduceCommand;
import cartoland.utilities.IDs;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

/**
 * {@code IntroduceMessage} is a listener that triggers when a user types anything in the self intro channel. This class
 * is in an array in {@link cartoland.events.MessageEvent}.
 *
 * @since 2.0
 * @author Alex Cai
 */
public class IntroduceMessage implements IMessage
{
	private final StringBuilder introductionBuilder = new StringBuilder();

	@Override
	public boolean messageCondition(MessageReceivedEvent event)
	{
		return event.getChannel().getIdLong() == IDs.SELF_INTRO_CHANNEL_ID; //是否在自我介紹的頻道
	}

	@Override
	public void messageProcess(MessageReceivedEvent event)
	{
		Message message = event.getMessage();
		introductionBuilder.setLength(0);
		introductionBuilder.append(message.getContentRaw());
		List<Message.Attachment> attachments = message.getAttachments();
		for (Message.Attachment attachment : attachments)
			introductionBuilder.append('\n').append(attachment.getUrl());
		IntroduceCommand.updateIntroduction(message.getAuthor().getIdLong(), introductionBuilder.toString()); //將自介頻道內的訊息設為/introduce的內容
	}
}