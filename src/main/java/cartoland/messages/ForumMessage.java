package cartoland.messages;

import cartoland.utilities.ForumsHandle;
import cartoland.utilities.IDs;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * {@code ForumMessage} is a listener that triggers when a user types anything in any post in Map-Discuss forum
 * channel and Questions
 * forum channel. This class is in an array in {@link cartoland.events.MessageEvent}.
 *
 * @since 1.5
 * @author Alex Cai
 */
public class ForumMessage implements IMessage
{
	@Override
	public boolean messageCondition(MessageReceivedEvent event)
	{
		if (!event.getChannelType().isThread())
			return false; //非討論串者 不得通過

		Category category = event.getChannel()
				.asThreadChannel()
				.getParentChannel()
				.asStandardGuildChannel()
				.getParentCategory();
		//獲取類別失敗就不執行後面那個
		return category != null && category.getIdLong() == IDs.FORUM_CATEGORY_ID;
	}

	@Override
	public void messageProcess(MessageReceivedEvent event)
	{
		ThreadChannel forumPost = event.getChannel().asThreadChannel();
		Message message = event.getMessage();

		if (ForumsHandle.questionForumPostIsIdled(forumPost)) //是問題貼文 且處在關閉狀態
			ForumsHandle.unIdleQuestionForumPost(forumPost, false);

		if (ForumsHandle.isFirstMessage(forumPost)) //如果是第一次傳送初始訊息
			ForumsHandle.startStuff(forumPost); //傳送指南

		if (!ForumsHandle.typedResolved(message)) //不是:resolved:表情符號
			return;

		Member member = event.getMember();
		if (member == null || (member.getIdLong() != forumPost.getOwnerIdLong() && member.hasPermission(Permission.MANAGE_THREADS)))
			return; //不是討論串擁有者 且 沒有管理討論串的權限
		ForumsHandle.archiveForumPost(forumPost, message);
	}
}