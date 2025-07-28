package cartoland.utilities;

public interface IStringHelper
{
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
	default String cleanFPString(String fpString)
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