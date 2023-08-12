package cartoland.mini_games;

import cartoland.commands.TicTacToeCommand;
import cartoland.utilities.Algorithm;

import java.util.Arrays;

/**
 * {@code TicTacToeGame} is the backend of the Tic-Tac-Toe game, it can process the entire game with all fields and
 * methods. This game start with an empty {@link #BOARD_SIDE} square board, the player and the bot take turns.
 *
 * @since 2.0
 * @see TicTacToeCommand The frontend of the Tic-Tac-Toe game.
 * @author Alex Cai
 */
public class TicTacToeGame implements IMiniGame
{
	private static final int BOARD_SIDE = 3;
	private static final int LEFT_CORNER = 0;
	private static final int CENTER = BOARD_SIDE * BOARD_SIDE >> 1;

	private final char[] board = new char[9];
	private final StringBuilder boardBuilder = new StringBuilder();

	private static final int[][] winningCombinations =
	{
		{0, 1, 2}, {3, 4, 5}, {6, 7, 8},  // 橫列
		{0, 3, 6}, {1, 4, 7}, {2, 5, 8},  // 直行
		{0, 4, 8}, {2, 4, 6}   // 斜線
	}; //注意: 這個陣列預設BOARD_SIDE為3

	private static final char NOUGHT = 'O';
	private static final char CROSS = 'X';
	private static final char EMPTY = ' ';

	private int spaces = BOARD_SIDE * BOARD_SIDE; //棋盤上還空著的格數
	private int[] notPlaced = null; //board還是EMPTY的index們 之所以不用ArrayList 是為了省效能 注意要到第三輪才會開始追蹤空棋盤
	private int round = 1;

	private final DifficultyBot difficultyBot;

	private static final int ROW_LENGTH = (BOARD_SIDE << 2) + 3; //3 * (BOARD_SIDE + 1) + BOARD_SIDE
	private static final String SEPARATE_ROW = "-".repeat(ROW_LENGTH);

	public TicTacToeGame(int difficulty)
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

		difficultyBot = switch (difficulty)
		{
			case 0 -> new BabyBot(this);
			case 1 -> new EasyBot(this);
			case 2 -> new NormalBot(this);
			case 3 -> new HardBot(this);
			case 4 -> new HellBot(this);
			default -> throw new IllegalArgumentException("How can you create this difficulty?");
		};
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
		board[boardCoordinate(row, column)] = NOUGHT;
		boardBuilder.setCharAt(4 + ((ROW_LENGTH << 1) + 1) * row + 2 + (column << 2), NOUGHT);
		//省略 開頭的四個字元 (頭上的列 = -符號 和 其他棋盤列) + 2 等差數列 注意row和column從1開始
		//上一列加上換行字元 有ROW_LENGTH個字元 -符號有ROW_LENGTH個 加上換行字元

		spaces--; //空棋盤少一格
		return round > 2 && checkWin(NOUGHT); //進行到第三回合才有可能有輸贏
	}

	public boolean aiPlaced()
	{
		int index = switch (round)
		{
			case 1 -> difficultyBot.round1(); //第一回合
			case 2 -> difficultyBot.round2(); //第二回合
			case 3 -> difficultyBot.round3(); //第三回合
			default -> difficultyBot.round4AndMore(); //第四回合和以上
		}; //機器人落子
		board[index] = CROSS;
		int[] co = arrayOfRowAndColumn(index); // co[0] = row, co[1] = column
		boardBuilder.setCharAt(4 + ((ROW_LENGTH << 1) + 1) * co[0] + 2 + (co[1] << 2), CROSS);
		spaces--; //空棋盤少一格
		return round++ > 2 && checkWin(CROSS); //進行到第三回合才有可能有輸贏
	}

	public String getBoard()
	{
		return boardBuilder.toString();
	}

	public int getReward()
	{
		return difficultyBot.getReward();
	}

	private void updateNotPlaced()
	{
		notPlaced = new int[spaces]; //每次落子都有在追蹤剩餘的版面 spaces代表棋盤上還有多少空白
		for (int i = 0, j = 0; i < BOARD_SIDE * BOARD_SIDE; i++) //走訪整個board
			if (board[i] == EMPTY) //如果該點位是空的
				notPlaced[j++] = i; //新增進notPlaced裡
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

	private static int[] arrayOfRowAndColumn(int index)
	{
		return new int[] { index / BOARD_SIDE + 1, index % BOARD_SIDE + 1 };
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
		return spaces == 0; //沒地方可以走了
	}

	/**
	 * The core of the AI that plays Tic-Tac-Toe.
	 *
	 * @since 2.1
	 * @author Alex Cai
	 */
	private static abstract class DifficultyBot
	{
		protected final TicTacToeGame game;

		private DifficultyBot(TicTacToeGame game)
		{
			this.game = game;
		}

		protected abstract int getReward();

		/**
		 * The bot needs to do based on the board and the difficulty of a game in first round.
		 *
		 * @return Bot's move.
		 * @since 2.1
		 * @author Alex Cai
		 */
		protected abstract int round1();
		protected abstract int round2();
		protected abstract int round3();
		protected abstract int round4AndMore();
	}

	/**
	 * Baby mode. Put pieces randomly.
	 *
	 * @since 2.1
	 * @author Alex Cai
	 */
	private static class BabyBot extends DifficultyBot
	{
		private BabyBot(TicTacToeGame game)
		{
			super(game);
		}

		@Override
		protected int getReward()
		{
			return 1;
		}

		@Override
		protected int round1()
		{
			return randomPlace();
		}

		@Override
		protected int round2()
		{
			return randomPlace();
		}

		@Override
		protected int round3()
		{
			return randomPlace();
		}

		@Override
		protected int round4AndMore()
		{
			return randomPlace();
		}

		private int randomPlace()
		{
			game.updateNotPlaced(); //更新空棋盤清單
			return Algorithm.randomElement(game.notPlaced); //隨機選一個地方放X
		}
	}

	/**
	 * Easy mode. Put the piece at left corner if the center is occupied or put it at center if not in the first round.
	 * Put pieces randomly when round turns to second and more.
	 *
	 * @since 2.1
	 * @author Alex Cai
	 */
	private static class EasyBot extends BabyBot
	{
		private EasyBot(TicTacToeGame game)
		{
			super(game);
		}

		@Override
		protected int getReward()
		{
			return 5;
		}

		/**
		 * Based on the user's move on round 1. If the user put the piece at center, the bot put its one at
		 * upper-left; otherwise, the bot put its piece at center of the board. Notice that <b>this method
		 * assumed that {@link TicTacToeGame#BOARD_SIDE} is 3.</b>
		 *
		 * @return The index of {@link TicTacToeGame#board} array that the bot is going to put piece.
		 * @since 2.1
		 * @author Alex Cai
		 */
		@Override
		protected int round1()
		{
			return game.board[CENTER] == NOUGHT ? LEFT_CORNER : CENTER;
		}
	}

	/**
	 * Normal mode. Inherited {@link EasyBot}, but will try to prevent human from win and find a
	 * way of wining in the second round. Still put pieces randomly after the third round.
	 *
	 * @since 2.1
	 * @author Alex Cai
	 */
	private static class NormalBot extends EasyBot
	{
		private static final int[][] tryAtLeftCorner = {{1, 2}, {3, 6},{4, 8}}; //第一步下在左上角後 第二步可以下的位置
		private static final int[][] tryAtCenter = {{0, 8}, {1, 7}, {2, 6}, {3, 5}}; //第一步下在中間後 第二步可以下的位置

		private NormalBot(TicTacToeGame game)
		{
			super(game);
		}

		@Override
		protected int getReward()
		{
			return 20;
		}

		@Override
		protected int round2()
		{
			Algorithm.shuffle(winningCombinations); //隨機更換檢測勝利的順序 為人類玩家的策略帶來不定性

			int first, second, third;
			char f, s, t;
			for (int[] winLine: winningCombinations) //檢查O是否即將連線 如果O確實即將連線則阻止
			{
				f = game.board[first = winLine[0]];
				s = game.board[second = winLine[1]];
				t = game.board[third = winLine[2]];
				if (f + s == NOUGHT + NOUGHT && t == EMPTY) //[0]和[1]皆為O
					return third;
				if (f + t == NOUGHT + NOUGHT && s == EMPTY) //[0]和[2]皆為O
					return second;
				if (s + t == NOUGHT + NOUGHT && f == EMPTY) //[1]和[2]皆為O
					return first;
			}

			//確認O沒有要連線後

			//找出和第一手鄰近 可連成一線 且都是空的兩格 隨機挑選一格落子
			//因為這是第二回合 O只放了兩個 代表必定能找到一組空的
			if (game.board[LEFT_CORNER] == CROSS) //第一手下在左上角
			{
				Algorithm.shuffle(tryAtLeftCorner);
				for (int[] bothEmpty : tryAtLeftCorner)
					if (bothEmpty[0] + bothEmpty[1] == EMPTY << 1) //bothEmpty[0] == TicTacToeGame.EMPTY && bothEmpty[1] == TicTacToeGame.EMPTY
						return bothEmpty[1]; //搶角落
			}
			else //如果不是下在左上角 那就肯定是下在中間了
			{
				Algorithm.shuffle(tryAtCenter);
				for (int[] bothEmpty : tryAtCenter)
					if (bothEmpty[0] + bothEmpty[1] == EMPTY << 1) //bothEmpty[0] == TicTacToeGame.EMPTY && bothEmpty[1] == TicTacToeGame.EMPTY
						return Algorithm.chance(50) ? bothEmpty[0] : bothEmpty[1];
			}

			//如果以上都不通過
			return super.round2(); //就隨機走
		}
	}

	/**
	 * Hard mode. Inherited {@link NormalBot}, but will try to prevent human from win and find a
	 * way of wining in the second round. Still put pieces randomly after the third round.
	 *
	 * @since 2.1
	 * @author Alex Cai
	 */
	private static class HardBot extends NormalBot
	{
		private HardBot(TicTacToeGame game)
		{
			super(game);
		}

		@Override
		protected int getReward()
		{
			return 50;
		}

		@Override
		protected int round3()
		{
			int first, second, third;
			char f, s, t;

			for (int[] winLine: winningCombinations) //檢查X是否即將連線 如果是則執行
			{
				f = game.board[first = winLine[0]];
				s = game.board[second = winLine[1]];
				t = game.board[third = winLine[2]];
				if (f + s == CROSS + CROSS && t == EMPTY) //[0]和[1]皆為X
					return third;
				if (f + t == CROSS + CROSS && s == EMPTY) //[0]和[2]皆為X
					return second;
				if (t + s == CROSS + CROSS && f == EMPTY) //[1]和[2]皆為X
					return first;
			}

			for (int[] winLine: winningCombinations) //檢查O是否即將連線 如果是則阻止
			{
				f = game.board[first = winLine[0]];
				s = game.board[second = winLine[1]];
				t = game.board[third = winLine[2]];
				if (f + s == NOUGHT + NOUGHT && t == EMPTY) //[0]和[1]皆為O
					return third;
				if (f + t == NOUGHT + NOUGHT && s == EMPTY) //[0]和[2]皆為O
					return second;
				if (t + s == NOUGHT + NOUGHT && f == EMPTY) //[1]和[2]皆為O
					return first;
			}

			//以上都不通過
			return super.round3(); //就隨機走
		}
	}

	private static final class HellBot extends HardBot
	{
		private HellBot(TicTacToeGame game)
		{
			super(game);
		}

		@Override
		protected int getReward()
		{
			return 100;
		}

		@Override
		protected int round4AndMore()
		{
			return round3();
		}
	}
}