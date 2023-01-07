package cartoland.backend;

public class MessageHandle implements GenericMessageHandle
{
    private final JsonHandle jsonHandle = new JsonHandle();

    @Override
    public String commandProcess(String userID, String[] messages)
    {
        String commandName; //指令名稱

        switch (messages[0])
        {
            case ".invite":
                return "https://discord.gg/UMYxwHyRNE";

            case ".cmd":
                commandName = "cmd";
                break;

            case ".faq":
                commandName = "faq";
                break;

            case ".datapack":
            case ".dtp":
                commandName = "dtp";
                break;

            case ".lang":
            case ".language":
                commandName = "lang";
                break;

            default: //不是輸入指令
                return null;
        }

        jsonHandle.lastUse(userID);

        if (messages.length == 1) //如果指令沒有參數
            return jsonHandle.command(commandName);
        return jsonHandle.command(commandName, messages[1]); //指令有一個參數
    }
}