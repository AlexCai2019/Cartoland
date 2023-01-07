package cartoland.backend;

public class AdminHandle implements GenericMessageHandle
{
    //TODO: 考慮重新設計前後端系統 藉此完善管理員控制
    @Override
    public String commandProcess(String userID, String[] messages)
    {
        switch (messages[0])
        {
            case ".shutdown":
                    System.exit(0);
                return "Bot shutting down";

            case ".echo":
                return messages[1];

            case ".list":


            default:
                return null;
        }
    }
}
