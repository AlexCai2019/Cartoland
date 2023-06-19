package cartoland.messages;

import cartoland.events.ClickedButton;
import cartoland.utilities.IDAndEntities;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.time.LocalDate;

public class ShowcaseMessage implements IMessage
{
	private final Button archiveButton = Button.success(ClickedButton.ARCHIVE_THREAD, "Archive thread")
			.withEmoji(Emoji.fromUnicode("📁"));
	private final Button renameButton = Button.primary(ClickedButton.RENAME_THREAD, "Rename Thread")
			.withEmoji(Emoji.fromUnicode("✏️"));

	@Override
	public boolean messageCondition(MessageReceivedEvent event)
	{
		if (!event.isFromGuild() || event.isFromThread())
			return false; //是私訊或在討論串內
		Category category = event.getMessage().getCategory();
		return category != null && category.getIdLong() == IDAndEntities.SHOWCASE_CATEGORY_ID;
	}

	@Override
	public void messageProcess(MessageReceivedEvent event)
	{
		String name = event.getAuthor().getEffectiveName();
		event.getMessage().createThreadChannel(name + '(' + LocalDate.now() + ')').queue(threadChannel ->
			threadChannel.sendMessage("Thread automatically created by " + name + " in " + event.getChannel().getAsMention())
					.addActionRow(archiveButton, renameButton).queue());
	}
}