package cartoland.backend;

public interface GenericMessageHandle
{
    String commandProcess(String userID, String[] messages);
}
