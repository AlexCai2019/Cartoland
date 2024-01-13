package cartoland.utilities;

import cartoland.Cartoland;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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

	private static final StringBuilder logString = new StringBuilder();

	//將JSON讀入進字串
	static String buildJsonStringFromFile(String fileName)
	{
		try
		{
			return Files.readString(Paths.get(fileName)); //從指定的檔名中讀取全部的資料 並將這些資料做成一個字串
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
	 * {@link #serialize()} when {@link cartoland.events.BotOnlineOffline#onShutdown(net.dv8tion.jda.api.events.session.ShutdownEvent)}
	 * was executed. Be aware that the object must implement {@link Serializable} interface. Most importantly, this object
	 * must be <b>final</b>, since Java doesn't have double pointer, there's no way to serialize correct contents if the
	 * class reference changed its pointing address.
	 *
	 * @param fileName The name of the serialize file. Usually has {@code .ser} as file name extension.
	 * @param object The object that is going to be serialized.
	 * @since 2.0
	 * @author Alex Cai
	 */
	public static void registerSerialize(String fileName, Object object)
	{
		if (object instanceof Serializable)
			serializeObjects.add(new SerializeObject(fileName, object)); //向註冊清單中新增一個註冊物件
	}

	/**
	 * This method will be call when {@link cartoland.events.BotOnlineOffline#onShutdown(net.dv8tion.jda.api.events.session.ShutdownEvent)}
	 * was executed. It will serialize every objects in {@link #serializeObjects}, which was registered by {@link #registerSerialize(String, Object)}.
	 *
	 * @since 2.0
	 * @author Alex Cai
	 */
	public static void serialize()
	{
		for (SerializeObject so : serializeObjects)
			so.serialize();
	}

	/**
	 * Deserialize an object from a file.
	 *
	 * @param fileName The name of the file that stores the object.
	 * @return The object that deserialize from a file.
	 * @since 2.0
	 * @author Alex Cai
	 */
	public static Object deserialize(String fileName)
	{
		try (FileInputStream fileStream = new FileInputStream(fileName); //從檔名建立檔案串流
			 ObjectInputStream objectStream = new ObjectInputStream(fileStream)) //從檔案串流建立物件串流
		{
			return objectStream.readObject(); //從物件串流讀取物件
		}
		catch (IOException | ClassNotFoundException exception)
		{
			exception.printStackTrace();
			log(exception);
			return null; //讀不到就回傳null
		}
	}

	private record SerializeObject(String fileName, Object object)
	{
		private void serialize()
		{
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
	}

	public synchronized static void flushLog()
	{
		//一定要事先備好logs資料夾
		try (FileWriter logger = new FileWriter("logs/" + TimerHandle.getDateString(), true))
		{
			logger.write(logString.toString()); //關閉時寫入
			logString.setLength(0); //清空暫存
		}
		catch (IOException exception)
		{
			exception.printStackTrace();
			Cartoland.getJDA().shutdownNow();
		}
	}

	public synchronized static void log(Object... outputs)
	{
		//時間 內容
		logString.append(TimerHandle.getTimeString()).append('\t');
		for (Object output : outputs)
			logString.append(output);
		logString.append('\n');
	}

	public synchronized static void log(Exception exception)
	{
		logString.append(TimerHandle.getTimeString()).append("\t\n");
		for (StackTraceElement trace : exception.getStackTrace())
			logString.append('\t').append(trace).append('\n');
	}
}