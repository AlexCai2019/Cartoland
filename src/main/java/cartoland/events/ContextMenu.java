package cartoland.events;

import cartoland.commands.QuoteCommand;
import cartoland.utilities.FileHandle;
import cartoland.utilities.IDs;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.utils.FileUpload;

import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * {@code ContextMenu} is a listener that triggers when a user uses right click command. This class was registered
 * in {@link cartoland.Cartoland#main(String[])}, with the build of JDA.
 *
 * @since 1.5
 * @author Alex Cai
 */
public class ContextMenu extends ListenerAdapter
{
	public static final String RAW_TEXT = "Raw Text";
	public static final String REACTIONS = "Reactions";
	public static final String CODE_BLOCK = "Code Block";
	public static final String QUOTE_ = "Quote";
	public static final String PIN = "Pin";

	@Override
	public void onMessageContextInteraction(MessageContextInteractionEvent event)
	{
		User user = event.getUser();
		long userID = user.getIdLong();
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

				event.reply(reactions.isEmpty() ? "There's no any reactions" : reactions)
						.setEphemeral(true)
						.queue();
			}

			case CODE_BLOCK ->
			{
				String rawContent = event.getTarget().getContentRaw();
				int contentLength = rawContent.length();
				final int maxLength = Message.MAX_CONTENT_LENGTH - 8;
				if (contentLength <= maxLength) //因為前後要加```\n和\n``` 因此以1992為界線
				{
					event.reply("```\n" + rawContent + "\n```").queue();
					break;
				}

				//先回覆前1992個字 以及格式
				event.reply("```\n" + rawContent.substring(0, maxLength) + "\n```")
					.flatMap(InteractionHook::retrieveOriginal)
					.queue(message ->
					{
						if (contentLength <= maxLength + maxLength) //如果從[1992] = 第1993個字開始算起 長度不超過1992個字
							message.reply("```\n" + rawContent.substring(maxLength) + "\n```").mentionRepliedUser(false).queue();
						else
							message.reply("```\n" + rawContent.substring(maxLength, maxLength + maxLength) + "\n```").mentionRepliedUser(false)
								.queue(message1 -> message1.reply("```\n" + rawContent.substring(maxLength + maxLength) + "\n```").mentionRepliedUser(false).queue());
					});
			}

			case QUOTE_ ->
			{
				event.deferReply().queue(); //延後回覆
				QuoteCommand.quoteMessage(event, event.getChannel(), event.getTarget());
			}

			case PIN ->
			{
				Member member = event.getMember();
				if (member == null)
				{
					event.reply("You should use this function in a server!").queue();
					return;
				}

				Message target = event.getTarget();
				boolean isDiscussPostOwner = //如果是地圖專版的論壇貼文的開啟者 可以無視權限直接釘選
					event.getChannel() instanceof ThreadChannel thread && //是討論串
					thread.getParentChannel().getIdLong() == IDs.MAP_DISCUSS_CHANNEL_ID && //是地圖專版
					thread.getOwnerIdLong() == userID; //是開啟者

				if (!isDiscussPostOwner && !member.hasPermission(Permission.MESSAGE_MANAGE)) //如果不是地圖專版貼文不是開啟者且沒有權限
				{
					event.reply("You don't have the permission to pin this message!").setEphemeral(true).queue();
					break;
				}

				if (target.isPinned())
				{
					event.reply("Unpinned message.").queue();
					target.unpin().queue(); //解釘
				}
				else
				{
					event.reply("Pinned message.").queue();
					target.pin().queue(); //釘選
				}
			}
		}

		FileHandle.log(user.getEffectiveName(), '(', userID, ") used ", eventName);
	}
}