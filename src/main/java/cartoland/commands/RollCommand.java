package cartoland.commands;

import cartoland.events.NewMember;
import cartoland.utilities.Algorithm;
import cartoland.utilities.CommonFunctions;
import cartoland.utilities.IDs;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RollCommand extends HasSubcommands
{
	public static final String MEMBER = "member";

	public static final String NUMBER = "number";

	public RollCommand()
	{
		super(2);
		subcommands.put(MEMBER, new MemberSubcommand());
		subcommands.put(NUMBER, event ->
		{
			int minimum = event.getOption("minimum", 0, CommonFunctions.getAsInt);
			int maximum = event.getOption("maximum", 0, CommonFunctions.getAsInt);

			if (minimum > maximum)
				event.reply("Minimum mustn't larger than maximum!").queue();
			else
				event.reply(Integer.toString(new Random().nextInt(minimum, Algorithm.safeAdd(maximum, 1)))).queue();
		});
	}

	private static class MemberSubcommand implements ICommand
	{
		@Override
		public void commandProcess(SlashCommandInteractionEvent event)
		{
			Guild cartoland = event.getGuild(); //伺服器
			if (cartoland == null) //找不到伺服器 說明是私訊
			{
				event.reply("This command is guild only!").queue();
				return; //結束
			}

			event.deferReply().queue(); //使用deferReply 避免超過3秒限制

			Role targetRole = event.getOption("role", cartoland.getRoleById(IDs.MEMBER_ROLE_ID), OptionMapping::getAsRole); //目標身分組

			List<Long> allMembers = NewMember.getAllMembersList(); //所有成員們的ID
			Collections.shuffle(allMembers); //洗牌
			for (Long userID : allMembers) //一個一個看
			{
				Member member = cartoland.retrieveMemberById(userID).complete(); //找到該名成員
				User user = member.getUser();
				if (user.isBot() || user.isSystem() || !member.getRoles().contains(targetRole)) //如果是機器人或系統或沒有目標身分組
					continue; //下一位成員
				String userIDString = Long.toUnsignedString(userID); //成員ID 字串型態
				event.getHook()
						.sendMessage(member.getEffectiveName() + '(' + user.getName() + ')')
						.setEmbeds(new EmbedBuilder()
								.setDescription("<@" + userIDString + '>') //注意此處的userIDString是字串 與慣例的long不同
								.setAuthor(member.getEffectiveName(), null, member.getEffectiveAvatarUrl())
								.setTimestamp(OffsetDateTime.now())
								.setFooter(userIDString)
								.build())
						.queue();
				return;
			}
			event.getHook().sendMessage("This guild doesn't have any members!").queue();
		}
	}
}