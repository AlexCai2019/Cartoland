package cartoland.utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@code JsonHandle} is a utility class that handles all the need of JSON. It will load every JSON files that the bot need
 * at the beginning of process, and provide every information that the outer classes need. This is the only place
 * that imports {@link JSONArray} and {@link JSONObject}. Can not be instantiated or inherited.
 *
 * @since 1.0
 * @author Alex Cai
 */
public final class JsonHandle
{
	private JsonHandle()
	{
		throw new AssertionError(IDs.YOU_SHALL_NOT_ACCESS);
	}

	private static final String USERS_FILE_NAME = "serialize/users.ser";

	@SuppressWarnings({"rawtypes","unchecked"})
	private static final Map<Long, String> users = (FileHandle.deserialize(USERS_FILE_NAME) instanceof HashMap map) ? map : new HashMap<>(); //使用者的語言設定 id為key en, tw 等等的語言字串為value
	private static final Map<String, JSONObject> languageFileMap = new HashMap<>(7); //語言字串為key 語言檔案為value
	private static final Map<String, List<String>> commandListMap = new HashMap<>(4); //cmd.list等等為key 語言檔案對應的JSONArray為value
	private static final StringBuilder builder = new StringBuilder();

	private static JSONObject englishFile; //英文檔案

	static
	{
		reloadLanguageFiles();
		FileHandle.registerSerialize(USERS_FILE_NAME, users);
	}

	public static String command(long userID, String commandName)
	{
		builder.setLength(0);
		builder.append(getStringFromJsonKey(userID, commandName + ".begin")); //開頭 注意每個語言檔的指令裡一定要有.begin 否則會出現"null"
		JSONArray dotListArray = englishFile.getJSONArray(commandName + ".list"); //中間的資料 注意每個語言檔的指令裡一定要有.list 否則會擲出JSONException
		int dotListLength = dotListArray.length();
		if (dotListLength != 0) //建立回覆字串
		{
			for (int i = 0; ; i++)
			{
				builder.append(dotListArray.opt(i)); //如果超出JSON陣列的範圍 會出現"null"
				if (i + 1 == dotListLength) //已經是最後一個了
					break;
				builder.append(", ");
			}
		}
		return builder.append(getStringFromJsonKey(userID, commandName + ".end")).toString(); //結尾 注意每個語言檔的指令裡一定要有.end 否則會出現"null"
	}

	public static String command(long userID, String commandName, String argument)
	{
		String result = getStringFromJsonKey(userID, commandName + ".name." + argument);
		if ("lang".equals(commandName)) //如果使用的是/lang指令(或/language)
		{
			users.put(userID, argument); //更改語言
			return result; //結束
		}

		//"null"字串 代表獲取失敗
		return "null".equals(result) ? getStringFromJsonKey(userID, commandName + ".fail") : result; //注意每個語言檔的指令裡一定要有.fail 否則會出現"null"
	}

	public static List<String> commandList(String commandName)
	{
		return commandListMap.get(commandName + ".list");
	}

	private static List<String> buildStringListFromJsonArray(JSONArray jsonArray)
	{
		return jsonArray.toList()
				.stream()
				.map(CommonFunctions.stringValue)
				.toList();
	}

	public static void reloadLanguageFiles()
	{
		languageFileMap.put(Languages.ENGLISH, englishFile = new JSONObject(FileHandle.buildJsonStringFromFile("lang/en.json")));
		languageFileMap.put(Languages.TW_MANDARIN, new JSONObject(FileHandle.buildJsonStringFromFile("lang/tw.json")));
		languageFileMap.put(Languages.TAIWANESE, new JSONObject(FileHandle.buildJsonStringFromFile("lang/ta.json")));
		languageFileMap.put(Languages.CANTONESE, new JSONObject(FileHandle.buildJsonStringFromFile("lang/hk.json")));
		languageFileMap.put(Languages.CHINESE, new JSONObject(FileHandle.buildJsonStringFromFile("lang/cn.json")));
		languageFileMap.put(Languages.ESPANOL, new JSONObject(FileHandle.buildJsonStringFromFile("lang/es.json")));
		languageFileMap.put(Languages.JAPANESE, new JSONObject(FileHandle.buildJsonStringFromFile("lang/jp.json")));

		commandListMap.put("help.list", buildStringListFromJsonArray(englishFile.getJSONArray("help.list")));
		commandListMap.put("cmd.list",  buildStringListFromJsonArray(englishFile.getJSONArray("cmd.list")));
		commandListMap.put("faq.list",  buildStringListFromJsonArray(englishFile.getJSONArray("faq.list")));
		commandListMap.put("dtp.list",  buildStringListFromJsonArray(englishFile.getJSONArray("dtp.list")));
	}

	/**
	 * Get string from json file based on the ID of a user and a key.
	 *
	 * @param userID Determines which json file are going to access.
	 * @param key The key of a string in a json file.
	 * @return The string from the json file that key mapped.
	 * @since 1.4
	 * @author Alex Cai
	 */
	public static String getStringFromJsonKey(long userID, String key)
	{
		//程式設計原則 make the common case fast
		//這個函式還能再最佳化嗎?

		//獲取使用者設定的語言
		//找不到設定的語言就放台灣正體進去
		JSONObject file = languageFileMap.get(users.computeIfAbsent(userID, k -> Languages.TW_MANDARIN));
		Object optionalValue; //要獲得的字串(物件型態)
		String result; //要獲得的字串
		while (true)
		{
			optionalValue = file.opt(key); //之所以使用opt 是為了更快一些 getString還要檢查has

			//如果使用者的語言檔沒有這個key 就預設使用英文
			//之所以不用String.valueOf包住全部 而是只包englishFile.opt(key) 是因為只有它才需要valueOf optionalValue只需一個轉字串即可
			result = optionalValue != null ? optionalValue.toString() : String.valueOf(englishFile.opt(key));

			//注意.json檔內一定不能有空字串 否則charAt會擲出StringIndexOutOfBoundsException
			//為了讓機器人省去檢查 也為了省去動用result.startsWith 辛苦一下我們人類了
			if (result.charAt(0) == '&') //以&開頭的json key 代表要去那個地方找 (&在C/C++中代表reference)
				key = result.substring(1); //獲得新的key 進入下一輪迴圈
			else //並不是以&開頭
				return result; //result就是最終找到的結果了 直接結束 注意若沒找到 會回傳內容為"null"的字串
		}
	}
}