package cartoland.events;

import cartoland.utilities.FileHandle;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
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

			case CODE_BLOCK -> event.reply("```\n" + event.getTarget().getContentRaw() + "\n```").queue();
		}

		FileHandle.log(user.getEffectiveName() + "(" + user.getIdLong() + ") used " + eventName);
	}
}