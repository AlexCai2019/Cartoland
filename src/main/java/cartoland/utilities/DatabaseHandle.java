package cartoland.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

class DatabaseHandle
{
	private DatabaseHandle()
	{
		throw new AssertionError();
	}

	private static final Logger logger = LoggerFactory.getLogger(DatabaseHandle.class);

	private static final String URL = FileHandle.readConfig("db.url");
	private static final String USER = FileHandle.readConfig("db.user");
	private static final String PASSWORD = FileHandle.readConfig("db.password");

	static Map<Long, CommandBlocksHandle.LotteryData> readLotteryDataMap()
	{
		String sql = """
					SELECT user_id, name, blocks, bet_won, bet_lost, bet_show_hand_won, bet_show_hand_lost,
					slot_won, slot_lost, slot_show_hand_won, slot_show_hand_lost, last_claim_second, streak
					FROM lottery_data;
					""";
		Map<Long, CommandBlocksHandle.LotteryData> lotteryDataMap = new HashMap<>();
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
		     PreparedStatement statement = connection.prepareStatement(sql);
		     ResultSet result = statement.executeQuery())
		{
			while (result.next())
			{
				long userID = result.getLong(1);
				CommandBlocksHandle.LotteryData newData = new CommandBlocksHandle.LotteryData(userID);
				newData.name = result.getString(2);
				newData.blocks = result.getLong(3);
				newData.betWon = result.getInt(4);
				newData.betLost = result.getInt(5);
				newData.betShowHandWon = result.getInt(6);
				newData.betShowHandLost = result.getInt(7);
				newData.slotWon = result.getInt(8);
				newData.slotLost = result.getInt(9);
				newData.slotShowHandWon = result.getInt(10);
				newData.slotShowHandLost = result.getInt(11);
				newData.lastClaimSecond = result.getLong(12);
				newData.streak = result.getInt(13);
				lotteryDataMap.put(userID, newData);
			}
		}
		catch (SQLException e)
		{
			logger.error("讀取資料庫時發生問題！", e);
		}
		return lotteryDataMap;
	}

	static void writeLotteryData(CommandBlocksHandle.LotteryData data)
	{
		String sql = """
				INSERT INTO lottery_data (user_id, name, blocks, bet_won, bet_lost, bet_show_hand_won, bet_show_hand_lost,
					slot_won, slot_lost, slot_show_hand_won, slot_show_hand_lost, last_claim_second, streak)
				VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)
				ON DUPLICATE KEY UPDATE blocks = ?, name = ?, bet_won = ?, bet_lost = ?, bet_show_hand_won = ?, bet_show_hand_lost = ?,
					slot_won = ?, slot_lost = ?, slot_show_hand_won = ?, slot_show_hand_lost = ?, last_claim_second = ?, streak = ?;
				"""; //將資料寫入資料庫 如果已存在就更新
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
		     PreparedStatement statement = connection.prepareStatement(sql))
		{
			//每一個set 都對應了sql裡的問號
			statement.setLong(1, data.userID);
			statement.setString(2, data.name);
			statement.setLong(3, data.blocks);
			statement.setInt(4, data.betWon);
			statement.setInt(5, data.betLost);
			statement.setInt(6, data.betShowHandWon);
			statement.setInt(7, data.betShowHandLost);
			statement.setInt(8, data.slotWon);
			statement.setInt(9, data.slotLost);
			statement.setInt(10, data.slotShowHandWon);
			statement.setInt(11, data.slotShowHandLost);
			statement.setLong(12, data.lastClaimSecond);
			statement.setInt(13, data.streak);

			statement.setLong(14, data.blocks);
			statement.setString(15, data.name);
			statement.setInt(16, data.betWon);
			statement.setInt(17, data.betLost);
			statement.setInt(18, data.betShowHandWon);
			statement.setInt(19, data.betShowHandLost);
			statement.setInt(20, data.slotWon);
			statement.setInt(21, data.slotLost);
			statement.setInt(22, data.slotShowHandWon);
			statement.setInt(23, data.slotShowHandLost);
			statement.setLong(24, data.lastClaimSecond);
			statement.setInt(25, data.streak);

			statement.executeUpdate(); //執行
		}
		catch (SQLException e)
		{
			logger.error("寫入資料庫時發生問題！", e);
		}
	}

	static long readUndergroundID(long privateID)
	{
		String sql = "SELECT underground_id FROM private_to_underground WHERE private_id = ?;";

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
		     PreparedStatement statement = connection.prepareStatement(sql))
		{
			statement.setLong(1, privateID);
			try (ResultSet result = statement.executeQuery())
			{
				return result.next() ? result.getLong(1) : AnonymousHandle.INVALID_CONNECTION;
			}
		}
		catch (SQLException e)
		{
			logger.error("讀取underground_id時發生問題！", e);
			return AnonymousHandle.INVALID_CONNECTION;
		}
	}

	static void writeUndergroundConnection(long privateID, long undergroundID)
	{
		String sql = "INSERT INTO private_to_underground (private_id, underground_id) VALUES (?,?);";

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
		     PreparedStatement statement = connection.prepareStatement(sql))
		{
			statement.setLong(1, privateID);
			statement.setLong(2, undergroundID);

			statement.executeUpdate(); //執行
		}
		catch (SQLException e)
		{
			logger.error("寫入private_to_underground時發生問題！", e);
		}
	}

	static LocalDate readBirthday(long userID)
	{
		String sql = "SELECT birthday FROM users WHERE user_id = ?;";

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
		     PreparedStatement statement = connection.prepareStatement(sql))
		{
			statement.setLong(1, userID);
			try (ResultSet result = statement.executeQuery())
			{
				if (result.next())
				{
					Date birthday = result.getDate(1);
					if (birthday != null)
						return birthday.toLocalDate();
				}
			}
		}
		catch (SQLException e)
		{
			logger.error("讀取birthday時發生問題！", e);
		}
		return null;
	}

	static void writeBirthday(long userID, LocalDate date)
	{
		String sql = "UPDATE users SET birthday=? WHERE user_id = ?;";

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
		     PreparedStatement statement = connection.prepareStatement(sql))
		{
			statement.setDate(1, date == null ? null : Date.valueOf(date));
			statement.setLong(2, userID);
			statement.executeUpdate(); //執行
		}
		catch (SQLException e)
		{
			logger.error("寫入birthday時發生問題！", e);
		}
	}

	static List<Long> readTodayBirthday(LocalDate date)
	{
		String sql = "SELECT user_id FROM users WHERE birthday = ?;";
		List<Long> todayBirthday = new ArrayList<>(); //所有今天生日的人

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
		     PreparedStatement statement = connection.prepareStatement(sql))
		{
			statement.setDate(1, Date.valueOf(date)); //日期
			try (ResultSet result = statement.executeQuery())
			{
				while (result.next()) //找出所有今天生日的人
					todayBirthday.add(result.getLong(1));
			}
		}
		catch (SQLException e)
		{
			logger.error("讀取birthday時發生問題！", e);
		}

		return todayBirthday;
	}

	static void writeLanguage(long userID, String language)
	{
		String sql = "INSERT INTO users (user_id, language) VALUES (?,?) ON DUPLICATE KEY UPDATE language = ?;";

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
		     PreparedStatement statement = connection.prepareStatement(sql))
		{
			statement.setLong(1, userID);
			statement.setString(2, language);
			statement.setString(3, language);

			statement.executeUpdate(); //執行
		}
		catch (SQLException e)
		{
			logger.error("寫入language時發生問題！", e);
		}
	}

	static List<Long> readAllUsers()
	{
		String sql = "SELECT user_id FROM users;";
		List<Long> allMembers = new ArrayList<>(); //所有人

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
		     PreparedStatement statement = connection.prepareStatement(sql);
		     ResultSet result = statement.executeQuery())
		{
			while (result.next()) //找出所有人
				allMembers.add(result.getLong(1));
		}
		catch (SQLException e)
		{
			logger.error("讀取users時發生問題！", e);
		}

		return allMembers;
	}

	static void onMemberJoin(long userID)
	{
		writeLanguage(userID, Languages.TW_MANDARIN); //預設中文
	}

	static void onMemberLeave(long userID)
	{
		String sql = "DELETE FROM users WHERE user_id=?;";

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
		     PreparedStatement statement = connection.prepareStatement(sql))
		{
			statement.setLong(1, userID);

			statement.executeUpdate(); //執行
		}
		catch (SQLException e)
		{
			logger.error("寫入user時發生問題！", e);
		}
	}
}