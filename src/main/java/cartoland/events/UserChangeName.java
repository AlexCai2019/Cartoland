package cartoland.events;

import cartoland.utilities.CommandBlocksHandle;
import cartoland.utilities.IDAndEntities;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * {@code UserChangeName} is a listener that triggers when a user changed his/her name. This class was registered in
 * {@link cartoland.Cartoland#main}, with the build of JDA.
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
		IDAndEntities.idAndNames.replace(user.getIdLong(), user.getEffectiveName());
		CommandBlocksHandle.changed = true;
	}
}