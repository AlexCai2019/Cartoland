package cartoland.commands;

import cartoland.utilities.Algorithm;
import cartoland.utilities.CommonFunctions;
import cartoland.utilities.IDs;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.time.OffsetDateTime;
import java.util.ArrayList;
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
		subcommands.put(NUMBER, new NumberSubcommand());
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

			Role memberRole = cartoland.getRoleById(IDs.MEMBER_ROLE_ID); //會員身分組
			List<Member> allMembers = new ArrayList<>();
			cartoland.loadMembers(member -> //一個一個成員尋找
			{
				User user = member.getUser();
				if (user.isBot() || user.isSystem()) //略過機器人或系統
					return;
				if (member.getRoles().contains(memberRole)) //如果有會員身分組
					allMembers.add(member); //加入
			});

			if (allMembers.isEmpty()) //找不到任何有會員身分組的人
			{
				event.reply("This guild doesn't have any members!").queue();
				return; //結束
			}
			User commandUser = event.getUser();
			Member member = Algorithm.randomElement(allMembers);
			User user = member.getUser();
			event.reply(member.getEffectiveName() + '(' + user.getEffectiveName() + ')')
					.setEmbeds(new EmbedBuilder()
							.setDescription(user.getAsMention())
							.setAuthor(commandUser.getEffectiveName(), null, commandUser.getEffectiveAvatarUrl())
							.setTimestamp(OffsetDateTime.now())
							.setFooter(user.getId())
							.build())
					.queue();
		}
	}

	private static class NumberSubcommand implements ICommand
	{
		@Override
		public void commandProcess(SlashCommandInteractionEvent event)
		{
			Integer minimum = event.getOption("minimum", CommonFunctions.getAsInt);
			Integer maximum = event.getOption("maximum", CommonFunctions.getAsInt);

			if (minimum == null || maximum == null) //都是null
			{
				event.reply("Impossible, this is required!").queue();
				return;
			}

			int min = minimum;
			int max = maximum;
			if (min > max)
			{
				event.reply("Minimum mustn't larger than maximum!").queue();
				return;
			}
			event.reply(Integer.toString(new Random().nextInt(min, Algorithm.safeAdd(max, 1)))).queue();
		}
	}
}