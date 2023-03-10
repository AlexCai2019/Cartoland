package cartoland.utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

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

	public static final String USERS_JSON = "users.json";
	public static final String COMMAND_BLOCKS_JSON = "command_blocks.json";

	private static final JSONObject usersFile = new JSONObject(FileHandle.buildJsonStringFromFile(USERS_JSON)); //使用者的語言設定
	static final JSONObject commandBlocksFile = new JSONObject(FileHandle.buildJsonStringFromFile(COMMAND_BLOCKS_JSON));
	private static final HashMap<String, JSONObject> languageFileMap = new HashMap<>();
	private static final HashMap<String, List<Object>> commandListMap = new HashMap<>(); //讓commandList()方便呼叫
	private static final StringBuilder builder = new StringBuilder();

	private static JSONObject file; //在lastUse中獲得這個ID對應的語言檔案 並在指令中使用
	private static JSONObject englishFile; //英文檔案
	private static String userIDString; //將ID轉換成字串

	static
	{
		reloadLanguageFiles();
	}

	private static void lastUse(long userID)
	{
		userIDString = Long.toUnsignedString(userID);
		String userLanguage;

		if (usersFile.has(userIDString))
			userLanguage = usersFile.getString(userIDString); //獲取使用者設定的語言
		else //找不到設定的語言
			usersFile.put(userIDString, userLanguage = IDAndEntities.Languages.ENGLISH); //放英文進去

		file = languageFileMap.get(userLanguage);
	}

	public static void synchronizeFiles()
	{
		FileHandle.synchronizeFile(JsonHandle.USERS_JSON, usersFile.toString());
		FileHandle.synchronizeFile(JsonHandle.COMMAND_BLOCKS_JSON, commandBlocksFile.toString());
	}

	public static String command(long userID, String typeCommandName)
	{
		lastUse(userID);
		builder.setLength(0);
		builder.append(file.getString(typeCommandName + ".begin")); //開頭
		JSONArray dotListArray = englishFile.getJSONArray(typeCommandName + ".list");
		dotListArray.forEach(s -> builder.append((String) s).append(' '));
		builder.append(file.getString(typeCommandName + ".end")); //結尾
		return builder.toString();
	}

	public static String command(long userID, String typeCommandName, String argument)
	{
		lastUse(userID);
		String jsonKey = typeCommandName + ".name." + argument;
		JSONObject hasKeyFile;
		if (file.has(jsonKey)) //如果有這個key
			hasKeyFile = file;
		else if (englishFile.has(jsonKey)) //預設使用英文
			hasKeyFile = englishFile;
		else
			return file.getString(typeCommandName + ".fail");

		if (typeCommandName.equals("lang"))
			usersFile.put(userIDString, argument);

		String result = hasKeyFile.getString(jsonKey); //要獲得的字串
		if (result.charAt(0) == '&') //以&開頭的json key 代表要去那個地方找
			return hasKeyFile.getString(result.substring(1));
		return result;
	}

	public static List<Object> commandList(String typeCommandName)
	{
		return commandListMap.get(typeCommandName + ".list");
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

	public static String getJsonKey(long userID, String key)
	{
		lastUse(userID);
		if (file.has(key))
			return file.getString(key);
		else if (englishFile.has(key))
			return englishFile.getString(key);
		else
			return "";
	}
}