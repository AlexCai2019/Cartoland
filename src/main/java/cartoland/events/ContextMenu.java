package cartoland.events;

import cartoland.utilities.FileHandle;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import java.util.stream.Collector;
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
	private final Function<MessageReaction, String> reactionToString =
			reaction -> reaction.getEmoji().getFormatted() + " × " + reaction.getCount();
	private final Collector<CharSequence, ?, String> toReactionListString = Collectors.joining("\t");

	@Override
	public void onMessageContextInteraction(MessageContextInteractionEvent event)
	{
		User user = event.getUser();
		String eventName = event.getName();

		switch (eventName)
		{
			case "Raw Text" ->
					event.replyFiles(FileUpload.fromData(event.getTarget().getContentRaw().getBytes(StandardCharsets.UTF_8), "message.txt"))
							.setEphemeral(true)
							.queue();

			case "Reactions" ->
			{
				String reactions = event.getTarget()
						.getReactions()
						.stream()
						.map(reactionToString)
						.collect(toReactionListString);

				event.reply(reactions.length() > 0 ? reactions : "There's no any reactions")
						.setEphemeral(true)
						.queue();
			}
		}

		FileHandle.log(user.getName() + "(" + user.getIdLong() + ") used " + eventName);
	}
}