package cartoland.events;

import cartoland.utilities.AnonymousHandle;
import cartoland.utilities.ObjectAndString;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;

import java.util.List;

public class EditMessage extends ListenerAdapter
{
	@Override
	public void onMessageUpdate(MessageUpdateEvent event)
	{
		if (event.isFromGuild()) //不是私訊
			return; //結束

		Message dm = event.getMessage(); //私訊
		Long undergroundMessageID = AnonymousHandle.getConnection(event.getMessageIdLong()); //查看有沒有記錄到這則訊息
		if (undergroundMessageID == null) //沒有
			return; //結束
		ObjectAndString channelAndString = AnonymousHandle.checkMemberValid(event.getAuthor().getIdLong());
		String errorMessage = channelAndString.string(); //錯誤訊息 預設為空字串""
		if (!errorMessage.isEmpty()) //有錯誤訊息
		{
			dm.reply(errorMessage).mentionRepliedUser(false).queue();
			return;
		}

		((TextChannel) channelAndString.object()).retrieveMessageById(undergroundMessageID).queue(message -> //獲取對應的地下訊息
		{
			List<Message.Attachment> messageAttachments = message.getAttachments(); //原本的檔案們
			if (messageAttachments.isEmpty()) //本來就沒有 也不可能新增
			{
				message.editMessage(dm.getContentRaw()).queue(); //只改內文
				return; //結束
			}

			List<Message.Attachment> dmAttachments = dm.getAttachments(); //編輯後的檔案們
			int dmAttachmentsSize = dmAttachments.size(); //編輯後的檔案個數
			if (dmAttachmentsSize == messageAttachments.size()) //檔案只能刪除 所以如果總數一樣 代表沒人被刪
			{
				message.editMessage(dm.getContentRaw()).setAttachments(messageAttachments).queue(); //用setAttachments讓舊訊息的檔案留存
				return; //結束
			}

			//有人被刪掉了
			if (dmAttachmentsSize == 0) //如果全刪
			{
				message.editMessage(dm.getContentRaw()).setAttachments((List<Message.Attachment>) null).queue(); //將附加物清除
				return; //結束
			}

			//不是全刪
			message.editMessage(dm.getContentRaw())
					.setFiles(dmAttachments.stream()
							.map(attachment -> FileUpload.fromStreamSupplier(attachment.getFileName(), () -> attachment.getProxy().download().join()))
							.toList())
					.queue();
		}); //編輯訊息內文和附加物
	}
}