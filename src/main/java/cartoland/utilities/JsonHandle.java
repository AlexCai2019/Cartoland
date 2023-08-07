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

	@SuppressWarnings("unchecked")
	private static final Map<Long, String> users = (FileHandle.deserialize(USERS_FILE_NAME) instanceof HashMap map) ? map : new HashMap<>(); //使用者的語言設定 id為key en, tw 等等的語言字串為value
	private static final Map<String, JSONObject> languageFileMap = new HashMap<>(7); //語言字串為key 語言檔案為value
	private static final Map<String, List<String>> commandListMap = new HashMap<>(4); //cmd.list等等為key 語言檔案對應的JSONArray為value
	private static final StringBuilder builder = new StringBuilder();

	private static JSONObject file; //在lastUse中獲得這個ID對應的語言檔案 並在指令中使用
	private static JSONObject englishFile; //英文檔案

	static
	{
		reloadLanguageFiles();
		FileHandle.registerSerialize(USERS_FILE_NAME, users);
	}

	private static void lastUse(long userID)
	{
		//獲取使用者設定的語言
		//找不到設定的語言就放英文進去
		file = languageFileMap.get(users.computeIfAbsent(userID, k -> Languages.ENGLISH));
	}

	public static String command(long userID, String commandName)
	{
		lastUse(userID);
		builder.setLength(0);
		builder.append(file.getString(commandName + ".begin")); //開頭 注意每個語言檔的指令裡一定要有.begin 否則會擲出JSONException
		JSONArray dotListArray = englishFile.getJSONArray(commandName + ".list"); //中間的資料 注意每個語言檔的指令裡一定要有.list 否則會擲出JSONException
		int dotListLength = dotListArray.length();
		if (dotListLength != 0) //建立回覆字串
		{
			for (int i = 0; ; i++)
			{
				builder.append(dotListArray.getString(i));
				if (i + 1 == dotListLength) //已經是最後一個了
					break;
				builder.append(", ");
			}
		}
		return builder.append(file.getString(commandName + ".end")).toString(); //結尾 注意每個語言檔的指令裡一定要有.end 否則會擲出JSONException
	}

	public static String command(long userID, String commandName, String argument)
	{
		String result = getStringFromJsonKey(userID, commandName + ".name." + argument);
		if ("lang".equals(commandName)) //如果使用的是/lang指令(或/language)
		{
			users.put(userID, argument); //更改語言
			return result; //結束
		}

		//空字串代表獲取失敗
		return result.isEmpty() ? file.getString(commandName + ".fail") : result; //注意每個語言檔的指令裡一定要有.fail 否則會擲出JSONException
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

	public static String getStringFromJsonKey(long userID, String key)
	{
		//程式設計原則 make the common case fast
		//這個函式還能再最佳化嗎?
		lastUse(userID);
		String result; //要獲得的字串
		while (true)
		{
			result = file.optString(key, ""); //之所以使用optString 是為了更快一些 getString還要檢查has isEmpty只需檢查字串長度 == 0
			if (result.isEmpty()) //如果沒有這個key
				result = englishFile.optString(key, ""); //預設使用英文

			//注意.json檔內一定不能有空字串 否則charAt會擲出StringIndexOutOfBoundsException
			//為了讓機器人省去檢查 辛苦一下我們人類了
			if (result.isEmpty() || result.charAt(0) != '&') //連英文都找不到 或 找到了且不是以&開頭 (&開頭代表reference)
				break; //結束

			//以&開頭的json key 代表要去那個地方找
			key = result.substring(1);
		}

		return result; //最終找到的結果 注意若沒找到 會回傳空字串
	}
}