package cartoland.buttons;

import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class ArchiveThreadButton extends ShowcaseThreadButtons
{
	public ArchiveThreadButton()
	{
		super(ARCHIVE_THREAD);
	}

	@Override
	public void buttonProcess(ButtonInteractionEvent event)
	{
		if (event.getChannel().asThreadChannel().isArchived())
			event.reply(JsonHandle.getString(event.getUser().getIdLong(), "archive_thread.already_archived")).setEphemeral(true).queue();
		else
			super.buttonProcess(event);
	}

	@Override
	public void adminManage(ButtonInteractionEvent event, ThreadChannel channel)
	{
		User user = event.getUser();
		event.reply(JsonHandle.getString(user.getIdLong(), "archive_thread.archived", user.getAsMention()))
			.flatMap(hook -> channel.getManager().setArchived(true))
			.queue();
	}
}