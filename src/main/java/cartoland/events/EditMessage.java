package cartoland.events;

import cartoland.utilities.AnonymousHandle;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

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
		AnonymousHandle.StringAndChannel stringAndChannel = AnonymousHandle.checkMemberValid(event.getAuthor().getIdLong());
		String errorMessage = stringAndChannel.string(); //錯誤訊息 預設為空字串""
		if (errorMessage.isEmpty()) //沒有錯誤訊息
			stringAndChannel.channel().retrieveMessageById(undergroundMessageID) //獲取對應的地下訊息
					.queue(message -> message.editMessage(dm.getContentRaw()).setAttachments(dm.getAttachments()).queue()); //編輯訊息內文和附加物
		else
			dm.reply(errorMessage).mentionRepliedUser(false).queue();
	}
}