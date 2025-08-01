package cartoland.commands;

import cartoland.modals.IModal;
import cartoland.modals.UpdateIntroduceModal;
import cartoland.utilities.JsonHandle;
import cartoland.utilities.MembersHandle;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

/**
 * {@code IntroduceCommand} is an execution when a user uses /introduce command. This class implements
 * {@link ICommand} interface, which is for the commands HashMap in {@link cartoland.events.CommandUsage}. This
 * class doesn't handle sub commands, but call other classes to deal with it.
 *
 * @since 2.0
 * @author Alex Cai
 */
public class IntroduceCommand extends HasSubcommands
{
	public static final String USER = "user";
	public static final String UPDATE = "update";
	public static final String DELETE = "delete";

	public IntroduceCommand()
	{
		super(3);

		subcommands.put("user", event ->
		{
			User user = event.getUser();
			User target = event.getOption("user", user, OptionMapping::getAsUser); //沒有填 預設是自己

			String introduction = MembersHandle.getIntroduction(target.getIdLong());
			event.reply(introduction.isEmpty() ? JsonHandle.getString(user.getIdLong(), "introduce.user.no_info") : introduction)
					.setEphemeral(true)
					.queue();
		});
		subcommands.put("update", event ->
		{
			long userID = event.getUser().getIdLong();
			TextInput newIntroductionInput = TextInput.create(UpdateIntroduceModal.NEW_INTRODUCTION_TEXT, JsonHandle.getString(userID, "introduce.update.new_introduction"), TextInputStyle.PARAGRAPH)
					.setRequiredRange(0, Message.MAX_CONTENT_LENGTH)
					.setValue(MembersHandle.getIntroduction(userID))
					.build();
			event.replyModal(Modal.create(IModal.UPDATE_INTRODUCE_ID, JsonHandle.getString(userID, "introduce.update.title"))
					.addComponents(ActionRow.of(newIntroductionInput))
					.build()).queue(); //如果Modal可以事先建好就好了
		});
		subcommands.put("delete", event ->
		{
			long userID = event.getUser().getIdLong();
			event.reply(JsonHandle.getString(userID, "introduce.delete.success")).queue();
			MembersHandle.updateIntroduction(userID, ""); //刪除自我介紹
		});
	}
}