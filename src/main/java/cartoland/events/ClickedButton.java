package cartoland.events;

import cartoland.utilities.FileHandle;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

/**
 * {@code ReceiveModal} is a listener that triggers when a user interact with a button. This class was registered in
 * {@link cartoland.Cartoland#main(String[])}, with the build of JDA.
 *
 * @since 2.0
 * @author Alex Cai
 */
public class ClickedButton extends ListenerAdapter
{
	public static final String ARCHIVE_THREAD = "archive_thread";
	public static final String DELETE_THREAD = "delete_thread";
	public static final String RENAME_THREAD = "rename_thread";

	private final TextInput.Builder newTitleInputBuilder = TextInput.create(ReceiveModal.NEW_TITLE_TEXT, "New Title", TextInputStyle.SHORT)
			.setRequiredRange(0, 100);

	@Override
	public void onButtonInteraction(ButtonInteractionEvent event)
	{
		User user = event.getUser();
		long userID = user.getIdLong();
		String componentName = event.getComponentId();

		switch (componentName)
		{
			case ARCHIVE_THREAD ->
			{
				ThreadChannel channel = (ThreadChannel) event.getChannel();
				if (channel.isArchived())
				{
					event.reply(JsonHandle.getString(userID, "archive_thread.already_archived")).setEphemeral(true).queue();
					return;
				}

				Member member = event.getMember();
				if (member == null)
				{
					event.reply(JsonHandle.getString(userID, "archive_thread.no_permission")).setEphemeral(true).queue();
					return;
				}

				event.deferReply().queue();
				channel.retrieveParentMessage().queue(parentMessage ->
				{
					if (member.hasPermission(Permission.MANAGE_THREADS) || userID == parentMessage.getAuthor().getIdLong()) //有權限 或 是討論串開啟者
					{
						event.getHook()
							.sendMessage(JsonHandle.getString(userID, "archive_thread.archived", member.getEffectiveName()))
							.complete(); //complete 才不會導致討論串被關了後才回覆
						channel.getManager().setArchived(true).queue();
					}
					else
						event.getHook()
							.sendMessage(JsonHandle.getString(userID, "archive_thread.no_permission"))
							.setEphemeral(true)
							.queue();
				});
			}

			case DELETE_THREAD ->
			{
				Member member = event.getMember();
				if (member == null)
				{
					event.reply(JsonHandle.getString(userID, "delete_thread.no_permission")).setEphemeral(true).queue();
					return;
				}

				event.deferReply().queue();
				ThreadChannel channel = (ThreadChannel) event.getChannel();
				channel.retrieveParentMessage().queue(parentMessage ->
				{
					if (member.hasPermission(Permission.MANAGE_THREADS) || userID == parentMessage.getAuthor().getIdLong()) //有權限 或 是討論串開啟者
					{
						event.getHook()
								.sendMessage(JsonHandle.getString(userID, "delete_thread.deleted", member.getEffectiveName()))
								.complete(); //complete 才不會導致討論串被刪了後才回覆
						channel.delete().queue();
					}
					else
						event.getHook()
							.sendMessage(JsonHandle.getString(userID, "delete_thread.no_permission"))
							.setEphemeral(true)
							.queue();
				});
			}

			case RENAME_THREAD ->
			{
				Member member = event.getMember();
				if (member == null)
				{
					event.reply(JsonHandle.getString(userID, "rename_thread.no_permission")).setEphemeral(true).queue();
					return;
				}

				ThreadChannel channel = (ThreadChannel) event.getChannel();
				channel.retrieveParentMessage().queue(parentMessage ->
				{
					if (member.hasPermission(Permission.MANAGE_THREADS) || userID == parentMessage.getAuthor().getIdLong()) //有權限 或 是討論串開啟者
						event.replyModal(
								Modal.create(ReceiveModal.NEW_TITLE_MODAL_ID, JsonHandle.getString(userID, "rename_thread.set_new_thread_title"))
										.addComponents(ActionRow.of(newTitleInputBuilder.setValue(channel.getName()).build()))
										.build()).queue(); //如果Modal可以事先建好就好了
					else
						event.reply(JsonHandle.getString(userID, "rename_thread.no_permission")).setEphemeral(true).queue();
				});
			}
		}

		FileHandle.log(user.getName(), '(', user.getId(), ") [", componentName + ']');
	}
}