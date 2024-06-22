package cartoland.events;

import cartoland.utilities.QuestionForumHandle;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateArchivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * {@code ThreadEvent} is a listener that triggers when a user create a thread or a thread archived. For now, this only
 * affect Question forum post. This class was registered in {@link cartoland.Cartoland#main(String[])}, with the build
 * of JDA.
 *
 * @since 1.5
 * @author Alex Cai
 */
public class ThreadEvent extends ListenerAdapter
{
	@Override
	public void onChannelCreate(ChannelCreateEvent event)
	{
		if (!event.getChannelType().isThread()) //如果不是討論串
			return; //bye have a great time

		ThreadChannel threadChannel = (ThreadChannel) event.getChannel();
		threadChannel.join().queue(); //加入討論串

		if (QuestionForumHandle.isQuestionPost(threadChannel))
			QuestionForumHandle.getInstance(threadChannel).createEvent(); //疑難雜症新增時傳送指南
	}

	@Override
	public void onChannelUpdateArchived(ChannelUpdateArchivedEvent event)
	{
		if (event.getChannel() instanceof ThreadChannel thread && QuestionForumHandle.isQuestionPost(thread) && !thread.isArchived())
			QuestionForumHandle.getInstance(thread).postWakeUpEvent(); //疑難雜症從關閉變成開啟時的事件
	}
}