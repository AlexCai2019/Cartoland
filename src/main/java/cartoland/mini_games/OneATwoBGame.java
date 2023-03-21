package cartoland.mini_games;

import cartoland.utilities.Algorithm;

import java.time.Duration;
import java.time.Instant;
import java.util.regex.Pattern;

/**
 * {@code OneATwoBGame} is the backend of the 1A2B game, it can process the entire game with all fields and methods.
 * This game will generate a {@link #ANSWER_LENGTH} digits long number. Leading 0 is allowed. Players need to guess
 * the number, if the place of a digit is right, that is an A; if the digit is right but the place is wrong, that is a B.
 *
 * @since 1.1
 * @see cartoland.commands.OneATwoBCommand The frontend of the 1A2B game.
 * @author Alex Cai
 */
public class OneATwoBGame implements IMiniGame
{
	public static final int ANSWER_LENGTH = 4;
	private final Pattern digitsRegex = Pattern.compile("\\d{" + ANSWER_LENGTH + "}"); //ANSWER_LENGTH個數字

	private final int[] answer = new int[ANSWER_LENGTH];
	private final int[] zeroToNine = { 0,1,2,3,4,5,6,7,8,9 };
	private final boolean[] inAnswer = { false,false,false,false,false,false,false,false,false,false };
	private final Instant begin;
	private int guesses = 0;

	public OneATwoBGame()
	{
		Algorithm.shuffle(zeroToNine); //洗牌0 ~ 9
		generateAnswer(); //產生答案
		begin = Instant.now();
	}

	@Override
	public String gameName()
	{
		return "1A2B";
	}

	private void generateAnswer()
	{
		for (int i = 0; i < ANSWER_LENGTH; i++)
		{
			answer[i] = zeroToNine[i];
			inAnswer[answer[i]] = true;
		}
	}

	private void restoreZeroToNine()
	{
		for (int i = 0; i < 10; i++)
			zeroToNine[i] = i;
	}

	public int calculateAAndB(String input)
	{
		guesses++;
		if (input == null || !digitsRegex.matcher(input).matches()) //不是ANSWER_LENGTH個數字
			return ErrorCode.INVALID;

		restoreZeroToNine();
		int a = 0, b = 0;
		for (int i = 0, digitValueOfInput; i < ANSWER_LENGTH; i++)
		{
			digitValueOfInput = Character.getNumericValue(input.charAt(i));
			if (zeroToNine[digitValueOfInput] == -1) //遇過這個數字了
				return ErrorCode.NOT_UNIQUE;

			zeroToNine[digitValueOfInput] = -1;

			if (answer[i] == digitValueOfInput)
				a++;
			else if (inAnswer[digitValueOfInput])
				b++;
		}

		return a * 10 + b;
	}

	public long getTimePassed()
	{
		Instant end = Instant.now(); //計時結束
		return Duration.between(begin, end).toSeconds();
	}

	public int getGuesses()
	{
		return guesses;
	}

	public static class ErrorCode
	{
		public static final int INVALID = -1;
		public static final int NOT_UNIQUE = -2;
	}
}