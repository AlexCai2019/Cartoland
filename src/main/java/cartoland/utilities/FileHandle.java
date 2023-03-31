package cartoland.utilities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
	static String buildJsonStringFromFile(@NotNull String fileName)
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

	static void synchronizeFile(@NotNull String fileName, @Nullable String content)
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

	public static void log(@NotNull String output)
	{
		LocalDate today = LocalDate.now(); //今天
		if (!today.isEqual(lastDateHasLog)) //如果今天跟上次有寫log的日期不同
		{
			Algorithm.updateSeed(); //在換日時更新種子
			lastDateHasLog = today;
			try
			{
				closeLog0();
				//一定要事先備好logs資料夾
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

	public static void log(@NotNull Throwable exception)
	{
		StackTraceElement[] exceptionMessage = exception.getStackTrace();
		String logString = Arrays.stream(exceptionMessage)
				.map(StackTraceElement::toString)
				.collect(Collectors.joining("\n"));
		log(logString);
	}

	private static void closeLog0() throws IOException
	{
		logger.close();
	}

	public static void closeLog()
	{
		try
		{
			closeLog0();
		}
		catch (IOException exception)
		{
			exception.printStackTrace();
			System.err.print('\u0007');
			System.exit(1);
		}
	}
}