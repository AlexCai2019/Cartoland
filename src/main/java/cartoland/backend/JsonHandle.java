package cartoland.backend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class JsonHandle
{
    private final FileHandle fileHandle = new FileHandle();
    private final JSONObject usersFile = new JSONObject(fileHandle.buildJsonStringFromFile("users.json")); //使用者的語言設定
    private final HashMap<Integer, JSONObject> languageFileMap= new HashMap<>();

    private JSONObject file = null; //在lastUse中獲得這個ID對應的語言檔案 並在指令中使用
    private String id = null;

    JsonHandle()
    {
        languageFileMap.put(Languages.ENGLISH, new JSONObject(fileHandle.buildJsonStringFromFile("lang/en.json")));
        languageFileMap.put(Languages.TW_MANDARIN, new JSONObject(fileHandle.buildJsonStringFromFile("lang/tw.json")));
        languageFileMap.put(Languages.TAIWANESE, new JSONObject(fileHandle.buildJsonStringFromFile("lang/ta.json")));
        languageFileMap.put(Languages.CANTONESE, new JSONObject(fileHandle.buildJsonStringFromFile("lang/hk.json")));
        languageFileMap.put(Languages.CHINESE, new JSONObject(fileHandle.buildJsonStringFromFile("lang/cn.json")));
    }
    void lastUse(String userID)
    {
        int userLanguage;

        if (usersFile.has(userID))
            userLanguage = usersFile.getInt(userID); //獲取使用者設定的語言
        else //找不到設定的語言
            usersFile.put(userID, userLanguage = Languages.ENGLISH); //放英文進去

        file = languageFileMap.get(userLanguage);
        id = userID;
    }

    String command(String typeCommandName)
    {
        System.out.println("User " + id + " used " + typeCommandName);
        fileHandle.logIntoFile("User " + id + " used " + typeCommandName);

        StringBuilder builder = new StringBuilder();
        builder.append(file.getString(typeCommandName + ".begin")); //開頭
        JSONArray dotListArray = file.getJSONArray(typeCommandName + ".list");
        for (int i = 0; i < dotListArray.length(); i++) //所有.list內的內容
            builder.append(dotListArray.getString(i)).append(' ');
        builder.append(file.getString(typeCommandName + ".end")); //結尾
        return builder.toString();
    }

    String command(String typeCommandName, String argument)
    {
        System.out.println("User " + id + " used " + typeCommandName + " " + argument);
        fileHandle.logIntoFile("User " + id + " used " + typeCommandName + " " + argument);

        String fileKey = typeCommandName + ".name." + argument;
        if (file.has(fileKey))
        {
            if (typeCommandName.equals("lang"))
            {
                usersFile.put(id, Integer.parseInt(argument));
                fileHandle.synchronizeUsersFile(usersFile.toString());
            }
            return file.getString(typeCommandName + ".name." + argument);
        }
        else
            return file.getString(typeCommandName + ".fail");
    }
}

class Languages
{
    static final int ENGLISH = 0;
    static final int TW_MANDARIN = 1;
    static final int TAIWANESE = 2;
    static final int CANTONESE = 3;
    static final int CHINESE = 4;
}