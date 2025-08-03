package cartoland.contexts;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class CodeBlockContext implements IContext
{
	@Override
	public void contextProcess(MessageContextInteractionEvent event)
	{
		String rawContent = event.getTarget().getContentRaw();
		int contentLength = rawContent.length();
		final int maxLength = Message.MAX_CONTENT_LENGTH - 8;
		if (contentLength <= maxLength) //因為前後要加```\n和\n``` 因此以1992為界線
		{
			event.reply("```\n" + rawContent + "\n```").queue();
			return;
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
}