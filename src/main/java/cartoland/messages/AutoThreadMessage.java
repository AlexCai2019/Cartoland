package cartoland.messages;

import cartoland.buttons.IButton;
import cartoland.utilities.IDs;
import cartoland.utilities.JsonHandle;
import cartoland.utilities.TimerHandle;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Set;

/**
 * {@code AutoThreadMessage} is a listener that triggers when a user types anything in text channels that need auto thread.
 * This class is in an array in {@link cartoland.events.MessageEvent}.
 *
 * @since 2.0
 * @author Alex Cai
 */
public class AutoThreadMessage implements IMessage
{
	private final Set<Long> autoThreadChannels = Set.of(IDs.VIDEOS_CHANNEL_ID, IDs.MAP_REVIEW_CHANNEL_ID, IDs.PLAY_TEST_CHANNEL_ID);

	@Override
	public boolean messageCondition(MessageReceivedEvent event)
	{
		return autoThreadChannels.contains(event.getChannel().getIdLong()); //在創作展示六頻道內
	}

	@Override
	public void messageProcess(MessageReceivedEvent event)
	{
		User author = event.getAuthor(); //訊息發送者
		long userID = author.getIdLong();
		String name = author.getEffectiveName();

		Button archiveButton = Button.success(IButton.ARCHIVE_THREAD, JsonHandle.getString(userID, "archive_thread.name")).withEmoji(Emoji.fromUnicode("📁"));
		Button renameButton = Button.primary(IButton.RENAME_THREAD, JsonHandle.getString(userID, "rename_thread.name")).withEmoji(Emoji.fromUnicode("✏️"));
		event.getMessage()
			.createThreadChannel(name + '(' + TimerHandle.getDateString() + ')')
			.flatMap(thread -> thread.sendMessage(JsonHandle.getString(userID, "showcase_thread.creation", name))
									.addComponents(ActionRow.of(archiveButton, renameButton)))
			.flatMap(Message::pin)
			.queue();
	}
}