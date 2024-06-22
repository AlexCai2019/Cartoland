package cartoland.events;

import cartoland.utilities.forums.ForumsHandle;
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

		ForumsHandle.getHandle(threadChannel).createEvent(event); //是地圖專版或疑難雜症就處理事件
	}

	@Override
	public void onChannelUpdateArchived(ChannelUpdateArchivedEvent event)
	{
		ForumsHandle handle = ForumsHandle.getHandle((ThreadChannel) event.getChannel()); //是地圖專版或疑難雜症就處理事件
		if (Boolean.TRUE.equals(event.getNewValue())) //變成關閉
			handle.postSleepEvent(event);
		else //變成開啟
			handle.postWakeUpEvent(event);
	}
}