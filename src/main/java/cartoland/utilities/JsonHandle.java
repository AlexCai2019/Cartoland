package cartoland.utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * {@code JsonHandle} is a utility class that handles all the need of JSON. It will load every JSON files that the bot need
 * at the beginning of process, and provide every information that the outer classes need.
 *
 * @since 1.0
 * @author Alex Cai
 */
public class JsonHandle
{
	private JsonHandle() {}

	public static final String USERS_JSON = "users.json";
	public static final String COMMAND_BLOCKS_JSON = "command_blocks.json";

	private static final JSONObject usersFile = new JSONObject(FileHandle.buildJsonStringFromFile(USERS_JSON)); //使用者的語言設定
	private static final JSONObject commandBlocksFile = new JSONObject(FileHandle.buildJsonStringFromFile(COMMAND_BLOCKS_JSON));
	private static final HashMap<String, JSONObject> languageFileMap = new HashMap<>();
	private static final StringBuilder builder = new StringBuilder();

	private static JSONObject file = null; //在lastUse中獲得這個ID對應的語言檔案 並在指令中使用
	private static JSONObject englishFile; //英文檔案
	private static String userIDString = null; //將ID轉換成字串

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

	static String getFileString(String fileName)
	{
		return switch (fileName)
		{
			case USERS_JSON -> usersFile.toString();
			case COMMAND_BLOCKS_JSON -> commandBlocksFile.toString();
			default -> "{}";
		};
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
		String jsonKey = typeCommandName + ".list";
		if (!englishFile.has(jsonKey))
			return null;
		return englishFile.getJSONArray(jsonKey).toList();
	}

	public static void reloadLanguageFiles()
	{
		languageFileMap.put(IDAndEntities.Languages.ENGLISH, englishFile = new JSONObject(FileHandle.buildJsonStringFromFile("lang/en.json")));
		languageFileMap.put(IDAndEntities.Languages.TW_MANDARIN, new JSONObject(FileHandle.buildJsonStringFromFile("lang/tw.json")));
		languageFileMap.put(IDAndEntities.Languages.TAIWANESE, new JSONObject(FileHandle.buildJsonStringFromFile("lang/ta.json")));
		languageFileMap.put(IDAndEntities.Languages.CANTONESE, new JSONObject(FileHandle.buildJsonStringFromFile("lang/hk.json")));
		languageFileMap.put(IDAndEntities.Languages.CHINESE, new JSONObject(FileHandle.buildJsonStringFromFile("lang/cn.json")));
		FileHandle.log("Reload all language json files");
	}

	public static void addCommandBlocks(long userID, long add)
	{
		userIDString = Long.toUnsignedString(userID);
		if (commandBlocksFile.has(userIDString))
		{
			long level = commandBlocksFile.getLong(userIDString);
			level += add;
			commandBlocksFile.put(userIDString, level >= 0 ? level : Long.MAX_VALUE); //避免溢位
		}
		else
			commandBlocksFile.put(userIDString, add);
	}

	public static void setCommandBlocks(long userID, long value)
	{
		userIDString = Long.toUnsignedString(userID);
		commandBlocksFile.put(userIDString, value);
	}

	public static long getCommandBlocks(long userID)
	{
		String userIDString = Long.toUnsignedString(userID);
		if (commandBlocksFile.has(userIDString))
			return commandBlocksFile.getLong(userIDString);
		commandBlocksFile.put(userIDString, 0L);
		return 0L;
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