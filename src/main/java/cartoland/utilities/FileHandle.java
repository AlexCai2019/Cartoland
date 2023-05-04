package cartoland.utilities;

import java.io.*;
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
			IDAndEntities.jda.shutdownNow();
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

	public static void serialize(String fileName, Object object)
	{
		if (!(object instanceof Serializable))
			return;
		try
		{
			FileOutputStream fileStream = new FileOutputStream(fileName);
			ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
			objectStream.writeObject(object);
			objectStream.flush();
			objectStream.close();
			fileStream.close();
		}
		catch (IOException exception)
		{
			exception.printStackTrace();
			System.err.print('\u0007');
			log(exception);
			IDAndEntities.jda.shutdownNow();
		}
	}

	public static Object deserialize(String fileName)
	{
		Object object = null;

		try
		{
			FileInputStream fileStream = new FileInputStream(fileName);
			ObjectInputStream objectStream = new ObjectInputStream(fileStream);
			object = objectStream.readObject();
			objectStream.close();
			fileStream.close();
		}
		catch (IOException | ClassNotFoundException exception)
		{
			exception.printStackTrace();
			System.err.print('\u0007');
			log(exception);
			IDAndEntities.jda.shutdownNow();
		}

		return object;
	}

	public static void log(String output)
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
				IDAndEntities.jda.shutdownNow();
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
			IDAndEntities.jda.shutdownNow();
		}
	}

	public static void log(Exception exception)
	{
		log(Arrays.stream(exception.getStackTrace())
					.map(StackTraceElement::toString)
					.collect(Collectors.joining("\n")));
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
			IDAndEntities.jda.shutdownNow();
		}
	}
}