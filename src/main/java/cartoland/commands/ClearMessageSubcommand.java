package cartoland.commands;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import cartoland.utilities.JsonHandle;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.ErrorResponse;

/**
 * {@code ClearMessageSubcommand} is an execution when a user uses /admin clear_message command.
 * This class implements {@link ICommand} interface, which is for the commands HashMap in 
 * {@link cartoland.events.CommandUsage}.
 *
 * @since 2.5
 * @author Alex Cai
 */
public class ClearMessageSubcommand implements ICommand
{
	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		User user = event.getUser();
		long userID = user.getIdLong();
		MessageChannel channel = event.getChannel();
		
		// 獲取要刪除的訊息數量參數
		int number = event.getOption("number", 1, OptionMapping::getAsInt);
		
		// 驗證數量範圍
		if (number < 1 || number > 100)
		{
			event.reply(JsonHandle.getString(userID, "clear_message.invalid_number")).setEphemeral(true).queue();
			return;
		}
		
		event.deferReply(true).queue(); // 延後回覆，設為隱私回覆
		
		// 取得頻道最近的訊息
		channel.getHistory().retrievePast(100).queue(messages -> {
			// 過濾出使用者自己的訊息
			List<Message> userMessages = messages.stream()
				.filter(msg -> msg.getAuthor().getIdLong() == userID)
				.limit(number)
				.toList();
			
			if (userMessages.isEmpty())
			{
				event.getHook().sendMessage(JsonHandle.getString(userID, "clear_message.no_messages")).queue();
				return;
			}
			
			// 計算成功刪除的訊息數量
			AtomicInteger deletedCount = new AtomicInteger(0);
			AtomicInteger processedCount = new AtomicInteger(0);
			int totalMessages = userMessages.size();
			
			// 逐一刪除訊息
			for (Message message : userMessages)
			{
				message.delete().queue(
					success -> {
						deletedCount.incrementAndGet();
						checkAndReply(event, deletedCount.get(), processedCount.incrementAndGet(), totalMessages, userID);
					},
					new ErrorHandler()
						.handle(ErrorResponse.UNKNOWN_MESSAGE, e -> {
							// 訊息已被刪除或不存在
							checkAndReply(event, deletedCount.get(), processedCount.incrementAndGet(), totalMessages, userID);
						})
						.handle(ErrorResponse.MISSING_PERMISSIONS, e -> {
							// 沒有權限刪除訊息
							checkAndReply(event, deletedCount.get(), processedCount.incrementAndGet(), totalMessages, userID);
						})
						.handle(ErrorResponse.MISSING_ACCESS, e -> {
							// 沒有存取權限
							checkAndReply(event, deletedCount.get(), processedCount.incrementAndGet(), totalMessages, userID);
						})
				);
			}
		}, failure -> {
			event.getHook().sendMessage(JsonHandle.getString(userID, "clear_message.fetch_error")).queue();
		});
	}
	
	/**
	 * 檢查是否所有訊息都已處理完成，並回覆結果
	 */
	private void checkAndReply(SlashCommandInteractionEvent event, int deletedCount, int processedCount, int totalMessages, long userID)
	{
		if (processedCount == totalMessages)
		{
			// 所有訊息都已處理完成
			if (deletedCount == 0)
			{
				event.getHook().sendMessage(JsonHandle.getString(userID, "clear_message.no_messages_deleted")).queue();
			}
			else
			{
				String message = JsonHandle.getString(userID, "clear_message.success")
					.replace("{count}", String.valueOf(deletedCount));
				event.getHook().sendMessage(message).queue();
			}
		}
	}
}
