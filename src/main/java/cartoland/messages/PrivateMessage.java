package cartoland.messages;

import cartoland.Cartoland;
import cartoland.utilities.CommonFunctions;
import cartoland.utilities.FileHandle;
import cartoland.utilities.IDs;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.sticker.Sticker;
import net.dv8tion.jda.api.entities.sticker.StickerItem;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

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
	public boolean messageCondition(MessageReceivedEvent event)
	{
		return !event.isFromGuild();
	}

	@Override
	public void messageProcess(MessageReceivedEvent event)
	{
		Message message = event.getMessage();
		User author = event.getAuthor();

		Guild cartoland = Cartoland.getJDA().getGuildById(IDs.CARTOLAND_SERVER_ID);
		if (cartoland == null)
		{
			message.reply("Can't get Cartoland server.").mentionRepliedUser(false).queue();
			return;
		}

		Role nsfwRole = cartoland.getRoleById(IDs.NSFW_ROLE_ID);
		if (nsfwRole == null)
		{
			message.reply("Can't get NSFW role.").mentionRepliedUser(false).queue();
			return;
		}

		TextChannel undergroundChannel = cartoland.getTextChannelById(IDs.UNDERGROUND_CHANNEL_ID);
		if (undergroundChannel == null)
		{
			message.reply("Can't get underground channel.").mentionRepliedUser(false).queue();
			return;
		}

		final String can_t = ", hence you can't send message to the NSFW channel.";

		cartoland.retrieveMemberById(author.getIdLong()).queue(member ->
		{
			if (member.isTimedOut())
			{
				message.reply("You are timed out from " + cartoland.getName() + can_t)
						.mentionRepliedUser(false).queue();
				return;
			}

			if (!member.getRoles().contains(nsfwRole))
			{
				message.reply("You don't have role " + nsfwRole.getName() + can_t)
						.mentionRepliedUser(false).queue();
				return;
			}

			String rawMessage = message.getContentRaw();
			List<Message.Attachment> attachments = message.getAttachments();
			if (!attachments.isEmpty())
				rawMessage += attachments.stream().map(CommonFunctions.getUrl).collect(Collectors.joining("\n", "\n", ""));
			List<StickerItem> stickerItems = message.getStickers();
			if (!stickerItems.isEmpty())
				rawMessage += stickerItems.stream().map(Sticker::getIconUrl).collect(Collectors.joining("\n", "\n", ""));

			undergroundChannel.sendMessage(rawMessage).queue(); //私訊轉到地下聊天室
			FileHandle.log(author.getName() + "(" + author.getId() + ") typed \"" + rawMessage + "\" in direct message.");
		}, new ErrorHandler().handle(ErrorResponse.UNKNOWN_MEMBER, e ->
				message.reply("You are not a member of " + cartoland.getName() + can_t).mentionRepliedUser(false).queue()));
	}
}