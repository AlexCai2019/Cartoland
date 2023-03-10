package cartoland.mini_games;

/**
 * The interface of all mini-games in mini_games packages. Some mini-games such as lottery don't need this
 * interface because they don't store in the {@code games} HashMap, which belongs to {@link cartoland.events.CommandUsage}
 * and can be access via {@link cartoland.events.CommandUsage#getGames()} method.
 *
 * @since 1.1
 * @author Alex Cai
 */
public interface IMiniGame
{
	String gameName();
}