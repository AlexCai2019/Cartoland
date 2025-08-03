package cartoland.contexts;

import cartoland.utilities.IDs;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;

public class PinContext implements IContext
{
	@Override
	public void contextProcess(MessageContextInteractionEvent event)
	{
		Member member = event.getMember();
		if (member == null)
		{
			event.reply("You should use this function in a server!").setEphemeral(true).queue();
			return;
		}

		Message target = event.getTarget();
		boolean isDiscussPostOwner = //如果是地圖專版的論壇貼文的開啟者 可以無視權限直接釘選
				event.getChannel() instanceof ThreadChannel thread && //是討論串
						thread.getParentChannel().getIdLong() == IDs.MAP_DISCUSS_CHANNEL_ID && //是地圖專版
						thread.getOwnerIdLong() == event.getUser().getIdLong(); //是開啟者

		if (!isDiscussPostOwner && !member.hasPermission(Permission.MESSAGE_MANAGE)) //如果不是地圖專版貼文不是開啟者且沒有權限
		{
			event.reply("You don't have the permission to pin this message!").setEphemeral(true).queue();
			return;
		}

		if (target.isPinned())
		{
			event.reply("Unpinned message.").queue();
			target.unpin().queue(); //解釘
		}
		else
		{
			event.reply("Pinned message.").queue();
			target.pin().queue(); //釘選
		}
	}
}