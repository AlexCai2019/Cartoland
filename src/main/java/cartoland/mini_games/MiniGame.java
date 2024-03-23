package cartoland.mini_games;

import java.time.Duration;
import java.time.Instant;

/**
 * The abstract class of all mini-games in mini_games packages. Some mini-games such as lottery don't need this
 * class because they don't store in the {@code games} HashMap, which belongs to {@link cartoland.events.CommandUsage}
 * and can be access directly through the field with same name.
 *
 * @since 1.1
 * @author Alex Cai
 */
public abstract class MiniGame
{
	private final Instant begin = Instant.now();

	//計時結束
	public long getTimePassed()
	{
		return Duration.between(begin, Instant.now()).toSeconds();
	}

	protected final StringBuilder recordBuilder = new StringBuilder(); //遊玩紀錄

	public abstract String gameName();

	public String getRecord()
	{
		return recordBuilder.toString();
	}

	public static class MiniGameMap extends java.util.HashMap<Long, MiniGame> {}
}