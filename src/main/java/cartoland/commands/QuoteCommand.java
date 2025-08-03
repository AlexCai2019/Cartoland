package cartoland.commands;

import cartoland.methods.IAnalyzeCTLCLink;
import cartoland.methods.IQuotable;
import cartoland.utilities.JsonHandle;
import cartoland.utilities.RegularExpressions;
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
		if (RegularExpressions.CARTOLAND_MESSAGE_LINK_REGEX.matcher(link).matches()) //必須是有效的創聯訊息連結
		{
			event.deferReply().queue(); //延後回覆
			analyze(event, link);
		}
		else
			event.reply(JsonHandle.getString(event.getUser().getIdLong(), "quote.invalid_link")).setEphemeral(true).queue();
	}

	@Override
	public void whenSuccess(IReplyCallback event, Message message)
	{
		User user = event.getUser();
		long userID = user.getIdLong();
		List<MessageEmbed> embeds = quoteMessage(message);
		SlashCommandInteractionEvent commandEvent = (SlashCommandInteractionEvent) event;
		boolean isMentionAuthor = commandEvent.getOption("mention_author", Boolean.FALSE, OptionMapping::getAsBoolean);

		//提及訊息作者 vs 不提及訊息作者
		WebhookMessageCreateAction<Message> messageAction;
		if (isMentionAuthor) //要tag訊息作者
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

	@Override
	public void whenFail(IReplyCallback event, String link, int failCode)
	{
		String jsonKey = switch (failCode)
		{
			case NO_GUILD -> "quote.invalid_link";
			case NO_CHANNEL -> "quote.no_channel";
			default -> "quote.no_message";
		};
		event.getHook().sendMessage(JsonHandle.getString(event.getUser().getIdLong(), jsonKey)).setEphemeral(true).queue(); //回覆
	}
}