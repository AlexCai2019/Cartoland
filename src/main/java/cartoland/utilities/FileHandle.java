package cartoland.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * {@code FileHandle} is a utility class that provides every functions that this program need to deal with file input
 * such as config and json build. Can not be instantiated or inherited.
 *
 * @since 1.0
 * @author Alex Cai
 */
public final class FileHandle
{
	private static final Logger logger = LoggerFactory.getLogger(FileHandle.class);

	private static final Properties config = new Properties();

	private FileHandle()
	{
		throw new AssertionError(IDs.YOU_SHALL_NOT_ACCESS);
	}

	static
	{
		try (FileInputStream configStream = new FileInputStream("config.properties"))
		{
			config.load(configStream);
		}
		catch (IOException e)
		{
			logger.error("Read properties", e);
		}
	}

	//將JSON讀入進字串
	static String buildJsonStringFromFile(String fileName)
	{
		try
		{
			return Files.readString(Paths.get(fileName)); //從指定的檔名中讀取全部的資料 並將這些資料做成一個字串
		}
		catch (IOException e)
		{
			logger.error("Build JSON fail", e);
			return "{}";
		}
	}

	public static String readConfig(String key)
	{
		return config.getProperty(key, "");
	}
}