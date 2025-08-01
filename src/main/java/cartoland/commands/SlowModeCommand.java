package cartoland.commands;

import cartoland.methods.IStringHelper;
import cartoland.utilities.*;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.attribute.ISlowmodeChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class SlowModeCommand implements ICommand, IStringHelper
{
	@Override
	public void commandProcess(SlashCommandInteractionEvent event)
	{
		Member member = event.getMember(); //使用指令的成員
		if (member == null)
		{
			event.reply("Impossible, this is required!").queue();
			return;
		}

		long userID = member.getIdLong(); //使用指令的成員ID

		if (!member.hasPermission(Permission.MANAGE_CHANNEL))
		{
			event.reply(JsonHandle.getString(userID, "admin.slow_mode.no_permission")).setEphemeral(true).queue();
			return;
		}

		if (!(event.getOption("channel", OptionMapping::getAsChannel) instanceof ISlowmodeChannel channel)) //不是可以設慢速模式的頻道
		{
			event.reply(JsonHandle.getString(userID, "admin.slow_mode.wrong_channel")).setEphemeral(true).queue();
			return;
		}

		//可惜沒有getAsFloat
		float time = event.getOption("time", 0.0, OptionMapping::getAsDouble).floatValue(); //解包並轉float
		if (time < 0) //不能負時間 可以0 0代表取消慢速
		{
			event.reply(JsonHandle.getString(userID, "admin.slow_mode.time_must_be_no_negative")).setEphemeral(true).queue();
			return;
		}

		String unit = event.getOption("unit", "", OptionMapping::getAsString); //單位字串

		int timeSecond = Math.round(time * switch (unit) //將單位轉成秒
		{
			case "second" -> 1;
			case "minute" -> 60;
			case "quarter" -> 60 * 15;
			case "hour" -> 60 * 60;
			case "double_hour" -> 60 * 60 * 2;
			default -> 0;
		});
		if (timeSecond > ISlowmodeChannel.MAX_SLOWMODE) //不能超過6小時 21600秒
		{
			event.reply(JsonHandle.getString(userID, "admin.slow_mode.too_long", ISlowmodeChannel.MAX_SLOWMODE / (60 * 60)))
					.setEphemeral(true).queue();
			return;
		}

		String slowTime = cleanFPString(Float.toString(time)) + ' ' + JsonHandle.getString(userID, "admin.unit_" + unit);
		if (timeSecond > 0)
			event.reply(JsonHandle.getString(userID, "admin.slow_mode.success", channel.getAsMention(), slowTime)).queue();
		else //一定是等於0 前面過濾掉小於0的情況了
			event.reply(JsonHandle.getString(userID, "admin.slow_mode.cancel", channel.getAsMention())).queue();
		channel.getManager().setSlowmode(timeSecond).queue(); //設定慢速時間
	}
}