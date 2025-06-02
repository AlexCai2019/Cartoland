package cartoland.messages;

import cartoland.utilities.IDs;
import cartoland.utilities.MembersHandle;
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
	@Override
	public boolean messageCondition(MessageReceivedEvent event)
	{
		return event.getChannel().getIdLong() == IDs.SELF_INTRO_CHANNEL_ID; //是否在自我介紹的頻道
	}

	@Override
	public void messageProcess(MessageReceivedEvent event)
	{
		Message message = event.getMessage();
		List<Message.Attachment> attachments = message.getAttachments();

		String introductionString; //介紹文字
		if (attachments.isEmpty()) //如果沒有附件 這是比較常見的情形
			introductionString = message.getContentRaw(); //直接等於訊息內容
		else //有附件 改用StringBuilder處理
		{
			StringBuilder introduceBuilder = new StringBuilder(message.getContentRaw()); //訊息內容
			for (Message.Attachment attachment : attachments)
				introduceBuilder.append('\n').append(attachment.getUrl()); //一一獲取附件的連結
			introductionString = introduceBuilder.toString();
		}
		MembersHandle.updateIntroduction(message.getAuthor().getIdLong(), introductionString); //將自介頻道內的訊息設為/introduce的內容
	}
}