package cartoland.mini_games;

import cartoland.utilities.Algorithm;

public class LightOutGame extends MiniGame
{
	private static final char ON = '▫';
	private static final char OFF = '▪';
	private static final byte ROWS = 5;
	private static final byte COLUMNS = 5;
	private static final byte INITIAL_LIGHTS = 5; //初始會點亮的格子
	private static final byte[][] PLACE_ON = new byte[ROWS * COLUMNS][2]; //將要隨機選出要被放置ON的點位
	private static final StringBuilder BOARD_TEMPLATE = new StringBuilder("```\nr\\c");
	private static final int CHARACTERS_IN_A_ROW = 3 + (COLUMNS) * 4 + 1; //r\c行的--- 以及數字行的---- 還有最後的換行

	static
	{
		byte row, column, index = 0;
		for (row = 0; row < ROWS; row++)
			for (column = 0; column < COLUMNS; column++)
				PLACE_ON[index++] = new byte[]{ row, column }; // {0,0},{0,1},{0,2},{0,3},{0,4},{1,0},{1,1},{1,2}...

		//建立棋盤範本
		for (column = 1; column <= COLUMNS; column++)
			BOARD_TEMPLATE.append("| ").append(column).append(' ');
		String separateRow = "-".repeat(CHARACTERS_IN_A_ROW - 1); //中間的橫槓
		for (row = 1; row <= ROWS; row++)
		{
			BOARD_TEMPLATE.append('\n').append(separateRow).append("\n ").append(row).append(' ');
			for (column = 1; column <= COLUMNS; column++)
				BOARD_TEMPLATE.append("| " + OFF + ' ');
		}
		BOARD_TEMPLATE.append("\n```");
	}

	private final boolean[][] board = new boolean[ROWS][COLUMNS]; //true為開啟 false為關閉
	private final StringBuilder boardBuilder = new StringBuilder(BOARD_TEMPLATE); //從棋盤範本建立棋盤字串
	private int onCount = INITIAL_LIGHTS; //on的格數

	public LightOutGame()
	{
		Algorithm.shuffle(PLACE_ON); //洗牌 藉此隨機選出要點亮的格子
		byte light;
		for (light = 0; light < INITIAL_LIGHTS; light++) //取出前INITIAL_LIGHTS項 作為要點亮的格子
		{
			byte row = PLACE_ON[light][0],
				column = PLACE_ON[light][1];
			board[row][column] = true; //設為點亮
			updateBoardString(ON, row, column);
		}
		for (; light < PLACE_ON.length; light++)
			board[PLACE_ON[light][0]][PLACE_ON[light][1]] = false; //剩下的就設為關閉
	}

	@Override
	public String gameName()
	{
		return "light_out";
	}

	public static boolean isInBounds(int row, int column)
	{
		return row >= 0 && row < ROWS && column >= 0 && column < COLUMNS;
	}

	public boolean flip(int row, int column)
	{
		internalFlip(row, column); //原地翻面
		safeFlip(row - 1, column); //上面翻面
		safeFlip(row + 1, column); //下面翻面
		safeFlip(row, column - 1); //左邊翻面
		safeFlip(row, column + 1); //右邊翻面
		recordBuilder.append(boardBuilder.substring(3, boardBuilder.length() - 3)).append('\n');
		return onCount == 0; //沒有on代表贏了
	}

	private void safeFlip(int row, int column)
	{
		if (isInBounds(row, column))
			internalFlip(row, column);
	}

	private void internalFlip(int row, int column)
	{
		boolean isOnAfterFlip = board[row][column] = !board[row][column]; //翻轉
		if (isOnAfterFlip) //如果翻轉後變成on
			onCount++; //增加
		else //反之
			onCount--; //減少
		updateBoardString(isOnAfterFlip ? ON : OFF, row, column); //更新board陣列 同時更新棋盤字串
	}

	private void updateBoardString(char symbol, int row, int column)
	{
		boardBuilder.setCharAt(4 + (CHARACTERS_IN_A_ROW << 1) + //首先排除掉前面的```\n以及上面那兩列
						row * (CHARACTERS_IN_A_ROW << 1) + //在第幾列 就忽略幾列的橫線和棋盤內容
						4 * (column + 1) + 1 //每一項都是前一項 + 1 這個等差數列是從1開始的
				, symbol);
	}

	public String getBoard()
	{
		return boardBuilder.toString();
	}
}