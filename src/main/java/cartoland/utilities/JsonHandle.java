package cartoland.utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@code JsonHandle} is a utility class that handles all the need of JSON. It will load every JSON files that the bot need
 * at the beginning of process, and provide every information that the outer classes need. This is the only place
 * that imports {@link JSONArray} and {@link JSONObject}. Can not be instantiated.
 *
 * @since 1.0
 * @author Alex Cai
 */
public class JsonHandle
{
	private JsonHandle()
	{
		throw new AssertionError(IDAndEntities.YOU_SHALL_NOT_ACCESS);
	}

	private static final String USERS_FILE_NAME = "users.ser";

	private static final Map<Long, String> users; //使用者的語言設定 id為key en, tw 等等的語言字串為value
	private static final Map<String, JSONObject> languageFileMap = new HashMap<>(); //語言字串為key 語言檔案為value
	private static final Map<String, List<Object>> commandListMap = new HashMap<>(); //cmd.list等等為key 語言檔案對應的JSONArray為value
	private static final StringBuilder builder = new StringBuilder();

	private static JSONObject file; //在lastUse中獲得這個ID對應的語言檔案 並在指令中使用
	private static JSONObject englishFile; //英文檔案

	static
	{
		reloadLanguageFiles();
		users = (FileHandle.deserialize(USERS_FILE_NAME) instanceof HashMap map) ? map : new HashMap<>();
	}

	private static void lastUse(long userID)
	{
		//獲取使用者設定的語言
		//找不到設定的語言就放英文進去
		file = languageFileMap.get(users.computeIfAbsent(userID, k -> IDAndEntities.Languages.ENGLISH));
	}

	public static void serializeUsersMap()
	{
		FileHandle.serialize(USERS_FILE_NAME, (HashMap<Long, String>) users);
	}

	public static String command(long userID, String commandName)
	{
		lastUse(userID);
		builder.setLength(0);
		builder.append(file.getString(commandName + ".begin")); //開頭
		JSONArray dotListArray = englishFile.getJSONArray(commandName + ".list");
		dotListArray.forEach(s -> builder.append((String) s).append(' '));
		builder.deleteCharAt(builder.length() - 1);
		builder.append(file.getString(commandName + ".end")); //結尾
		return builder.toString();
	}

	public static String command(long userID, String commandName, String argument)
	{
		if (commandName.equals("lang"))
			users.put(userID, argument);

		String result = getStringFromJsonKey(userID, commandName + ".name." + argument);

		//空字串代表獲取失敗
		return result.isEmpty() ? file.getString(commandName + ".fail") : result;
	}

	public static List<Object> commandList(String commandName)
	{
		return commandListMap.get(commandName + ".list");
	}

	public static void reloadLanguageFiles()
	{
		languageFileMap.put(IDAndEntities.Languages.ENGLISH, englishFile = new JSONObject(FileHandle.buildJsonStringFromFile("lang/en.json")));
		languageFileMap.put(IDAndEntities.Languages.TW_MANDARIN, new JSONObject(FileHandle.buildJsonStringFromFile("lang/tw.json")));
		languageFileMap.put(IDAndEntities.Languages.TAIWANESE, new JSONObject(FileHandle.buildJsonStringFromFile("lang/ta.json")));
		languageFileMap.put(IDAndEntities.Languages.CANTONESE, new JSONObject(FileHandle.buildJsonStringFromFile("lang/hk.json")));
		languageFileMap.put(IDAndEntities.Languages.CHINESE, new JSONObject(FileHandle.buildJsonStringFromFile("lang/cn.json")));

		commandListMap.put("cmd.list", englishFile.getJSONArray("cmd.list").toList());
		commandListMap.put("faq.list", englishFile.getJSONArray("faq.list").toList());
		commandListMap.put("dtp.list", englishFile.getJSONArray("dtp.list").toList());

		//FileHandle.log("Reload all language json files");
	}

	public static String getStringFromJsonKey(long userID, String key)
	{
		lastUse(userID);
		String result; //要獲得的字串
		if (file.has(key)) //如果有這個key
			result = file.getString(key);
		else if (englishFile.has(key)) //預設使用英文
			result = englishFile.getString(key);
		else
			result = "";

		//以&開頭的json key 代表要去那個地方找
		return (result.charAt(0) == '&') ? getStringFromJsonKey(userID, result.substring(1)) : result;
	}
}