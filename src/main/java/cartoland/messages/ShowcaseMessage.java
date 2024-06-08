package cartoland.messages;

import cartoland.buttons.IButton;
import cartoland.utilities.IDs;
import cartoland.utilities.TimerHandle;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.HashSet;
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
	private final Button archiveButton = Button.success(IButton.ARCHIVE_THREAD, "Archive Thread")
			.withEmoji(Emoji.fromUnicode("📁"));
	private final Button deleteButton = Button.danger(IButton.DELETE_THREAD, "Delete Thread")
			.withEmoji(Emoji.fromUnicode("🗑️"));
	private final Button renameButton = Button.primary(IButton.RENAME_THREAD, "Edit Title")
			.withEmoji(Emoji.fromUnicode("✏️"));
	private final Set<Long> showcaseChannels = HashSet.newHashSet(5);

	public ShowcaseMessage()
	{
		showcaseChannels.add(IDs.DATAPACK_SHOWCASE_CHANNEL_ID);
		showcaseChannels.add(IDs.MAP_SHOWCASE_CHANNEL_ID);
		showcaseChannels.add(IDs.BUILDING_SHOWCASE_CHANNEL_ID);
		showcaseChannels.add(IDs.MODEL_SHOWCASE_CHANNEL_ID);
		showcaseChannels.add(IDs.VIDEOS_AND_STREAMS_CHANNEL_ID);
	}

	@Override
	public boolean messageCondition(MessageReceivedEvent event)
	{
		return showcaseChannels.contains(event.getChannel().getIdLong()); //在創作展示四頻道內
	}

	@Override
	public void messageProcess(MessageReceivedEvent event)
	{
		String name = event.getAuthor().getEffectiveName();
		event.getMessage()
				.createThreadChannel(name + '(' + TimerHandle.getDateString() + ')')
				.flatMap(thread -> thread.sendMessage("Thread automatically created by " + name + " in " + event.getChannel().getAsMention()).addActionRow(archiveButton, deleteButton, renameButton))
				.flatMap(Message::pin)
				.queue();
	}
}