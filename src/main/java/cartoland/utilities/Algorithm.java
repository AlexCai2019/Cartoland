package cartoland.utilities;

import java.util.Random;

/**
 * {@code Algorithm} is a class that provides functions that helps calculate.
 *
 * @since 1.5
 * @author Alex Cai
 */
public class Algorithm
{
	private Algorithm()
	{
		throw new AssertionError(IDAndEntities.YOU_SHALL_NOT_ACCESS);
	}

	private static final Random random = new Random();

	/**
	 * Shuffle an array.
	 *
	 * @param array The array that need to shuffle.
	 * @since 1.5
	 * @author Alex Cai
	 */
	public static void shuffle(int[] array)
	{
		int endIndex = array.length - 1;
		int temp;
		for (int i = 0, destIndex; i < endIndex; i++) //到endIndex為止 因為最後一項項沒必要交換
		{
			destIndex = random.nextInt(array.length - i) + i; //0會得到0~endIndex 1會得到1~endIndex 2會得到2~endIndex
			//交換
			temp = array[destIndex];
			array[destIndex] = array[i];
			array[i] = temp;
		}
	}

	/**
	 * Add two long integers without overflow.
	 *
	 * @param augend augend
	 * @param addend addend
	 * @return sum
	 * @since 1.5
	 * @author Alex Cai
	 */
	public static long safeAdd(long augend, long addend)
	{
		long sum = augend + addend;
		if (sum < 0)
			sum = Long.MAX_VALUE; //避免溢位
		return sum;
	}

	public static boolean chance(int percent, Random random)
	{
		return percent > random.nextInt(100);
	}
}