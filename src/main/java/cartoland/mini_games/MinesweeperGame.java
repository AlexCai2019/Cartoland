package cartoland.mini_games;

import cartoland.utilities.Algorithm;
import cartoland.utilities.IDAndEntities;

/**
 * {@code MinesweeperGame} is the backend of the Minesweeper game, it can process the entire game with all fields and methods.
 * TODO: Complete this class
 *
 * @since 1.1
 * @see cartoland.commands.MinesweeperCommand The frontend of the Minesweeper game.
 * @author Alex Cai
 */
public class MinesweeperGame implements IMiniGame
{
	private final byte[][] map;
	public static final byte ZERO_UNDERCOVER = 0;
	public static final byte ONE_UNDERCOVER = 1;
	public static final byte TWO_UNDERCOVER = 2;
	public static final byte THREE_UNDERCOVER = 3;
	public static final byte FOUR_UNDERCOVER = 4;
	public static final byte FIVE_UNDERCOVER = 5;
	public static final byte SIX_UNDERCOVER = 6;
	public static final byte SEVEN_UNDERCOVER = 7;
	public static final byte EIGHT_UNDERCOVER = 8;
	public static final byte ZERO_REVEAL = 10;
	public static final byte ONE_REVEAL = 11;
	public static final byte TWO_REVEAL = 12;
	public static final byte THREE_REVEAL = 13;
	public static final byte FOUR_REVEAL = 14;
	public static final byte FIVE_REVEAL = 15;
	public static final byte SIX_REVEAL = 16;
	public static final byte SEVEN_REVEAL = 17;
	public static final byte EIGHT_REVEAL = 18;
	public static final byte MINE = 100;
	private final int row;
	private final int column;
	private int mines;

	public MinesweeperGame(String difficulty)
	{
		switch (difficulty)
		{
			case IDAndEntities.Difficulty.EASY ->
			{
				map = new byte[5][5];
				mines = 5;
			}

			case IDAndEntities.Difficulty.NORMAL ->
			{
				map = new byte[7][7];
				mines = 7;
			}

			case IDAndEntities.Difficulty.HARD ->
			{
				map = new byte[9][9];
				mines = 9;
			}

			default ->
			{
				map = new byte[1][1];
				mines = 0;
			}
		}

		row = map.length;
		column = map[0].length;
		for (int i = 0; i < row; i++)
			for (int j = 0; j < column; j++)
				map[i][j] = ZERO_UNDERCOVER;

		int straightMapLength = row * column;
		int[] coordinates = new int[straightMapLength];

		for (int i = 0; i < straightMapLength; i++)
			coordinates[i] = i;
		Algorithm.shuffle(coordinates);

		for (int i = 0, x, y; i < mines; i++)
		{
			x = coordinates[i] / row;
			y = coordinates[i] % column;
			map[x][y] = MINE;

			addNearMineCount(x - 1, y - 1);
			addNearMineCount(x, y - 1);
			addNearMineCount(x + 1, y - 1);
			addNearMineCount(x - 1, y);
			addNearMineCount(x + 1, y);
			addNearMineCount(x - 1, y + 1);
			addNearMineCount(x, y + 1);
			addNearMineCount(x + 1, y + 1);
		}
	}

	private void addNearMineCount(int x, int y)
	{
		if (x >= 0 && x < column && y >= 0 && y < row)
			map[x][y]++;
	}

	public byte[][] getMap()
	{
		return map;
	}

	@Override
	public String gameName()
	{
		return "Minesweeper";
	}
}