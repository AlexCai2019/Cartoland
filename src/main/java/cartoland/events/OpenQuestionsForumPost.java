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
 * {@code OpenQuestionsForumPost} is a listener that triggers when a user create a forum post. This class was
 * registered in {@link cartoland.Cartoland#main}, with the build of JDA.
 *
 * @since 1.5
 * @author Alex Cai
 */
public class OpenQuestionsForumPost extends ListenerAdapter
{
	private final MessageEmbed startEmbed = new EmbedBuilder()
			.setTitle("**-=發問指南=-**", null)
			.setDescription("""
							-=發問指南=-
														
							• 請清楚說明你想做什麼，並想要什麼結果
							• 請提及你正在使用的Minecraft版本，以及是否正在使用任何模組
							• 討論完成後，使用✅表情符號關閉貼文
														
							-=Guidelines=-
							       
							• Ask your question straight and clearly, tell us what you are trying to do.
							• Mention which Minecraft version you are using and any mods.
							• Remember to use ✅ to close the post after resolved.
							""")
			.setColor(new Color(133, 201, 103))
			.build();

	@Override
	public void onChannelCreate(@NotNull ChannelCreateEvent event)
	{
		if (!event.getChannelType().isThread())
			return;

		ThreadChannel forumPost = event.getChannel().asThreadChannel();
		if (forumPost.getParentChannel().getIdLong() != IDAndEntities.QUESTIONS_CHANNEL_ID)
			return;

		forumPost.addThreadMember(IDAndEntities.botItself).queue();
		forumPost.sendMessageEmbeds(startEmbed).queue();

		List<ForumTag> tags = new ArrayList<>(forumPost.getAppliedTags());
		tags.remove(IDAndEntities.resolvedForumTag);
		if (!tags.contains(IDAndEntities.unresolvedForumTag))
		{
			if (tags.size() == 5) //不可以超過5個tag
				tags.remove(4);
			tags.add(IDAndEntities.unresolvedForumTag);
		}
		forumPost.getManager().setAppliedTags(tags).queue();
	}
}