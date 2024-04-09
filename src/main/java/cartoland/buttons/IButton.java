package cartoland.buttons;

import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

public interface IButton
{
	String ARCHIVE_THREAD = "archive_thread";
	String DELETE_THREAD = "delete_thread";
	String RENAME_THREAD = "rename_thread";

	void buttonProcess(ButtonInteractionEvent event);
}

abstract class ShowcaseThreadButtons implements IButton
{
	private final String jsonKey;

	public ShowcaseThreadButtons(String jsonKey)
	{
		this.jsonKey = jsonKey;
	}

	@Override
	public void buttonProcess(ButtonInteractionEvent event)
	{
		long userID = event.getUser().getIdLong();

		Member member = event.getMember();
		if (member == null)
		{
			event.reply(JsonHandle.getString(userID, jsonKey + ".no_permission")).setEphemeral(true).queue();
			return;
		}

		ThreadChannel channel = (ThreadChannel) event.getChannel();
		if (member.hasPermission(Permission.MANAGE_THREADS)) //有權限
			hasPermission(event, channel, member);

		channel.retrieveParentMessage().queue(parentMessage -> //追溯到開啟的訊息
		{
			if (userID == parentMessage.getAuthor().getIdLong()) //是討論串開啟者
				hasPermission(event, channel, member);
			else
				event.reply(JsonHandle.getString(userID, jsonKey + ".no_permission")).setEphemeral(true).queue();
		}, new ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE,
				e -> event.reply(JsonHandle.getString(userID, "showcase_thread_button.no_owner")).setEphemeral(true).queue()));
	}

	public abstract void hasPermission(ButtonInteractionEvent event, ThreadChannel channel, Member member);
}