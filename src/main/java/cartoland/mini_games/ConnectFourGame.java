package cartoland.mini_games;

import cartoland.utilities.Algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConnectFourGame implements IMiniGame
{
	private static final int WIN_COUNT = 4; //4個連線就算贏
	private static final int ROWS = 6; //棋盤有6個橫列
	private static final int COLUMNS = 7; //每個橫列有7個直行
	private static final char PLAYER_PLACE = 'O';
	private static final char AI_PLACE = 'X';
	private static final char EMPTY = ' ';
	private static final int[] ZERO_ONE_TWO = {0,1,2};
	public static final StringBuilder BOARD_TEMPLATE = new StringBuilder("```\n 1 "); //棋盤範本
	private static final int CHARACTERS_IN_A_ROW = 3 + (COLUMNS - 1) * 4 + 1; //第一行的--- 以及剩下那些行分配到的---- 還有最後的換行

	static
	{
		int r, c;
		for (c = 2; c <= COLUMNS; c++) //從2開始 1已經事先放好了
			BOARD_TEMPLATE.append("| ").append(c).append(' '); //標上棋盤的行數
		String separateRowDashes = "-".repeat(CHARACTERS_IN_A_ROW - 1); //扣掉換行符號
		for (r = 1; r <= ROWS; r++)
		{
			BOARD_TEMPLATE.append('\n') //換行
					.append(separateRowDashes) //一大堆橫線
					.append("\n " + EMPTY + ' '); //下一行以及第一格的空白
			for (c = 1; c < COLUMNS; c++)
				BOARD_TEMPLATE.append("| " + EMPTY + ' ');
		}
		BOARD_TEMPLATE.append("\n```");
	}

	private final char[][] board = new char[ROWS][COLUMNS];
	private int spaces = ROWS * COLUMNS;
	private final StringBuilder boardBuilder = new StringBuilder(BOARD_TEMPLATE); //顯示棋盤

	private int lastHumanPlace;

	public ConnectFourGame()
	{
		for (char[] row : board)
			Arrays.fill(row, EMPTY);
	}

	@Override
	public String gameName()
	{
		return "connect_four";
	}

	private void updateBoardString(char symbol, int row, int column)
	{
		boardBuilder.setCharAt(4 + (CHARACTERS_IN_A_ROW << 1) + //首先排除掉前面的```\n以及上面那兩列 (用 << 1 取代 * 2)
				row * (CHARACTERS_IN_A_ROW << 1) + //在第幾列 就忽略幾列的橫線和棋盤內容 (用 << 1 取代 * 2)
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
		lastHumanPlace = column;
		int row = place(PLAYER_PLACE, column); //即將要落子的那個橫列
		updateBoardString(PLAYER_PLACE, row, column); //更新棋盤字串
		return isWon(PLAYER_PLACE, row, column);
	}

	public boolean aiPlace()
	{
		spaces--; //可用空間減少一格
		int column; //要放棋子的直行
		if (isFull(Math.max(0, lastHumanPlace - 1)) && isFull(lastHumanPlace) && isFull(Math.min(lastHumanPlace + 1, COLUMNS - 1))) //玩家下的左 中 右 都是滿的
		{
			List<Integer> possibleColumns = new ArrayList<>(); //所有還有空位的直行
			for (int i = 0; i < lastHumanPlace - 1; i++) //從最左邊開始 走訪玩家下的左邊那一區(不包含剛剛已經檢查過的左邊那一行)
				if (!isFull(i)) //這一行還有空間
					possibleColumns.add(i); //加入可能落子的直行中
			for (int i = lastHumanPlace + 2; i < COLUMNS; i++)
				if (!isFull(i))
					possibleColumns.add(i);
			column = Algorithm.randomElement(possibleColumns); //隨機選一直行
		}
		else //玩家的左 中 右 至少有一行還有個空格
		{
			Algorithm.shuffle(ZERO_ONE_TWO); //隨機排序0、1、2
			int i = 0;
			do //column必須要先取值一次 雖然用while(isFull(column))也可以 但是難得有do-while的表現機會
				column = lastHumanPlace - 1 + ZERO_ONE_TWO[i++]; //從玩家下的左 中 右 當中 隨機選一直行
			while (column < 0 || column >= COLUMNS || isFull(column)); //如果數字不對 或已經滿了 就再隨機選一次
		}
		int row = place(AI_PLACE, column); //即將要落子的那個橫列
		updateBoardString(AI_PLACE, row, column); //更新棋盤字串

		return isWon(AI_PLACE, row, column); //回傳是否勝利
	}

	private int place(char symbol, int column)
	{
		int row = 1; //即將要放置棋子的橫列
		for (; row < ROWS; row++)
			if (board[row][column] != EMPTY) //找到第一個不是空的
				break; //說明它前一個一定是空的
		board[row - 1][column] = symbol; //找到之後放棋子
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
		//noinspection StatementWithEmptyBody
		for (r = row, c = column; r > 0 && c > 0; r--, c--)
			; //將r和c歸位到左上角 直到碰到其中一面的邊界為止
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
		//noinspection StatementWithEmptyBody
		for (r = row, c = column; r > 0 && c < COLUMNS - 1; r--, c++)
			; //將r和c歸位到右上角 直到碰到其中一面的邊界為止
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