package cartoland.mini_games;

import cartoland.utilities.Algorithm;

public class LightOutGame implements IMiniGame
{
	private static final char ON = '⬜';
	private static final char OFF = '⬛';
	private static final byte ROWS = 5;
	private static final byte COLUMNS = 5;
	private static final byte INITIAL_LIGHTS = 5; //初始會點亮的格子
	private static final byte[][] PLACE_ON = new byte[ROWS * COLUMNS][2]; //將要隨機選出要被放置ON的點位
	private static final StringBuilder BOARD_TEMPLATE = new StringBuilder("r\\c");

	static
	{
		byte row, column;
		for (row = 0; row < ROWS; row++)
			for (column = 0; column < COLUMNS; column++)
				PLACE_ON[row * column] = new byte[]{ row, column }; // {0,0},{0,1},{0,2},{0,3},{0,4},{1,0},{1,1},{1,2}...

		for (column = 1; column <= COLUMNS; column++)
			BOARD_TEMPLATE.append("| ").append(column).append(' ');
		String separateRow = "-".repeat((COLUMNS + 1) * 3 + COLUMNS); //中間的橫槓
		for (row = 1; row <= ROWS; row++)
		{
			BOARD_TEMPLATE.append('\n').append(separateRow).append("\n ").append(row).append(' ');
			for (column = 1; column < COLUMNS; column++)
				BOARD_TEMPLATE.append("|   ");
		}
	}

	private final boolean[][] board = new boolean[ROWS][COLUMNS]; //true為開啟 false為關閉
	private final StringBuilder boardBuilder = new StringBuilder(BOARD_TEMPLATE);

	public LightOutGame()
	{
		Algorithm.shuffle(PLACE_ON); //洗牌 藉此隨機選出要點亮的格子
		byte light;
		for (light = 0; light < INITIAL_LIGHTS; light++) //取出前INITIAL_LIGHTS項 作為要點亮的格子
			board[PLACE_ON[light][0]][PLACE_ON[light][1]] = true; //設為點亮
		for (; light < PLACE_ON.length; light++)
			board[PLACE_ON[light][0]][PLACE_ON[light][1]] = false; //剩下的就設為關閉
	}

	@Override
	public String gameName()
	{
		return "Light Out";
	}
}