package cartoland.utilities;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.function.Function;

/**
 * Common lambda functions for use. Including method references in {@link OptionMapping},
 * {@link Message.Attachment} and {@link String}.
 *
 * @since 2.0
 * @author Alex Cai
 */
public class CommonFunctions
{
	private CommonFunctions()
	{
		throw new AssertionError(IDAndEntities.YOU_SHALL_NOT_ACCESS);
	}

	public static final Function<OptionMapping, Integer> getAsInt = OptionMapping::getAsInt;
	public static final Function<OptionMapping, String> getAsString = OptionMapping::getAsString;
	public static final Function<OptionMapping, User> getAsUser = OptionMapping::getAsUser;
	public static final Function<OptionMapping, Boolean> getAsBoolean = OptionMapping::getAsBoolean;
	public static final Function<Message.Attachment, String> getUrl = Message.Attachment::getUrl;
	public static final Function<Object, String> stringValue = String::valueOf;
}