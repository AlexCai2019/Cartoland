package cartoland.commands;

import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.List;

/**
 * {@code ClearMessageCommand} is an execution when a user uses /clear_message command.
 * This class implements {@link ICommand} interface, which is for the commands HashMap in 
 * {@link cartoland.events.CommandUsage}.
 *
 * @since 2.5
 * @author Alex Cai
 */
public class ClearMessageCommand implements ICommand
{
	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		User user = event.getUser();
		long userID = user.getIdLong();

		Member member = event.getMember();
		if (member == null) //找不到成員 說明不是在群組裡
		{
			event.reply(JsonHandle.getString(userID, "clear_message.wrong_place")).setEphemeral(true).queue();
			return;
		}

		GuildMessageChannel channel = event.getGuildChannel(); //獲取頻道
		int number = event.getOption("number", 1, OptionMapping::getAsInt); //要刪除的訊息數量
		User target = event.getOption("target", OptionMapping::getAsUser); //要刪除的訊息的發送者

		if (target == null) //沒有指定目標
		{
			if (member.hasPermission(Permission.MESSAGE_MANAGE)) //有管理權限
			{
				event.reply(JsonHandle.getString(userID, "clear_message.success", number)).queue(); //趕快回覆避免超過3秒限制
				channel.getIterableHistory()
						.limit(number)
						.queue(messages -> clearMessages(channel, messages));
			}
			else //沒有權限
				event.reply(JsonHandle.getString(userID, "clear_message.no_permission")).setEphemeral(true).queue();
			return;
		}

		boolean isSelf = user.equals(target);
		if (isSelf || member.hasPermission(Permission.MESSAGE_MANAGE)) //要刪除自己的訊息 或是有權限
		{
			List<Message> messages = channel.getIterableHistory()
					.stream()
					.filter(message -> message.getAuthor().equals(target))
					.limit(number)
					.toList();
			event.reply(JsonHandle.getString(userID, "clear_message.success_with_user", target.getEffectiveName(), messages.size()))
				.setEphemeral(isSelf)
				.queue();
			clearMessages(channel, messages);
		}
		else
			event.reply(JsonHandle.getString(userID, "clear_message.no_permission")).setEphemeral(true).queue(); //沒有權限
	}

	private void clearMessages(GuildMessageChannel channel, List<Message> messages)
	{
		if (messages.isEmpty()) //空的就算了
			return;

		if (messages.size() == 1) //只有一則
			channel.deleteMessageById(messages.getFirst().getIdLong()).queue(); //那麼必須使用deleteMessageById
		else //兩則或以上
			channel.deleteMessages(messages).queue(); //可以用批次
	}
}