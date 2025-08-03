package cartoland.events;

import cartoland.contexts.*;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@code ContextMenu} is a listener that triggers when a user uses right click command. This class was registered
 * in {@link cartoland.Cartoland#main(String[])}, with the build of JDA.
 *
 * @since 1.5
 * @author Alex Cai
 */
public class ContextMenu extends ListenerAdapter
{
	private static final Logger logger = LoggerFactory.getLogger(ContextMenu.class);

	public static final String RAW_TEXT = "Raw Text";
	public static final String REACTIONS = "Reactions";
	public static final String CODE_BLOCK = "Code Block";
	public static final String QUOTE_ = "Quote";
	public static final String PIN = "Pin";

	private final Map<String, IContext> menuMap = new HashMap<>();

	public ContextMenu()
	{
		menuMap.put(RAW_TEXT, event ->
		{
			byte[] fullData = event.getTarget().getContentRaw().getBytes(StandardCharsets.UTF_8);
			event.replyFiles(FileUpload.fromData(fullData, "message.txt"))
					.setEphemeral(true)
					.queue();
		});
		menuMap.put(REACTIONS, event ->
		{
			List<MessageReaction> reactions = event.getTarget().getReactions();
			if (reactions.isEmpty())
			{
				event.reply("There's no any reactions").setEphemeral(true).queue();
				return;
			}

			StringBuilder reactionsBuilder = new StringBuilder();
			for (MessageReaction reaction : reactions)
				reactionsBuilder.append(reaction.getEmoji().getFormatted()).append(" × ").append(reaction.getCount()).append('\t');
			event.reply(reactionsBuilder.toString()).setEphemeral(true).queue();
		});
		menuMap.put(CODE_BLOCK, new CodeBlockContext());
		menuMap.put(QUOTE_, new QuoteContext());
		menuMap.put(PIN, new PinContext());
	}

	@Override
	public void onMessageContextInteraction(MessageContextInteractionEvent event)
	{
		String eventName = event.getName();
		menuMap.get(eventName).contextProcess(event);

		User user = event.getUser();
		logger.info("{}({}) used {}", user.getEffectiveName(), user.getId(), eventName);
	}
}