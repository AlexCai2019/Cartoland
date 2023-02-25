package cartoland.events;

import cartoland.utilities.IDAndEntities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;

public class OpenFormPost extends ListenerAdapter
{
	@Override
	public void onChannelCreate(@NotNull ChannelCreateEvent event)
	{
		if (!event.getChannelType().isThread())
			return;

		ThreadChannel forumPost = event.getChannel().asThreadChannel();
		if (forumPost.getParentChannel().getIdLong() != IDAndEntities.QUESTIONS_CHANNEL_ID)
			return;

		EmbedBuilder builder = new EmbedBuilder()
				.setTitle("**-=發問指南=-**", null)
				.setDescription("""
								┌ 請清楚說明你想做什麼，並想要什麼結果
								┌ 請提及你正在使用的Minecraft版本，以及是否正在使用任何模組
								┌ 發文時請務必附上貼文標籤
								""")
				.setColor(new Color(133, 201, 103));

		forumPost.addThreadMember(IDAndEntities.botItself).queue();
		forumPost.sendMessageEmbeds(builder.build()).queue();

		//TODO: figure out how to manage tags of a forum post
		//forumPost.getManager().setAppliedTags(ForumTag.fromId(1))
	}
}