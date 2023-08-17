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
	private int spaces = ROWS * COLUMNS;
	private final StringBuilder boardBuilder = new StringBuilder("```\n 1 "); //顯示棋盤
	private static final int CHARACTERS_IN_A_ROW = 3 + (COLUMNS - 1) * 4 + 1; //第一行的--- 以及剩下那些行分配到的---- 還有最後的換行
	private final Random random = new Random();

	public ConnectFourGame()
	{
		for (char[] row : board)
			Arrays.fill(row, EMPTY);

		int r, c;
		for (c = 2; c <= COLUMNS; c++) //從2開始 1已經事先放好了
			boardBuilder.append("| ").append(c).append(' '); //標上棋盤的行數
		String separateRowDashes = "-".repeat(CHARACTERS_IN_A_ROW - 1); //扣掉換行符號
		for (r = 1; r <= ROWS; r++)
		{
			boardBuilder.append('\n') //換行
					.append(separateRowDashes) //一大堆橫線
					.append("\n " + EMPTY + ' '); //下一行以及第一格的空白
			for (c = 1; c < COLUMNS; c++)
				boardBuilder.append("| " + EMPTY + ' ');
		}
		boardBuilder.append("\n```");
	}

	@Override
	public String gameName()
	{
		return "Connect Four";
	}

	private void updateBoardString(char symbol, int row, int column)
	{
		System.out.println("symbol = " + symbol + ", row = " + row + ", column = " + column);
		boardBuilder.setCharAt(4 + CHARACTERS_IN_A_ROW * 2 + //首先排除掉前面的```\n以及上面那兩列
				row * CHARACTERS_IN_A_ROW * 2 + //在第幾列 就忽略幾列的橫線和棋盤內容
				4 * column + 1 //每一項都是前一項 + 1 這個等差數列是從1開始的
				, symbol);
	}

	public String getBoard()
	{
		return boardBuilder.toString();
	}

	public boolean isInBounds(int column)
	{
		return 0 <= column && column < COLUMNS;
	}

	public boolean isFull(int column)
	{
		return board[0][column] != EMPTY; //最上面的那橫列 是否有東西
	}

	public boolean humanPlace(int column)
	{
		spaces--; //可用空間減少一格
		int row = place(PLAYER_PLACE, column); //即將要落子的那個橫列
		updateBoardString(PLAYER_PLACE, row, column); //更新棋盤字串
		return isWon(PLAYER_PLACE, row, column);
	}

	public boolean aiPlace()
	{
		spaces--; //可用空間減少一格
		int column; //要放棋子的直行
		do //column必須要先取值一次 雖然用while(isFull(column))也可以 但是難得有do-while的表現機會
			column = random.nextInt(COLUMNS); //隨機選一直行
		while (isFull(column)); //如果已經滿了 就再隨機選一次
		int row = place(AI_PLACE, column); //即將要落子的那個橫列
		updateBoardString(AI_PLACE, row, column); //更新棋盤字串

		return isWon(AI_PLACE, row, column); //回傳是否勝利
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
		return row - 1;
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
			r = row - column;
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

	public boolean isTie()
	{
		return spaces == 0; //已經沒有空間了
	}
}