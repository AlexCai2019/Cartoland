package cartoland.messages;

import cartoland.utilities.FileHandle;
import cartoland.utilities.IDAndEntities;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@code PrivateMessage} is a listener that triggers when a user types anything in the direct message to the bot. This
 * class is in an array in {@link cartoland.events.MessageEvent}.
 *
 * @since 1.0
 * @author Alex Cai
 */
public class PrivateMessage implements IMessage
{
	@Override
	public void messageProcess(MessageReceivedEvent event)
	{
		Message message = event.getMessage();
		User author = event.getAuthor();

		Member member = IDAndEntities.cartolandServer.getMemberById(author.getIdLong());
		String can_t = ", hence you can't send message to the NSFW channel.";
		if (member == null)
		{
			message.reply("You are not a member of " + IDAndEntities.cartolandServer.getName() + can_t)
					.mentionRepliedUser(false)
					.queue();
			return;
		}
		if (member.isTimedOut())
		{
			message.reply("You are timed out from " + IDAndEntities.cartolandServer.getName() + can_t)
					.mentionRepliedUser(false)
					.queue();
			return;
		}
		if (!member.getRoles().contains(IDAndEntities.nsfwRole))
		{
			message.reply("You don't have role " + IDAndEntities.nsfwRole.getName() + can_t)
					.mentionRepliedUser(false)
					.queue();
			return;
		}

		String rawMessage = message.getContentRaw();
		List<Attachment> attachments = message.getAttachments();
		if (attachments.size() != 0)
			rawMessage += attachments.stream().map(Attachment::getUrl).collect(Collectors.joining("\n", "\n", ""));

		IDAndEntities.undergroundChannel.sendMessage(rawMessage).queue(); //私訊轉到地下聊天室
		FileHandle.log(author.getName() + "(" + author.getId() + ") typed \"" + rawMessage + "\" in direct message.");
	}
}