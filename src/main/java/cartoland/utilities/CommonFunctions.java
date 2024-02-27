package cartoland.utilities;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.function.Function;

/**
 * Common lambda functions for use. Including method references in {@link OptionMapping},
 * {@link Message.Attachment} and {@link String}. Can not be instantiated or inherited.
 *
 * @since 2.0
 * @author Alex Cai
 */
public final class CommonFunctions
{
	private CommonFunctions()
	{
		throw new AssertionError(IDs.YOU_SHALL_NOT_ACCESS);
	}

	public static final Function<OptionMapping, Boolean> getAsBoolean = OptionMapping::getAsBoolean;
	public static final Function<OptionMapping, Integer> getAsInt = OptionMapping::getAsInt;
	public static final Function<OptionMapping, Double> getAsDouble = OptionMapping::getAsDouble;
	public static final Function<OptionMapping, String> getAsString = OptionMapping::getAsString;
	public static final Function<OptionMapping, User> getAsUser = OptionMapping::getAsUser;
	public static final Function<OptionMapping, Member> getAsMember = OptionMapping::getAsMember;
	public static final Function<OptionMapping, GuildChannelUnion> getAsChannel = OptionMapping::getAsChannel;
	public static final Function<Object, String> stringValue = String::valueOf;
}