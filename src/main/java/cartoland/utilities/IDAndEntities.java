package cartoland.utilities;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

/**
 * {@code IDAndEntities} is a utility class that stores IDs, JDA entities and language constants. For example, this class
 * has some channel IDs, role IDs, and entities such as {@link JDA} and {@link TextChannel}. Can not be instantiated.
 *
 * @since 1.3
 * @author Alex Cai
 */
public class IDAndEntities
{
	private IDAndEntities()
	{
		throw new AssertionError(YOU_SHALL_NOT_ACCESS);
	}

	public static final long CARTOLAND_SERVER_ID = 886936474723950603L; //創聯
	public static final long QUESTIONS_CHANNEL_ID = 1079073022624940044L; //問題諮詢
	public static final long LOBBY_CHANNEL_ID = 886936474723950611L; //創聯的大廳頻道
	public static final long BOT_CHANNEL_ID = 891703579289718814L; //創聯的機器人頻道
	public static final long UNDERGROUND_CHANNEL_ID = 962688156942073887L; //創聯的地下頻道
	public static final long GENERAL_CATEGORY_ID = 886936474723950608L; //創聯的一般類別
	public static final long TECH_TALK_CATEGORY_ID = 974224793727537182L; //創聯的技術討論區類別
	public static final long MEMBER_ROLE_ID = 892415577002504272L; //會員身分組
	public static final long NSFW_ROLE_ID = 919700598612426814L; //地下身分組
	public static final long AC_ID = 355953951469731842L;

	public static JDA jda;
	public static Guild cartolandServer;
	public static TextChannel lobbyChannel;
	public static TextChannel botChannel;
	public static TextChannel undergroundChannel;
	public static Role memberRole;
	public static Role nsfwRole;
	public static User botItself;

	public static final String YOU_SHALL_NOT_ACCESS = "You shall not access!";

	public static ScheduledExecutorService threeAMService;
	public static ScheduledFuture<?> threeAMHandle;

	/**
	 * Language constants. Can not be instantiated.
	 *
	 * @since 1.0
	 * @author Alex Cai
	 */
	public static class Languages
	{
		private Languages()
		{
			throw new AssertionError(YOU_SHALL_NOT_ACCESS);
		}

		public static final String ENGLISH = "en";
		public static final String TW_MANDARIN = "tw";
		public static final String TAIWANESE = "ta";
		public static final String CANTONESE = "hk";
		public static final String CHINESE = "cn";
	}

	/**
	 * Difficulty constants. Can not be instantiated.
	 *
	 * @since 1.5
	 * @author Alex Cai
	 */
	public static class Difficulty
	{
		private Difficulty()
		{
			throw new AssertionError(YOU_SHALL_NOT_ACCESS);
		}

		public static final String EASY = "easy";
		public static final String NORMAL = "normal";
		public static final String HARD = "hard";
	}
}