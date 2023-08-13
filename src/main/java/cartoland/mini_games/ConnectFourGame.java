package cartoland.mini_games;

import java.util.Arrays;
import java.util.Random;

public class ConnectFourGame implements IMiniGame
{
	private static final int WIN_COUNT = 4; //4個連線就算贏
	private static final int ROWS = 6; //棋盤有6個橫列
	private static final int COLUMNS = 7; //每個橫列有7個直行
	private static final char PLAYER_PLACE = 'O';
	private static final char AI_PLACE = 'o';
	private static final char EMPTY = ' ';

	private final char[][] board = new char[ROWS][COLUMNS];

	public ConnectFourGame()
	{
		for (char[] row : board)
			Arrays.fill(row, EMPTY);
	}

	@Override
	public String gameName()
	{
		return "Connect Four";
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
		place(PLAYER_PLACE, column);
		return isWon(PLAYER_PLACE);
	}

	private final Random random = new Random();
	public boolean aiPlace()
	{
		int column; //要放棋子的直行
		do
			column = random.nextInt(COLUMNS); //隨機選一直行
		while (isFull(column));

		place(AI_PLACE, column);
		return isWon(AI_PLACE);
	}

	private void place(char symbol, int column)
	{
		for (int row = 0; row < ROWS; row++)
		{
			if (board[row][column] != EMPTY) //找到第一個不是空的
			{
				board[row - 1][column] = symbol; //說明它前一個一定是空的
				return; //結束
			}
		}
	}

	private boolean isWon(char symbol)
	{
		int symbolCount;

		//偵測橫的
		for (int row = 0; row < ROWS; row++) //每一列都做相同處理
		{
			symbolCount = 0;
			int c;
			for (c = 0; c < WIN_COUNT; c++) //偵測這一列前WIN_COUNT個符號 計算symbol的總數
				if (board[row][c] == symbol)
					symbolCount++;
			if (symbolCount == WIN_COUNT) //如果前WIN_COUNT個就都是symbol
				return true; //代表已經贏了

			//在這之下 就是代表前四個並非都是symbol
			//開始的時候 c已經等於WIN_COUNT 因此不用再指定
			for (; c < COLUMNS; c++) //開始一路往右
			{
				//c代表的是目前移動窗格中的最右邊那一項 在迴圈開始的時候 c等於WIN_COUNT
				if (board[row][c - WIN_COUNT] == symbol) //如果移動窗格的左邊那個是symbol
					symbolCount--; //因為即將向右 因此捨棄
				if (board[row][c] == symbol) //如果移動窗格的新格數是symbol
					symbolCount++; //加一
				if (symbolCount == WIN_COUNT) //如果窗格在移動後 就滿足的連線的條件
					return true; //代表勝利
			}
		}

		//偵測直的
		for (int column = 0; column < COLUMNS; column++) //每一行都做相同處理
		{
			;
		}
		return false;
	}
}