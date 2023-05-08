package cartoland.utilities;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.function.Function;

public class OptionFunctions
{
	private OptionFunctions()
	{
		throw new AssertionError(IDAndEntities.YOU_SHALL_NOT_ACCESS);
	}

	public static final Function<OptionMapping, Integer> getAsInt = OptionMapping::getAsInt;
	public static final Function<OptionMapping, String> getAsString = OptionMapping::getAsString;
	public static final Function<OptionMapping, User> getAsUser = OptionMapping::getAsUser;
}
