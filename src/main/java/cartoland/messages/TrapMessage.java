package cartoland.messages;

import cartoland.utilities.FileHandle;
import cartoland.utilities.IDs;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public class TrapMessage implements IMessage
{
	@Override
	public boolean messageCondition(MessageReceivedEvent event)
	{
		return event.getChannel().getIdLong() == IDs.TRAP_CHANNEL_ID;
	}

	@Override
	public void messageProcess(MessageReceivedEvent event)
	{
		Member member = event.getMember();
		if (member == null|| member.isOwner()) //不能封鎖群主
			return;
		Guild guild = event.getGuild();
		if (!guild.getSelfMember().canInteract(member)) //只能封鎖比機器人身分更低的成員
			return;
		String channelName = event.getChannel().getName(); //頻道名稱
		guild.ban(member, 1, TimeUnit.MINUTES).reason('在' + channelName + "傳送訊息。\nSend messages in " + channelName + '.').queue();
		ThreadChannel strangersRoom = guild.getThreadChannelById(IDs.STRANGERS_ROOM_CHANNEL_ID); //討論串
		if (strangersRoom != null)
		{
			String mention = member.getAsMention();
			strangersRoom.sendMessage(mention + "被停權了。\n" + mention + " was banned.").queue(); //在會客室裡傳送停權訊息
		}
		FileHandle.log("trap ", member.getId(), ' ', event.getMessage().getContentRaw());
	}
}