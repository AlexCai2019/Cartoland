package cartoland.backend;

public class AdminHandle implements GenericMessageHandle
{
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
