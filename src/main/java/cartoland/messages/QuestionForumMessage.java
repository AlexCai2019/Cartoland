package cartoland.messages;

import cartoland.utilities.IDs;
import cartoland.utilities.QuestionForumHandle;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * {@code QuestionForumMessage} is a listener that triggers when a user types anything in any post in Questions
 * forum channel. This class is in an array in {@link cartoland.events.MessageEvent}.
 *
 * @since 1.5
 * @author Alex Cai
 */
public class QuestionForumMessage implements IMessage
{
	private ThreadChannel forumPost = null;

	@Override
	public boolean messageCondition(MessageReceivedEvent event)
	{
		MessageChannelUnion channel = event.getChannel();
		return channel.getType().isThread() &&
				(forumPost = channel.asThreadChannel()).getParentChannel().getIdLong() == IDs.QUESTIONS_CHANNEL_ID;
	}

	@Override
	public void messageProcess(MessageReceivedEvent event)
	{
		if (forumPost == null)
			return;
		Message message = event.getMessage();
		if (QuestionForumHandle.isIdled(forumPost))
			QuestionForumHandle.unIdleForumPost(forumPost, false);

		if (QuestionForumHandle.shouldSendStartEmbed(forumPost)) //如果是第一次傳送初始訊息
			QuestionForumHandle.sendStartEmbed(forumPost); //傳送指南

		if (!QuestionForumHandle.typedResolved(message)) //不是:resolved:表情符號
			return;

		Member member = event.getMember();
		if (member == null || (member.getIdLong() != forumPost.getOwnerIdLong() && member.hasPermission(Permission.MANAGE_THREADS)))
			return; //不是討論串擁有者 且 沒有管理討論串的權限
		QuestionForumHandle.archiveForumPost(forumPost, message);
	}
}