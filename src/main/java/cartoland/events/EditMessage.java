package cartoland.events;

import cartoland.utilities.AnonymousHandle;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
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
		Long undergroundMessageID = AnonymousHandle.getConnection(dm.getIdLong()); //查看有沒有記錄到這則訊息
		if (undergroundMessageID == null) //沒有
			return; //結束
		TextChannel[] undergroundChannel = new TextChannel[1];
		String errorMessage = AnonymousHandle.checkMemberValid(event.getAuthor().getIdLong(), undergroundChannel);
		if (errorMessage.isEmpty())
			undergroundChannel[0].retrieveMessageById(undergroundMessageID) //獲取對應的地下訊息
					.queue(message -> message.editMessage(dm.getContentRaw()).setAttachments(dm.getAttachments()).queue()); //編輯訊息內文和附加物
		else
			dm.reply(errorMessage).mentionRepliedUser(false).queue();
	}
}