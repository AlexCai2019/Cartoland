package cartoland.events;

import cartoland.utilities.FileHandle;
import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.utils.FileUpload;

import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * {@code ContextMenu} is a listener that triggers when a user uses right click command. This class was registered
 * in {@link cartoland.Cartoland#main}, with the build of JDA.
 *
 * @since 1.5
 * @author Alex Cai
 */
public class ContextMenu extends ListenerAdapter
{
	public static final String RAW_TEXT = "Raw Text";
	public static final String REACTIONS = "Reactions";
	public static final String CODE_BLOCK = "Code Block";

	@Override
	public void onMessageContextInteraction(MessageContextInteractionEvent event)
	{
		User user = event.getUser();
		String eventName = event.getName();

		switch (eventName)
		{
			case RAW_TEXT ->
					event.replyFiles(FileUpload.fromData(event.getTarget().getContentRaw().getBytes(StandardCharsets.UTF_8), "message.txt"))
							.setEphemeral(true)
							.queue();

			case REACTIONS ->
			{
				String reactions = event.getTarget()
						.getReactions()
						.stream()
						.map(reaction -> reaction.getEmoji().getFormatted() + " × " + reaction.getCount())
						.collect(Collectors.joining("\t"));

				event.reply(reactions.length() > 0 ? reactions : "There's no any reactions")
						.setEphemeral(true)
						.queue();
			}

			case CODE_BLOCK ->
			{
				String rawContent = event.getTarget().getContentRaw();
				int contentLength = rawContent.length();
				if (contentLength <= 1992) //因為前後要加```\n和\n``` 因此以1992為界線
				{
					event.reply("```\n" + rawContent + "\n```").queue();
					return;
				}

				//先回覆前1992個字 以及格式
				event.reply("```\n" + rawContent.substring(0, 1992) + "\n```").queue(interactionHook ->
					interactionHook.retrieveOriginal().queue(message ->
					{
						if (contentLength <= 1992 + 1992) //如果從[1992] = 第1993個字開始算起 長度不超過1992個字
							message.reply("```\n" + rawContent.substring(1992) + "\n```").mentionRepliedUser(false).queue();
						else
							message.reply("```\n" + rawContent.substring(1992, 1992 + 1992) + "\n```").mentionRepliedUser(false)
								.queue(message1 -> message1.reply("```\n" + rawContent.substring(1992 + 1992) + "\n```").mentionRepliedUser(false).queue());
					}));
			}
		}

		FileHandle.log(user.getEffectiveName() + "(" + user.getIdLong() + ") used " + eventName);
	}

	@Override
	public void onUserContextInteraction(UserContextInteractionEvent event)
	{
		long userID = event.getUser().getIdLong();
		String eventName = event.getName();
		Member target = event.getTargetMember();
		if (target == null)
		{
			event.reply("something went wrong...").queue();
			return;
		}

		switch (eventName)
		{
			case "mute" ->
			{
				TextInput targetInput = TextInput.create("target", Long.toUnsignedString(target.getIdLong()), TextInputStyle.SHORT).build();
				TextInput durationInput = TextInput.create("duration", JsonHandle.getStringFromJsonKey(userID, "admin.mute.duration_text"), TextInputStyle.SHORT)
						.setRequiredRange(0, 100)
						.build();
				StringSelectMenu unitSelect = StringSelectMenu.create("unit")
						.addOptions(
								SelectOption.of(JsonHandle.getStringFromJsonKey(userID, "admin.mute.unit_second_text"), "second"),
								SelectOption.of(JsonHandle.getStringFromJsonKey(userID, "admin.mute.unit_minute_text"), "minute"),
								SelectOption.of(JsonHandle.getStringFromJsonKey(userID, "admin.mute.unit_hour_text"), "hour"),
								SelectOption.of(JsonHandle.getStringFromJsonKey(userID, "admin.mute.unit_double_hour_text"), "double_hour"),
								SelectOption.of(JsonHandle.getStringFromJsonKey(userID, "admin.mute.unit_day_text"), "day"),
								SelectOption.of(JsonHandle.getStringFromJsonKey(userID, "admin.mute.unit_week_text"), "week"))
						.build();
				TextInput reasonInput = TextInput.create("reason", JsonHandle.getStringFromJsonKey(userID, "admin.mute.reason_text"), TextInputStyle.SHORT).setRequiredRange(0, 100).setRequired(false).build();
				event.replyModal(
						Modal.create(ReceiveModal.MUTE_MEMBER_MODAL_ID, JsonHandle.getStringFromJsonKey(userID, "admin.mute.target").formatted(target.getUser().getEffectiveName()))
								.addActionRow(targetInput)
								.addActionRow(durationInput)
								.addActionRow(unitSelect)
								.addActionRow(reasonInput)
								.build()).queue();
			}

			case "temp_ban" ->
			{
			}
		}
	}
}