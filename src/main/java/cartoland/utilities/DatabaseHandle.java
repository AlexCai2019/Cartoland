package cartoland.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

public class DatabaseHandle
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
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
		     PreparedStatement statement = connection.prepareStatement(sql);
		     ResultSet result = statement.executeQuery())
		{
			Map<Long, CommandBlocksHandle.LotteryData> lotteryDataMap = new HashMap<>();
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
			return lotteryDataMap;
		}
		catch (SQLException e)
		{
			logger.error("讀取資料庫時發生問題！", e);
			return new HashMap<>();
		}
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

			statement.execute(); //執行
		}
		catch (SQLException e)
		{
			logger.error("寫入資料庫時發生問題！", e);
		}
	}
}