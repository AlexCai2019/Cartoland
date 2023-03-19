package cartoland.utilities;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * {@code FileHandle} is a utility class that provides every functions that this program need to deal with file input and
 * output. Can not be instantiated.
 *
 * @since 1.0
 * @author Alex Cai
 */
public class FileHandle
{
	private FileHandle()
	{
		throw new AssertionError(IDAndEntities.YOU_SHALL_NOT_ACCESS);
	}

	private static LocalDate lastDateHasLog; //上一次有寫log的日期
	private static FileWriter logger;

	static
	{
		lastDateHasLog = LocalDate.now();
		try
		{
			//一定要事先備好logs資料夾
			logger = new FileWriter("logs/" + lastDateHasLog, true);
		}
		catch (IOException exception)
		{
			exception.printStackTrace();
			System.err.print('\u0007');
			System.exit(1);
		}
	}

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
			System.err.print('\u0007');
			log(exception);
			return "{}";
		}
	}

	public static void synchronizeFile(String fileName, String content)
	{
		if (content == null)
			content = "{}";

		try
		{
			FileWriter writer = new FileWriter(fileName); //同步到檔案裡
			writer.write(content);
			writer.close();
		}
		catch (IOException exception)
		{
			exception.printStackTrace();
			System.err.print('\u0007');
			log(exception);
			System.exit(1);
		}
	}

	public static void log(String output)
	{
		LocalDate today = LocalDate.now(); //今天
		if (!today.isEqual(lastDateHasLog)) //如果今天跟上次有寫log的日期不同
		{
			lastDateHasLog = today;
			try
			{
				//一定要事先備好logs資料夾
				logger.close();
				logger = new FileWriter("logs/" + today, true);
			}
			catch (IOException exception)
			{
				exception.printStackTrace();
				System.err.print('\u0007');
				System.exit(1);
			}
		}

		LocalTime now = LocalTime.now(); //現在
		//時間 內容
		String logString = String.format("%02d:%02d:%02d\t%s\n", now.getHour(), now.getMinute(), now.getSecond(), output);
		try
		{
			logger.write(logString);
		}
		catch (IOException exception)
		{
			exception.printStackTrace();
			System.err.print('\u0007');
			System.exit(1);
		}
	}

	public static void log(Throwable exception)
	{
		StackTraceElement[] exceptionMessage = exception.getStackTrace();
		String logString = Arrays.stream(exceptionMessage)
				.map(StackTraceElement::toString)
				.collect(Collectors.joining("\n"));
		log(logString);
	}

	public static void closeLog()
	{
		try
		{
			logger.close();
		}
		catch (IOException exception)
		{
			exception.printStackTrace();
			System.err.print('\u0007');
			System.exit(1);
		}
	}
}