package cartoland.mini_games;

import cartoland.utilities.Algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * {@code TicTacToeGame} is the backend of the Tic-Tac-Toe game, it can process the entire game with all fields and
 * methods. This game start with an empty {@link #BOARD_SIDE} square board, the player and the bot take turns.
 *
 * @since 2.0
 * @see cartoland.commands.TicTacToeCommand The frontend of the Tic-Tac-Toe game.
 * @author Alex Cai
 */
public class TicTacToeGame implements IMiniGame
{
	public static final int BOARD_SIDE = 3;
	private final char[] board = new char[BOARD_SIDE * BOARD_SIDE];
	private final StringBuilder boardBuilder = new StringBuilder();
	private static final int[][] winningCombinations = new int[(BOARD_SIDE << 1) + 2][BOARD_SIDE]; //若BOARD_SIZE改變 這個也要改
	/*{
		{0, 1, 2}, {3, 4, 5}, {6, 7, 8},  // 橫列
		{0, 3, 6}, {1, 4, 7}, {2, 5, 8},  // 直行
		{0, 4, 8}, {2, 4, 6}   // 斜線
	};*/
	private static final char NOUGHT = 'O';
	private static final char CROSS = 'X';
	private static final char EMPTY = ' ';
	private static final List<int[]> notPlaced = new ArrayList<>(BOARD_SIDE * BOARD_SIDE - 1);

	static
	{
		int i, j;

		//橫列
		//{0, 1, 2}, {3, 4, 5}, {6, 7, 8}
		for (i = 0; i < BOARD_SIDE; i++)
			for (j = 0; j < BOARD_SIDE; j++)
				winningCombinations[i][j] = i * BOARD_SIDE + j;

		//直行
		//{0, 3, 6}, {1, 4, 7}, {2, 5, 8}
		for (/*i = BOARD_SIZE*/; i < BOARD_SIDE << 1 ; i++)
			for (j = 0; j < BOARD_SIDE; j++)
				winningCombinations[i][j] = j * BOARD_SIDE + i - BOARD_SIDE;

		//斜線
		//{0, 4, 8}, {2, 4, 6}
		for (j = 0; j < BOARD_SIDE; j++)
		{
			winningCombinations[BOARD_SIDE << 1][j] = j * (BOARD_SIDE + 1);
			winningCombinations[(BOARD_SIDE << 1) + 1][j] = (j + 1) * (BOARD_SIDE - 1); //j * (BOARD_SIZE - 1) + (BOARD_SIZE - 1)
		}
	}

	private static final int ROW_LENGTH = (BOARD_SIDE << 2) + 3; //3 * (BOARD_SIDE + 1) + BOARD_SIDE
	private static final String SEPARATE_ROW = "-".repeat(ROW_LENGTH);
	public TicTacToeGame()
	{
		Arrays.fill(board, ' ');
		boardBuilder.append("```\nr\\c");
		int r, c;
		for (c = 1; c <= BOARD_SIDE; c++)
			boardBuilder.append("| ").append(c).append(' ');
		for (r = 1; r <= BOARD_SIDE; r++)
		{
			boardBuilder.append('\n').append(SEPARATE_ROW).append("\n ").append(r);
			for (c = 1; c <= BOARD_SIDE; c++)
				boardBuilder.append(" | ").append(board[boardCoordinate(r, c)]);
		}
		boardBuilder.append("\n```");
	}

	@Override
	public String gameName()
	{
		return "Tic-Tac-Toe";
	}

	/**
	 * Check if the coordinate on the board is empty.
	 *
	 * @param row Attention: this is <b>1-indexed</b>. The row index of the {@code board}.
	 * @param column Attention: this is <b>1-indexed</b>. The column index of the {@code board}.
	 * @return True if the board at the row and column is empty, otherwise it's false.
	 * @since 2.0
	 * @author Alex Cai
	 */
	public boolean isPlaced(int row, int column)
	{
		return board[boardCoordinate(row, column)] != EMPTY;
	}

	public static boolean isInBounds(int row, int column)
	{
		int co = boardCoordinate(row, column);
		return 0 <= co && co < BOARD_SIDE * BOARD_SIDE;
	}

	public boolean humanPlace(int row, int column)
	{
		int co = boardCoordinate(row, column);
		board[co] = NOUGHT;
		boardBuilder.setCharAt(4 + ((ROW_LENGTH << 1) + 1) * row + 2 + (column << 2), NOUGHT);
		//省略 開頭的四個字元 (頭上的列 = -符號 和 其他棋盤列) + 2 等差數列 注意row和column從1開始
		//上一列加上換行字元 有ROW_LENGTH個字元 -符號有ROW_LENGTH個 加上換行字元

		//更新空棋盤清單
		updateNotPlaced(board);

		return checkWin(NOUGHT);
	}

	public boolean aiPlaced()
	{
		int[] co = Algorithm.randomElement(notPlaced);
		int row = co[0];
		int column = co[1];
		board[boardCoordinate(row, column)] = CROSS; //隨機選一個地方放X
		boardBuilder.setCharAt(4 + ((ROW_LENGTH << 1) + 1) * row + 2 + (column << 2), CROSS);
		return checkWin(CROSS);
	}

	public String getBoard()
	{
		return boardBuilder.toString();
	}

	private static void updateNotPlaced(char[] board)
	{
		notPlaced.clear();
		for (int r = 1; r <= BOARD_SIDE; r++)
			for (int c = 1; c <= BOARD_SIDE; c++)
				if (board[boardCoordinate(r,c)] == EMPTY)
					notPlaced.add(new int[]{r, c});
	}

	/**
	 * Calculate the index of one-dimension array {@link #board} via two parameters {@code row} and {@code column}. Must aware
	 * that these two parameters are 1-indexed, so they have to subtract 1 before doing the calculation.
	 *
	 * @param row Attention: this is <b>1-indexed</b>. The row index of the {@code board}.
	 * @param column Attention: this is <b>1-indexed</b>. The column index of the {@code board}.
	 * @return The 0-indexed index of {@link #board} after multiplied row with {@link #BOARD_SIDE} then add column.
	 * @since 2.0
	 * @author Alex Cai
	 */
	private static int boardCoordinate(int row, int column)
	{
		return (row - 1) * BOARD_SIDE + column - 1;
	}

	private boolean checkWin(char symbol)
	{
		for (int[] winLine : winningCombinations)
			if (matchWinLine(winLine, symbol)) //如果有任何一條中了勝利的組合
				return true;
		return false;
	}

	private boolean matchWinLine(int[] winLine, char symbol)
	{
		for (int w : winLine)
			if (board[w] != symbol)
				return false;
		return true;
	}

	public boolean isTie()
	{
		return notPlaced.isEmpty(); //沒地方可以走了
	}
}