package cartoland.utilities;

import cartoland.Cartoland;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@code FileHandle} is a utility class that provides every functions that this program need to deal with file input and
 * output. Including logger, json build and serialize. Can not be instantiated or inherited.
 *
 * @since 1.0
 * @author Alex Cai
 */
public final class FileHandle
{
	private FileHandle()
	{
		throw new AssertionError(IDs.YOU_SHALL_NOT_ACCESS);
	}

	private static FileWriter logger = null;

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

	private static final List<SerializeObject> serializeObjects = new ArrayList<>();

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
		try (FileOutputStream fileStream = new FileOutputStream(fileName);
			 ObjectOutputStream objectStream = new ObjectOutputStream(fileStream))
		{
			objectStream.writeObject(object);
			objectStream.flush();
		}
		catch (IOException exception)
		{
			exception.printStackTrace();
			log(exception);
		}
	}

	public static Object deserialize(String fileName)
	{
		Object object;
		try (FileInputStream fileStream = new FileInputStream(fileName);
			 ObjectInputStream objectStream = new ObjectInputStream(fileStream))
		{
			object = objectStream.readObject();
		}
		catch (IOException | ClassNotFoundException exception)
		{
			exception.printStackTrace();
			log(exception);
			return null;
		}
		return object;
	}

	public static void changeLogDate()
	{
		try
		{
			closeLog0();
			startLog0();
		}
		catch (IOException exception)
		{
			exception.printStackTrace();
			Cartoland.getJDA().shutdownNow();
		}
	}

	public static void log(String output)
	{
		//時間 內容
		String logString = TimerHandle.getTimeString() + '\t' + output + '\n';
		try
		{
			if (logger == null)
				startLog0();
			logger.write(logString);
		}
		catch (IOException exception)
		{
			exception.printStackTrace();
			Cartoland.getJDA().shutdownNow();
		}
	}

	public static void log(Exception exception)
	{
		log(Arrays.stream(exception.getStackTrace())
					.map(CommonFunctions.stringValue)
					.collect(Collectors.joining("\n")));
	}

	private static void startLog0() throws IOException
	{
		//一定要事先備好logs資料夾
		logger = new FileWriter("logs/" + TimerHandle.getDateString(), true);
	}

	public static void startLog()
	{
		try
		{
			startLog0();
		}
		catch (IOException exception)
		{
			exception.printStackTrace();
			Cartoland.getJDA().shutdownNow();
		}
	}

	private static void closeLog0() throws IOException
	{
		if (logger != null)
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
			Cartoland.getJDA().shutdownNow();
		}
	}

	private static record SerializeObject(String fileName, Object object) {}
}