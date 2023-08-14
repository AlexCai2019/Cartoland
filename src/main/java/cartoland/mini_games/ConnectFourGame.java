package cartoland.mini_games;

import java.util.Arrays;
import java.util.Random;

public class ConnectFourGame implements IMiniGame
{
	private static final int WIN_COUNT = 4; //4個連線就算贏
	private static final int ROWS = 6; //棋盤有6個橫列
	private static final int COLUMNS = 7; //每個橫列有7個直行
	private static final char PLAYER_PLACE = 'O';
	private static final char AI_PLACE = 'X';
	private static final char EMPTY = ' ';

	private final char[][] board = new char[ROWS][COLUMNS];
	private final StringBuilder boardBuilder = new StringBuilder("```\nr\\c");

	public ConnectFourGame()
	{
		for (char[] row : board)
			Arrays.fill(row, EMPTY);

		int r, c;
		for (c = 1; c <= COLUMNS; c++)
			boardBuilder.append("| ").append(c).append(' ');
		for (r = 1; r <= ROWS; r++)
		{
			boardBuilder.append('\n');
			for (c = 0; c < COLUMNS; c++)
				boardBuilder.append("----");
			boardBuilder.append("---\n ").append(r).append(' ');
			for (c = 0; c < COLUMNS; c++)
				boardBuilder.append("| " + EMPTY + ' ');
		}
		boardBuilder.append("\n```");
	}

	@Override
	public String gameName()
	{
		return "Connect Four";
	}

	public String getBoard()
	{
		return boardBuilder.toString();
	}

	public boolean isInBounds(int column1Index)
	{
		return 1 <= column1Index && column1Index <= COLUMNS;
	}

	public boolean isFull(int column)
	{
		return board[0][column - 1] != EMPTY; //最上面的那橫列 是否有東西
	}

	public boolean humanPlace(int column)
	{
		return isWon(PLAYER_PLACE, place(PLAYER_PLACE, column), column);
	}

	private final Random random = new Random();
	public boolean aiPlace()
	{
		int column; //要放棋子的直行
		do
			column = random.nextInt(COLUMNS); //隨機選一直行
		while (isFull(column)); //如果已經滿了 就再隨機選一次

		return isWon(AI_PLACE, place(AI_PLACE, column), column);
	}

	private int place(char symbol, int column)
	{
		int row = 0; //即將要放置棋子的橫列
		for (; row < ROWS; row++)
		{
			if (board[row][column] != EMPTY) //找到第一個不是空的
			{
				board[row - 1][column] = symbol; //說明它前一個一定是空的
				break; //結束
			}
		}
		return row;
	}

	private boolean isWon(char symbol, int row, int column)
	{
		int r, c;
		int rowMax, columnMax;
		int symbolCount;

		// -
		symbolCount = 0;
		for (c = Math.max(0, column - (WIN_COUNT - 1)), columnMax = Math.min(COLUMNS, column + WIN_COUNT); c < columnMax; c++) //走訪這一列的每個棋子
		{
			if (board[row][c] == symbol) //檢查經過的每個棋子
			{
				symbolCount++;
				if (symbolCount == WIN_COUNT) //連續WIN_COUNT個都是symbol
					return true; //勝利
			}
			else //有一個不符合
				symbolCount = 0; //歸零
		}

		// |
		symbolCount = 0;
		for (r = Math.max(0, row - (WIN_COUNT - 1)), rowMax = Math.min(ROWS, row + WIN_COUNT); r < rowMax; r++) //走訪這一行的每個棋子
		{
			if (board[r][column] == symbol) //檢查經過的每個棋子
			{
				symbolCount++;
				if (symbolCount == WIN_COUNT) //連續WIN_COUNT個都是symbol
					return true;
			}
			else //有一個不符合
				symbolCount = 0; //歸零
		}

		// \
		symbolCount = 0;
		if (row > column) //將r和c歸位到左上角 直到碰到其中一面的邊界為止
		{
			r = row - column;
			c = 0;
		}
		else
		{
			r = 0;
			c = column - row;
		}
		for (rowMax = Math.min(ROWS, row + WIN_COUNT), columnMax = Math.min(COLUMNS, column + WIN_COUNT); r < rowMax && c < columnMax; r++, c++)
		{
			if (board[r][c] == symbol)
			{
				symbolCount++;
				if (symbolCount == WIN_COUNT) //連續WIN_COUNT個都是symbol
					return true;
			}
			else //有一個不符合
				symbolCount = 0; //歸零
		}

		// /
		symbolCount = 0;
		if (row > column) //將r和c歸位到右上角 直到碰到其中一面的邊界為止
		{
			r = row + column;
			c = 0;
		}
		else
		{
			r = 0;
			c = column + row;
		}
		for (rowMax = Math.min(ROWS, row + WIN_COUNT), columnMax = Math.max(-1, column - WIN_COUNT); r < rowMax && c > columnMax; r++, c--)
		{
			if (board[r][c] == symbol)
			{
				symbolCount++;
				if (symbolCount == WIN_COUNT) //連續WIN_COUNT個都是symbol
					return true;
			}
			else //有一個不符合
				symbolCount = 0; //歸零
		}

		return false;
	}
}