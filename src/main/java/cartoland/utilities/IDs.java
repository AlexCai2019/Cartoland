package cartoland.utilities;

/**
 * {@code IDs} is a utility class that stores IDs. For example, this class has some channel IDs, role IDs. Can not be
 * instantiated or inherited.
 *
 * @since 1.3
 * @author Alex Cai
 */
public final class IDs
{
	public static final String YOU_SHALL_NOT_ACCESS = "You shall not access!";

	private IDs()
	{
		throw new AssertionError(YOU_SHALL_NOT_ACCESS);
	}

	public static final long CARTOLAND_SERVER_ID = 886936474723950603L; //創聯
	public static final long GENERAL_CATEGORY_ID = 886936474723950608L; //創聯的一般類別
	public static final long FORUM_CATEGORY_ID = 922892242459459657L; //創聯的論壇類別
	public static final long TECH_TALK_CATEGORY_ID = 974224793727537182L; //創聯的技術討論區類別
	public static final long SHOWCASE_CATEGORY_ID = 892127357878554634L; //創聯的創作展示類別
	public static final long VOICE_CATEGORY_ID = 886936475143405618L; //創聯的語音頻道類別
	public static final long DANGEROUS_CATEGORY_ID = 919696732613378078L; //創聯的地下類別
	public static final long READ_ME_CHANNEL_ID = 973898745777377330L; //創聯的解鎖須知頻道
	public static final long SELF_INTRO_CHANNEL_ID = 892415434240950282L; //創聯的會員申請頻道
	public static final long MAP_DISCUSS_CHANNEL_ID = 1072796680996532275L; //創聯的地圖專板
	public static final long QUESTIONS_CHANNEL_ID = 1079073022624940044L; //創聯的問題諮詢頻道
	public static final long LOBBY_CHANNEL_ID = 886936474723950611L; //創聯的大廳頻道
	public static final long BOT_CHANNEL_ID = 891703579289718814L; //創聯的機器人頻道
	public static final long TRAP_CHANNEL_ID = 1225886187017732256L; //創聯的陷阱頻道
	public static final long STRANGERS_ROOM_CHANNEL_ID = 1226038026170400769L; //創聯的會客室頻道
	public static final long UNDERGROUND_CHANNEL_ID = 962688156942073887L; //創聯的地下頻道
	public static final long RESOLVED_FORUM_TAG_ID = 1079074167468605490L; //已解決的tag
	public static final long UNRESOLVED_FORUM_TAG_ID = 1079074098493280347L; //未解決的tag
	public static final long GOD_OF_GAMBLERS_ROLE_ID = 1119944573117014096L; //賭神身分組
	public static final long MEMBER_ROLE_ID = 892415577002504272L; //會員身分組
	public static final long NSFW_ROLE_ID = 919700598612426814L; //地下身分組
	public static final long LEARNED_EMOJI_ID = 892406442622083143L; //:learned:表情符號
	public static final long CHAO_WEN_DE_LA_EMOJI_ID = 967305472950542336L; //:chaowendela:表情符號
	public static final long CARTOLAND_LOGO_EMOJI_ID = 949332057258070036L; //:cartoland_logo:表情符號
	public static final long HAHA_EMOJI_ID = 900717110488084530L; //:haha:表情符號
	public static final long PIKA_EMOJI_ID = 891713649926869003L; //:pika:表情符號
	public static final long COOL_PIKA_EMOJI_ID = 891714126424985610L; //:cool_pika:表情符號
	public static final long YA_EMOJI_ID = 920200649830989825L; //:ya:表情符號
	public static final long WOW_EMOJI_ID = 893499112228519996L; //:wow:表情符號
	public static final long WORSHIP_A_EMOJI_ID = 935135593527128104L; //:worship_a:表情符號
	public static final long PING_EMOJI_ID = 1065915559918719006L; //:ping:表情符號
	public static final long RESOLVED_EMOJI_ID = 1081082902785314921L; //:resolved:表情符號
	public static final long AC_ID = 355953951469731842L;
	public static final long MEGA_ID = 412943154317361152L;
}

/**
 * Language constants. Can not be instantiated or inherited.
 *
 * @since 1.0
 * @author Alex Cai
 */
final class Languages
{
	private Languages()
	{
		throw new AssertionError(IDs.YOU_SHALL_NOT_ACCESS);
	}

	public static final String ENGLISH = "en";
	public static final String TW_MANDARIN = "tw";
	public static final String TAIWANESE = "ta";
	public static final String CANTONESE = "hk";
	public static final String CHINESE = "cn";
	public static final String ESPANOL = "es";
	public static final String JAPANESE = "jp";
}