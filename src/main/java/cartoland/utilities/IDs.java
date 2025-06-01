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

	public static final long OFF_TOPIC_CATEGORY_ID = 974224793727537182L; //創聯的雜談類別

	public static final long READ_ME_CHANNEL_ID = 973898745777377330L; //創聯的解鎖須知頻道
	//auto thread
	public static final long VIDEOS_CHANNEL_ID = 1248704681543335987L; //創聯的影片與直播展示頻道
	public static final long MAP_REVIEW_CHANNEL_ID = 1279852641077166243L; //創聯的地圖評鑑頻道
	public static final long PLAY_TEST_CHANNEL_ID = 994286072932610118L; //創聯的測試招募頻道
	//self intro
	public static final long SELF_INTRO_CHANNEL_ID = 892415434240950282L; //創聯的會員申請頻道
	//forum
	public static final long MAP_DISCUSS_CHANNEL_ID = 1072796680996532275L; //創聯的地圖專板
	public static final long CREATION_CHANNEL_ID = 1309238729101152397L; //創聯的作品展示頻道
	public static final long RESOURCE_CHANNEL_ID = 1256209988825649245L; //創聯的素材頻道
	public static final long QUESTIONS_CHANNEL_ID = 1079073022624940044L; //創聯的問題諮詢頻道
	//bot can talk
	public static final long ZH_CHAT_CHANNEL_ID = 886936474723950611L; //創聯的中文頻道
	public static final long EN_CHAT_CHANNEL_ID = 891662166082601020L; //創聯的英文頻道
	public static final long VOICE_TEXT_CHANNEL_ID = 891704883194003486L; //創聯的語音文字頻道
	public static final long BOT_CHANNEL_ID = 891703579289718814L; //創聯的機器人頻道
	public static final long UNDERGROUND_CHANNEL_ID = 962688156942073887L; //創聯的地下頻道
	//trap
	public static final long THE_DIOGENES_CLUB_CHANNEL_ID = 1225886187017732256L; //創聯的第歐根尼俱樂部頻道
	public static final long STRANGERS_ROOM_CHANNEL_ID = 1226038026170400769L; //創聯的會客室頻道

	public static final long RESOLVED_FORUM_TAG_ID = 1079074167468605490L; //已解決的tag
	public static final long UNRESOLVED_FORUM_TAG_ID = 1079074098493280347L; //未解決的tag

	public static final long ADMIN_ROLE_ID = 891664569150341170L; //聯邦政府成員身分組
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
	public static final long PING_EMOJI_ID = 1065915559918719006L; //:ping:表情符號
	public static final long RESOLVED_EMOJI_ID = 1081082902785314921L; //:resolved:表情符號

	public static final long AC_ID = 355953951469731842L;
	public static final long MEGA_ID = 412943154317361152L;
}