package cartoland.methods;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.List;

public interface IQuotable
{
	default List<MessageEmbed> quoteMessage(Message message)
	{
		User author = message.getAuthor(); //連結訊息的發送者
		String messageTitle = author.getEffectiveName();
		String messageLink = message.getJumpUrl();
		List<MessageEmbed> embeds = new ArrayList<>(); //要被送出的所有embed們
		EmbedBuilder messageEmbed = new EmbedBuilder()
				.setTitle(messageTitle, messageLink)
				.setAuthor(author.getName(), null, author.getEffectiveAvatarUrl())
				.appendDescription(message.getContentRaw()) //訊息的內容
				.setTimestamp(message.getTimeCreated()) //連結訊息的發送時間
				.setFooter(message.getChannel().getName(), null); //訊息的發送頻道

		List<Message.Attachment> attachments = message.getAttachments(); //訊息的附件

		List<Message.Attachment> images;
		if (attachments.isEmpty() || (images = attachments.stream().filter(Message.Attachment::isImage).toList()).isEmpty()) //沒有任何附件或圖片
			embeds.add(messageEmbed.build());
		else //有圖片
		{
			embeds.add(messageEmbed.setImage(images.getFirst().getUrl()).build()); //第一個要放訊息embed
			for (int i = 1, size = Math.min(images.size(), Message.MAX_EMBED_COUNT); i < size; i++) //剩下的要開新embed, 注意總數不能超過10個
				embeds.add(new EmbedBuilder().setTitle(messageTitle, messageLink).setImage(images.get(i).getUrl()).build());
		}
		return embeds;
	}
}