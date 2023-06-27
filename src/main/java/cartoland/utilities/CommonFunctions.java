package cartoland.utilities;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.function.Function;

/**
 * Common lambda functions for use. Including method references in {@link OptionMapping},
 * {@link Message.Attachment} and {@link String}. Can not be instantiated.
 *
 * @since 2.0
 * @author Alex Cai
 */
public interface CommonFunctions
{
	Function<OptionMapping, Integer> getAsInt = OptionMapping::getAsInt;
	Function<OptionMapping, String> getAsString = OptionMapping::getAsString;
	Function<OptionMapping, User> getAsUser = OptionMapping::getAsUser;
	Function<OptionMapping, Boolean> getAsBoolean = OptionMapping::getAsBoolean;
	Function<Message.Attachment, String> getUrl = Message.Attachment::getUrl;
	Function<Object, String> stringValue = String::valueOf;
}