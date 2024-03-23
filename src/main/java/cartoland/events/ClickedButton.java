package cartoland.events;

import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
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
	public static final String RENAME_THREAD = "rename_thread";

	private final TextInput.Builder newTitleInputBuilder = TextInput.create(ReceiveModal.NEW_TITLE_TEXT, "New Title", TextInputStyle.SHORT)
			.setRequiredRange(0, 100);

	@Override
	public void onButtonInteraction(ButtonInteractionEvent event)
	{
		long userID = event.getUser().getIdLong();

		switch (event.getComponentId())
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
				if (member == null || (!member.hasPermission(Permission.MANAGE_THREADS) && member.getIdLong() != channel.getOwnerIdLong())) //獲取失敗 或 沒有權限
				{
					event.reply(JsonHandle.getString(userID, "archive_thread.no_permission")).setEphemeral(true).queue();
					return;
				}

				event.reply(JsonHandle.getString(userID, "archive_thread.archived",
						member.getEffectiveName())).complete(); //complete 才不會導致討論串被關了後才回覆
				channel.getManager().setArchived(true).queue();
			}

			case RENAME_THREAD ->
			{
				ThreadChannel channel = (ThreadChannel) event.getChannel();

				Member member = event.getMember();
				if (member == null || (!member.hasPermission(Permission.MANAGE_THREADS) && member.getIdLong() != channel.getOwnerIdLong())) //獲取失敗 或 沒有權限
				{
					event.reply(JsonHandle.getString(userID, "rename_thread.no_permission")).setEphemeral(true).queue();
					return;
				}

				event.replyModal(
						Modal.create(ReceiveModal.NEW_TITLE_MODAL_ID, JsonHandle.getString(userID, "rename_thread.set_new_thread_title"))
								.addComponents(ActionRow.of(newTitleInputBuilder.setValue(channel.getName()).build()))
								.build()).queue(); //如果Modal可以事先建好就好了
			}
		}
	}
}