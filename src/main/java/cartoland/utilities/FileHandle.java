package cartoland.utilities;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

/**
 * @since 1.0
 * @author Alex Cai
 */
public class FileHandle
{
	private FileHandle() {}

	//將JSON讀入進字串
	static String buildJsonStringFromFile(String fileName)
	{
		try
		{
			return Files.readString(Paths.get(fileName));
		}
		catch (IOException exception)
		{
			exception.printStackTrace();
			log(exception);
			return "{}";
		}
	}

	public static void synchronizeFile(String fileName)
	{
		String fileContent = JsonHandle.getFileString(fileName);
		try
		{
			FileWriter writer = new FileWriter(fileName); //同步到檔案裡
			writer.write(fileContent != null ? fileContent : "{}");
			writer.close();
		}
		catch (IOException exception)
		{
			exception.printStackTrace();
			log(exception);
			System.exit(1);
		}
	}

	public static void log(String output)
	{
		LocalTime now = LocalTime.now();
		//時間 內容
		String logString = String.format("%02d:%02d:%02d\t%s\n", now.getHour(), now.getMinute(), now.getSecond(), output);
		try
		{
			//一定要事先備好logs資料夾
			FileWriter logWriter = new FileWriter("logs/" + LocalDate.now(), true);
			logWriter.write(logString);
			logWriter.close();
		}
		catch (IOException exception)
		{
			exception.printStackTrace();
			System.exit(1);
		}
	}

	public static void log(Exception exception)
	{
		log(Arrays.toString(exception.getStackTrace()));
	}
}