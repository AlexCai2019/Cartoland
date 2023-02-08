package cartoland.utilities;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.Random;

/**
 * @since 1.3
 * @author Alex Cai
 */
public class IDAndEntities
{
	private IDAndEntities() {}

	public static final long BOT_CHANNEL_ID = 891703579289718814L; //創聯的機器人頻道
	public static final long UNDERGROUND_CHANNEL_ID = 962688156942073887L; //創聯的地下頻道
	public static final long AC_ID = 355953951469731842L;

	public static TextChannel botChannel;
	public static TextChannel undergroundChannel;
	public static JDA jda;
	public static User botItself;

	public static Random random = new Random();
}