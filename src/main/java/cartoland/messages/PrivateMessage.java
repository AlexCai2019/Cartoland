package cartoland.messages;

import cartoland.utilities.AnonymousHandle;
import cartoland.utilities.ReturnResult;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.sticker.StickerItem;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * {@code PrivateMessage} is a listener that triggers when a user types anything in the direct message to the bot. This
 * class is in an array in {@link cartoland.events.MessageEvent}.
 *
 * @since 1.0
 * @author Alex Cai
 */
public class PrivateMessage implements IMessage
{
	private static final Logger logger = LoggerFactory.getLogger(PrivateMessage.class);

	@Override
	public boolean messageCondition(MessageReceivedEvent event)
	{
		return !event.isFromGuild();
	}

	@Override
	public void messageProcess(MessageReceivedEvent event)
	{
		Message message = event.getMessage(); //訊息
		User author = event.getAuthor(); //訊息傳送者
		if (author.isBot() || author.isSystem()) //是機器人或系統
			return; //忽略

		ReturnResult<TextChannel> validChannel = AnonymousHandle.checkMemberValid(author.getIdLong()); //檢查該ID的使用者有沒有權限
		if (!validChannel.isSuccess()) //有錯誤訊息
		{
			message.reply(validChannel.getError()).mentionRepliedUser(false).queue(); //回覆錯誤訊息
			return;
		}

		MessageCreateBuilder messageBuilder = MessageCreateBuilder.fromMessage(message); //訊息本文
		List<Message.Attachment> attachments = message.getAttachments(); //訊息附件
		List<StickerItem> stickers = message.getStickers();
		if (!attachments.isEmpty() || !stickers.isEmpty()) //有附件或貼圖
		{
			List<FileUpload> files = new ArrayList<>(); //要上傳的檔案

			for (Message.Attachment attachment : attachments) //附件
			{
				files.add(FileUpload.fromStreamSupplier(attachment.getFileName(), () ->
				{
					try
					{
						return attachment.getProxy().download().get(); //下載圖片
					}
					catch (InterruptedException | ExecutionException e)
					{
						return InputStream.nullInputStream();
					}
				}));
			}

			for (StickerItem sticker : stickers) //貼圖
			{
				//貼圖名.副檔名
				files.add(FileUpload.fromStreamSupplier(sticker.getName() + '.' + sticker.getFormatType().getExtension(), () ->
				{
					try
					{
						return sticker.getIcon().download().get(); //貼圖轉圖片
					}
					catch (InterruptedException | ExecutionException e)
					{
						return InputStream.nullInputStream();
					}
				}));
			}

			messageBuilder.addFiles(files);
		}

		if (messageBuilder.isEmpty())
			return; //是空的就算了

		validChannel.getValue().sendMessage(messageBuilder.build()) //私訊轉到地下聊天室
				.queue(undergroundMessage -> AnonymousHandle.addConnection(message.getIdLong(), undergroundMessage.getIdLong()));
		logger.info("{} {} dm {}", author.getName(), author.getId(), message.getContentRaw());
	}
}