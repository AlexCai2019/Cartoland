package cartoland.messages;

import cartoland.buttons.IButton;
import cartoland.utilities.IDs;
import cartoland.utilities.JsonHandle;
import cartoland.utilities.TimerHandle;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.Set;

/**
 * {@code ShowcaseMessage} is a listener that triggers when a user types anything in text channels (excluding thread
 * channels) in showcase category. This class is in an array in {@link cartoland.events.MessageEvent}.
 *
 * @since 2.0
 * @author Alex Cai
 */
public class ShowcaseMessage implements IMessage
{
	private final Set<Long> showcaseChannels = Set.of(IDs.DATAPACK_SHOWCASE_CHANNEL_ID, IDs.MAP_SHOWCASE_CHANNEL_ID,
			IDs.BUILDING_SHOWCASE_CHANNEL_ID, IDs.MODEL_SHOWCASE_CHANNEL_ID, IDs.VIDEOS_AND_STREAMS_CHANNEL_ID);

	@Override
	public boolean messageCondition(MessageReceivedEvent event)
	{
		return showcaseChannels.contains(event.getChannel().getIdLong()); //在創作展示四頻道內
	}

	@Override
	public void messageProcess(MessageReceivedEvent event)
	{
		User author = event.getAuthor();
		long userID = author.getIdLong();
		String name = author.getEffectiveName();

		Button archiveButton = Button.success(IButton.ARCHIVE_THREAD, JsonHandle.getString(userID, "archive_thread.name")).withEmoji(Emoji.fromUnicode("📁"));
		Button renameButton = Button.primary(IButton.RENAME_THREAD, JsonHandle.getString(userID, "rename_thread.name")).withEmoji(Emoji.fromUnicode("✏️"));
		event.getMessage()
			.createThreadChannel(name + '(' + TimerHandle.getDateString() + ')')
			.flatMap(thread -> thread.sendMessage(JsonHandle.getString(userID, "showcase_thread.creation", name)).addActionRow(archiveButton, renameButton))
			.flatMap(Message::pin)
			.queue();
	}
}