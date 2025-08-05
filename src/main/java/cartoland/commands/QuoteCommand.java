package cartoland.commands;

import cartoland.methods.IAnalyzeCTLCLink;
import cartoland.methods.IQuotable;
import cartoland.utilities.JsonHandle;
import cartoland.utilities.ReturnResult;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;

import java.util.List;

/**
 * {@code QuoteCommand} is an execution when a user uses /transfer command. This class implements {@link ICommand}
 * interface, which is for the commands HashMap in {@link cartoland.events.CommandUsage}.
 *
 * @since 1.6
 * @author Alex Cai
 */
public class QuoteCommand implements ICommand, IAnalyzeCTLCLink, IQuotable
{
	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		String link = event.getOption("link", "", OptionMapping::getAsString);
		event.deferReply().queue(); //延後回覆
		analyze(event, link);
	}

	@Override
	public void afterAnalyze(IReplyCallback event, String link, ReturnResult<Message> result)
	{
		User user = event.getUser();
		long userID = user.getIdLong();
		if (!result.isSuccess()) //如果不成功
		{
			event.getHook()
				.sendMessage(JsonHandle.getString(userID, "quote." + result.getError()))
				.setEphemeral(true)
				.queue();
			return;
		}

		//以下是成功了的時候
		Message message = result.getValue();
		List<MessageEmbed> embeds = quoteMessage(message);
		SlashCommandInteractionEvent commandEvent = (SlashCommandInteractionEvent) event;

		//提及訊息作者 vs 不提及訊息作者
		WebhookMessageCreateAction<Message> messageAction;
		if (commandEvent.getOption("mention_author", Boolean.FALSE, OptionMapping::getAsBoolean)) //要tag訊息作者
		{
			String mention = message.getAuthor().getAsMention();
			messageAction = event.getHook()
					.sendMessage(JsonHandle.getString(userID, "quote.mention", user.getEffectiveName(), mention))
					.addEmbeds(embeds);
		}
		else
			messageAction = event.getHook().sendMessageEmbeds(embeds);
		messageAction.addComponents(ActionRow.of(Button.link(message.getJumpUrl(), JsonHandle.getString(userID, "quote.jump_message")))).queue();
	}
}