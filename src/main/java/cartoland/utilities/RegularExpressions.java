package cartoland.utilities;

import java.util.regex.Pattern;

public final class RegularExpressions
{
	private RegularExpressions()
	{
		throw new AssertionError(IDs.YOU_SHALL_NOT_ACCESS);
	}

	public static final Pattern CARTOLAND_MESSAGE_LINK_REGEX = Pattern.compile("https://discord\\.com/channels/" + IDs.CARTOLAND_SERVER_ID + "/\\d+/\\d+");
	public static final Pattern JIRA_LINK_REGEX; //https://bugs.mojang.com/browse/MC-87984
	public static final Pattern BUG_ID_REGEX; //MC-87984
	public static final Pattern BUG_NUMBER_REGEX; //87984
	public static final Pattern BET_NUMBER_REGEX = Pattern.compile("\\d{1,18}"); //防止輸入超過Long.MAX_VALUE
	public static final Pattern BET_PERCENT_REGEX = Pattern.compile("\\d{1,4}%"); //防止輸入超過Short.MAX_VALUE
	public static final Pattern UUID_DASH_REGEX = Pattern.compile("[0-9A-Fa-f]{1,8}-[0-9A-Fa-f]{1,4}-[0-9A-Fa-f]{1,4}-[0-9A-Fa-f]{1,4}-[0-9A-Fa-f]{1,12}"); //59c1027b-5559-4e6a-91e4-2b8b949656ce
	public static final Pattern UUID_NO_DASH_REGEX = Pattern.compile("[0-9A-Fa-f]{32}"); //59c1027b55594e6a91e42b8b949656ce
	public static final Pattern DECIMAL_UNSIGNED_INT_REGEX = Pattern.compile("\\d{1,10}"); //最高4294967295 最低0
	public static final Pattern HEXADECIMAL_UNSIGNED_INT_REGEX = Pattern.compile("[0-9A-Fa-f]{6,8}"); //從六個0到八個F
	public static final Pattern LEADING_SHARP_HEXADECIMAL_UNSIGNED_INT_REGEX = Pattern.compile("#[0-9A-Fa-f]{6,8}"); //#FFFFFF

	static
	{
		final String BUG_NUMBER_STRING = "\\d{1,6}"; //目前bug數還沒超過999999個 等超過了再來改
		final String BUG_ID_STRING = "(?i)(MC(PE|D|L|LG)?|REALMS|WEB|BDS)-" + BUG_NUMBER_STRING;
		JIRA_LINK_REGEX = Pattern.compile("https://bugs\\.mojang\\.com/browse/" + BUG_ID_STRING);
		BUG_ID_REGEX = Pattern.compile(BUG_ID_STRING);
		BUG_NUMBER_REGEX = Pattern.compile(BUG_NUMBER_STRING);
	}
}