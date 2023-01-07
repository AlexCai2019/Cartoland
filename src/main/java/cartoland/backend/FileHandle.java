package cartoland.backend;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;

public class FileHandle
{
    //將JSON讀入進字串
    String buildJsonStringFromFile(String fileName)
    {
        try
        {
            return new String(Files.readAllBytes(Paths.get(fileName)));
        }
        catch (IOException exception)
        {
            exception.printStackTrace(System.err);
            System.exit(-1);
        }

        return null;
    }

    void synchronizeUsersFile(String usersFileString)
    {
        try
        {
            FileWriter writer = new FileWriter("users.json"); //同步到檔案裡
            writer.write(usersFileString);
            writer.close();
        }
        catch (IOException exception)
        {
            exception.printStackTrace(System.err);
            System.exit(-1);
        }
    }

    void logIntoFile(String output)
    {
        try
        {
            //一定要事先備好logs資料夾
            FileWriter logWriter = new FileWriter("logs/" + LocalDate.now(), true);
            logWriter.write(LocalTime.now() + " " + output + "\n"); //時間 內容 換行
            logWriter.close();
        }
        catch (IOException e)
        {
            e.printStackTrace(System.err);
            System.exit(-1);
        }
    }
}
