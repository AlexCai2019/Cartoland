package cartoland.messages;

import cartoland.utilities.FileHandle;
import cartoland.utilities.IDs;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class TrapMessage implements IMessage
{
	private static final Logger logger = LoggerFactory.getLogger(TrapMessage.class);

	private static final int DELETE_MESSAGES_MINUTES = 30;

	@Override
	public boolean messageCondition(MessageReceivedEvent event)
	{
		return event.getChannel().getIdLong() == IDs.TRAP_CHANNEL_ID; //第歐根尼俱樂部
	}

	@Override
	public void messageProcess(MessageReceivedEvent event)
	{
		Member member = event.getMember();
		if (member == null || member.isOwner()) //不能封鎖群主
			return;

		Guild cartoland = event.getGuild(); //創聯
		if (!cartoland.getSelfMember().canInteract(member)) //只能封鎖比機器人身分更低的成員
			return;

		cartoland.ban(member, DELETE_MESSAGES_MINUTES, TimeUnit.MINUTES)
				.reason('在' + event.getChannel().getName() + "傳送訊息。")
				.queue();

		ThreadChannel strangersRoom = cartoland.getThreadChannelById(IDs.STRANGERS_ROOM_CHANNEL_ID); //討論串
		if (strangersRoom != null)
		{
			String mention = member.getAsMention();
			strangersRoom.sendMessage(mention + " 被停權了。\n" + mention + " was banned.").queue(); //在會客室裡傳送停權訊息
		}
		logger.info("trap {} {}", member.getId(), event.getMessage().getContentRaw());
	}
}