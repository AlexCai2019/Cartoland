package cartoland.utilities;

import java.util.List;
import java.util.Random;

/**
 * {@code Algorithm} is a class that provides functions that helps calculate. Can not be instantiated or inherited.
 *
 * @since 1.5
 * @author Alex Cai
 */
public final class Algorithm
{
	/**
	 * Private constructor to prevent instantiated.
	 *
	 * @throws AssertionError Always, prevent reflection.
	 * @since 1.5
	 * @author Alex Cai
	 */
	private Algorithm()
	{
		throw new AssertionError(IDs.YOU_SHALL_NOT_ACCESS);
	}

	/**
	 * The random core of all functions that related to random and chance.
	 */
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
		for (int index = 0, endIndex = array.length - 1, destIndex, temp; index < endIndex; index++) //到endIndex為止 因為最後一項沒必要交換
		{
			destIndex = random.nextInt(array.length - index) + index; //0會得到0 ~ endIndex 1會得到1 ~ endIndex 2會得到2 ~ endIndex
			//交換
			temp = array[destIndex];
			array[destIndex] = array[index];
			array[index] = temp;
		}
	}

	/**
	 * Shuffle an array.
	 *
	 * @param array The array that need to shuffle.
	 * @since 2.1
	 * @author Alex Cai
	 */
	public static void shuffle(Object[] array)
	{
		Object temp;
		for (int index = 0, endIndex = array.length - 1, destIndex; index < endIndex; index++) //到endIndex為止 因為最後一項沒必要交換
		{
			destIndex = random.nextInt(array.length - index) + index; //0會得到0~endIndex 1會得到1~endIndex 2會得到2~endIndex
			//交換
			temp = array[destIndex];
			array[destIndex] = array[index];
			array[index] = temp;
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
		return sum >= 0 ? sum : Long.MAX_VALUE; //避免溢位
	}

	/**
	 * Returns if it passed based on the percent parameter as percentage.<br>
	 * Usage:
	 * <pre>
	 *     if (Algorithm.chance(50)) //50% of pass
	 *     if (Algorithm.chance(99)) //99% of pass
	 *     if (Algorithm.chance(100)) //equals to if (true)
	 *     if (Algorithm.chance(0)) //equals to if (false)
	 * </pre>
	 *
	 * @param percent the chance of passing, range is from zero to 100.
	 * @return if the random test passed.
	 * @since 1.5
	 * @author Alex Cai
	 */
	public static boolean chance(int percent)
	{
		return percent > random.nextInt(100);
	}

	/**
	 * Returns a random element of an array. Use generic to avoid unnecessary cast.
	 *
	 * @param array The array that are going to return a random element.
	 * @param <T> Type of elements.
	 * @return A random element of the array.
	 * @since 2.0
	 * @author Alex Cai
	 */
	public static<T> T randomElement(T[] array)
	{
		return array[random.nextInt(array.length)];
	}

	/**
	 * Returns a random element of an array.
	 *
	 * @param array The array that are going to return a random element.
	 * @return A random element of the array.
	 * @since 2.1
	 * @author Alex Cai
	 */
	public static int randomElement(int[] array)
	{
		return array[random.nextInt(array.length)];
	}

	/**
	 * Returns a random element of an array.
	 *
	 * @param array The array that are going to return a random element.
	 * @return A random element of the array.
	 * @since 2.2
	 * @author Alex Cai
	 */
	public static long randomElement(long[] array)
	{
		return array[random.nextInt(array.length)];
	}

	/**
	 * Returns a random element of a list.
	 *
	 * @param list The list that need to shuffle
	 * @param <T> Type of elements.
	 * @since 2.2
	 * @author Alex Cai
	 */
	public static<T> T randomElement(List<T> list)
	{
		return list.get(random.nextInt(list.size()));
	}

	/**
	 * Create a string from the value of a double without trailing zeros.
	 * <pre>
	 *     String s1 = Algorithm.cleanFPString("5.5"); //5.5
	 *     String s2 = Algorithm.cleanFPString("5.0"); //5
	 *     String s3 = Algorithm.cleanFPString("1.500"); //1.5
	 * </pre>
	 *
	 * @param fpString The string that are going to trim the trailing zeros.
	 * @return The string of duration without trailing zeros.
	 * @since 2.1
	 * @author Alex Cai
	 */
	public static String cleanFPString(String fpString)
	{
		int dotIndex = fpString.indexOf('.'); //小數點的索引
		int headOfTrailingZeros = fpString.length(); //將會是小數部分最後的一串0中 第一個0

		//從最後一個字元開始 一路往左到小數點
		int index;
		for (index = headOfTrailingZeros - 1; index >= dotIndex; index--) //開始時 headOfTrailingZeros為字串長度 index則為字串長度 - 1
		{
			if (fpString.charAt(index) != '0') //如果發現第一個不是0的數
			{
				headOfTrailingZeros = index + 1; //那就說明這個索引的右邊 一定末端連續0的第一個
				break; //找到了 結束
			}
		}

		//結果:
		//5.000000 => 5
		//1.500000 => 1.5
		//從第一個數字開始 一路到連續0的第一個 如果小數點後都是連續0 那就連小數點都不要了
		return fpString.substring(0, (index == dotIndex) ? dotIndex : headOfTrailingZeros); //經歷過for迴圈 此時index必定是連續0開頭的左邊那個
	}
}