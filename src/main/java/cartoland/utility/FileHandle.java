package cartoland.utility;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

public class FileHandle
{
    //將JSON讀入進字串
    static String buildJsonStringFromFile(String fileName)
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

    static void synchronizeUsersFile(String usersFileString)
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

    public static void logIntoFile(String output)
    {
        try
        {
            //一定要事先備好logs資料夾
            FileWriter logWriter = new FileWriter("logs/" + LocalDate.now(), true);
            logWriter.write(LocalTime.now() + "\t" + output + "\n"); //時間 內容 換行
            logWriter.close();
        }
        catch (IOException e)
        {
            e.printStackTrace(System.err);
            System.exit(-1);
        }
    }

    private static final Random random = new Random();
    private static final Path megumin = Paths.get("megumin");
    public static String readRandomMeguminUrl()
    {
        int lineNumber = random.nextInt(350);

        try
        {
            Stream<String> lines = Files.lines(megumin);
            Optional<String> line = lines.skip(lineNumber).findFirst();
            return line.orElse("pensukeo/status/1184557949714219009");
        }
        catch (IOException e)
        {
            e.printStackTrace(System.err);
            System.exit(-1);
        }

        return "pensukeo/status/1184557949714219009";
    }
}
