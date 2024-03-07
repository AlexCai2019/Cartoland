package cartoland.events;

import cartoland.utilities.CommandBlocksHandle;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * {@code UserChangeName} is a listener that triggers when a user changed his/her name. This class was registered in
 * {@link cartoland.Cartoland#main(String[])}, with the build of JDA.
 *
 * @since 1.6
 * @author Alex Cai
 */
public class UserChangeName extends ListenerAdapter
{
	@Override
	public void onUserUpdateName(UserUpdateNameEvent event)
	{
		User user = event.getUser();
		CommandBlocksHandle.getLotteryData(user.getIdLong()).setName(user.getEffectiveName()); //修改紀錄內的名字
		CommandBlocksHandle.changed = true; //下次用/lottery ranking的時候要重新排序
	}
}