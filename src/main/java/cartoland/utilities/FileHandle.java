package cartoland.utilities;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@code FileHandle} is a utility class that provides every functions that this program need to deal with file input and
 * output. Including logger, json build and serialize. Can not be instantiated.
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
	private static final List<SerializeObject> serializeObjects = new ArrayList<>();

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
			log(exception);
			return "{}";
		}
	}

	/**
	 * Register an object to the {@link #serializeObjects} list, then the objects in that list will be serialized by
	 * {@link #serialize} when {@link cartoland.events.BotOnlineOffline#onShutdown} was executed. Be aware
	 * that the object must implement {@link Serializable} interface. Most importantly, this object must be <b>final</b>,
	 * since Java doesn't have double pointer, there's no way to serialize correct contents if the class reference
	 * changed its pointing address.
	 *
	 * @param fileName The name of the serialize file. Usually has {@code .ser} as file name extension.
	 * @param object The object that is going to be serialized.
	 * @since 2.0
	 * @author Alex Cai
	 */
	public static void registerSerialize(String fileName, Object object)
	{
		serializeObjects.add(new SerializeObject(fileName, object));
	}

	/**
	 * This method will be call when {@link cartoland.events.BotOnlineOffline#onShutdown} was executed.
	 * It will serialize every objects in {@link #serializeObjects}, which was registered by {@link #registerSerialize}.
	 *
	 * @since 2.0
	 * @author Alex Cai
	 */
	public static void serialize()
	{
		for (SerializeObject so : serializeObjects)
			serialize(so.fileName, so.object);
	}

	private static void serialize(String fileName, Object object)
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
			log(exception);
			IDAndEntities.jda.shutdownNow();
		}
	}

	public static Object deserialize(String fileName)
	{
		try
		{
			FileInputStream fileStream = new FileInputStream(fileName);
			ObjectInputStream objectStream = new ObjectInputStream(fileStream);
			Object object = objectStream.readObject();
			objectStream.close();
			fileStream.close();
			return object;
		}
		catch (IOException | ClassNotFoundException exception)
		{
			exception.printStackTrace();
			log(exception);
			return null;
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
				closeLog0();
				//一定要事先備好logs資料夾
				logger = new FileWriter("logs/" + today, true);
			}
			catch (IOException exception)
			{
				exception.printStackTrace();
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
			IDAndEntities.jda.shutdownNow();
		}
	}

	private static record SerializeObject(String fileName, Object object) {}
}