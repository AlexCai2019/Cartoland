package cartoland.mini_games;

public class LightOutGame implements IMiniGame
{
	private static final char ON = '⬜';
	private static final char OFF = '⬛';
	private static final byte ROWS = 5;
	private static final byte COLUMNS = 5;

	private final char[][] board = new char[ROWS][COLUMNS];

	public LightOutGame()
	{
	}

	@Override
	public String gameName()
	{
		return "Light Out";
	}
}