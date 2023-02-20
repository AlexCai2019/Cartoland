package cartoland;

import cartoland.events.*;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import static cartoland.utilities.AddCommands.getCommands;
import static cartoland.utilities.IDAndEntities.jda;

/**
 * {@code Cartoland} is the class that has the {@link #main} method, which is the entry point of the entire program. All the
 * settings of JDA was done here.
 *
 * @since 1.0
 * @author Alex Cai
 */
public class Cartoland
{
	public static void main(String[] args) throws InterruptedException
	{
		if (args.length < 1)
			return;

		jda = JDABuilder.createDefault(args[0])
				.addEventListeners(
						new BotOnline(), //當機器人上線的時候
						new BotOffline(), //當機器人下線的時候
						new ChannelMessage(), //當有人在群組傳訊息
						new PrivateMessage(), //當有人傳私訊給機器人
						new CommandUsage(), //當有人使用指令
						new AutoComplete(), //當指令需要自動補完
						new ContextMenu(), //當有人使用右鍵功能
						new GetRole(), //當有人獲得會員身分組
						new JoinServer()) //當有人加入創聯
				.enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
				.setMemberCachePolicy(MemberCachePolicy.ALL)
				.setActivity(Activity.playing("Use /help to check more information")) //正在玩
				.build();

		jda.updateCommands().addCommands(getCommands()).queue();

		jda.awaitReady();
	}
}