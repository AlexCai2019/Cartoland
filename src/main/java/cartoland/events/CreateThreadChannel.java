package cartoland.events;

import cartoland.utilities.IDAndEntities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code CreateThreadChannel} is a listener that triggers when a user create a forum post. This class was
 * registered in {@link cartoland.Cartoland#main}, with the build of JDA.
 *
 * @since 1.5
 * @author Alex Cai
 */
public class CreateThreadChannel extends ListenerAdapter
{
	private final MessageEmbed startEmbed = new EmbedBuilder()
			.setTitle("**-=發問指南=-**", "https://discord.com/channels/886936474723950603/1079081061658673253/1079081061658673253")
			.setDescription("""
							-=發問指南=-
														
							• 請清楚說明你想做什麼，並想要什麼結果
							• 請提及你正在使用的Minecraft版本，以及是否正在使用任何模組
							• 討論完成後，使用`:resolved:` <:resolved:1081082902785314921>表情符號關閉貼文
														
							-=Guidelines=-
							       
							• Ask your question straight and clearly, tell us what you are trying to do.
							• Mention which Minecraft version you are using and any mods.
							• Remember to use `:resolved:` <:resolved:1081082902785314921> to close the post after resolved.
							""")
			.setColor(new Color(133, 201, 103)) //創聯的綠色
			.build();

	@Override
	public void onChannelCreate(@NotNull ChannelCreateEvent event)
	{
		if (!event.getChannelType().isThread())
			return;

		ThreadChannel threadChannel = event.getChannel().asThreadChannel();
		threadChannel.join().queue(); //加入討論串

		//這以下是關於問題論壇
		if (threadChannel.getParentChannel().getIdLong() != IDAndEntities.QUESTIONS_CHANNEL_ID)
			return;

		threadChannel.sendMessageEmbeds(startEmbed).queue(); //傳送發問指南

		List<ForumTag> tags = new ArrayList<>(threadChannel.getAppliedTags());
		tags.remove(IDAndEntities.resolvedForumTag); //避免使用者自己加resolved
		if (!tags.contains(IDAndEntities.unresolvedForumTag)) //如果使用者自己沒有加unresolved
		{
			if (tags.size() == 5) //不可以超過5個tag
				tags.remove(4); //移除最後一個 空出位置給unresolved
			tags.add(IDAndEntities.unresolvedForumTag);
		}
		threadChannel.getManager().setAppliedTags(tags).queue();
	}
}