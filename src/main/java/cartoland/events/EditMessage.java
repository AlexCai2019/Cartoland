package cartoland.events;

import cartoland.utilities.AnonymousHandle;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EditMessage extends ListenerAdapter
{
	@Override
	public void onMessageUpdate(MessageUpdateEvent event)
	{
		MessageChannel channel = event.getChannel();
		if (channel.getType().isGuild()) //不是私訊
			return; //結束

		Message message = event.getMessage();
		Long undergroundMessageID = AnonymousHandle.getConnection(message.getIdLong()); //查看有沒有記錄到這則訊息
		if (undergroundMessageID == null) //沒有
			return; //結束
		TextChannel[] undergroundChannel = new TextChannel[1];
		String isValid = AnonymousHandle.checkMemberValid(event.getAuthor().getIdLong(), undergroundChannel);
		if (!isValid.isEmpty())
		{
			message.reply(isValid).mentionRepliedUser(false).queue();
			return;
		}
		undergroundChannel[0].retrieveMessageById(undergroundMessageID).queue(unsergroundMessage -> //獲取對應的地下訊息
		{
			 unsergroundMessage.editMessage(message.getContentRaw()).queue(); //編輯訊息內文
			 unsergroundMessage.editMessageAttachments(message.getAttachments()).queue(); //編輯訊息附加物
		});
	}
}