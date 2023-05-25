package cartoland.mini_games;

import cartoland.utilities.Algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @since 2.0
 * @author Alex Cai
 */
public class TicTacToeGame implements IMiniGame
{
	public static final int BOARD_SIZE = 3;
	private final char[] board = new char[BOARD_SIZE * BOARD_SIZE];
	private static final int[][] winningCombinations = //若BOARD_SIZE改變 這個也要改
	{
		{0, 1, 2}, {3, 4, 5}, {6, 7, 8},  // 橫列
		{0, 3, 6}, {1, 4, 7}, {2, 5, 8},  // 直行
		{0, 4, 8}, {2, 4, 6}   // 斜線
	};

	public TicTacToeGame()
	{
		Arrays.fill(board, ' ');
	}

	@Override
	public String gameName()
	{
		return "Tic-Tac-Toe";
	}

	public boolean isPlaced(int row, int column)
	{
		return board[boardCoordinate(row, column)] != ' ';
	}

	public boolean humanPlace(int row, int column)
	{
		board[boardCoordinate(row, column)] = 'O';
		return checkWin('O');
	}

	public boolean aiPlaced()
	{
		List<Integer> notPlaced = new ArrayList<>(BOARD_SIZE * BOARD_SIZE - 1);
		for (int i = 0; i < board.length; i++)
			if (board[i] == ' ')
				notPlaced.add(i);
		board[Algorithm.randomElement(notPlaced)] = 'X'; //隨機選一個地方放X
		return checkWin('X');
	}

	public char[] getBoard()
	{
		return board;
	}

	private static int boardCoordinate(int row, int column)
	{
		return row * BOARD_SIZE + column;
	}

	private boolean checkWin(char symbol)
	{
		boolean win;
		for (int[] winLine : winningCombinations)
		{
			win = true;
			for (int w : winLine)
			{
				if (board[w] != symbol)
				{
					win = false;
					break;
				}
			}
			if (win)
				return true;
		}
		return false;
	}
}