package cartoland.modals;

import cartoland.methods.IAnalyzeCTLCLink;
import cartoland.utilities.JsonHandle;
import cartoland.utilities.MembersHandle;
import cartoland.utilities.RegularExpressions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

import java.util.List;

public class UpdateIntroduceModal implements IModal, IAnalyzeCTLCLink
{
	public static final String NEW_INTRODUCTION_TEXT = "new_introduction";

	@Override
	public void modalProcess(ModalInteractionEvent event)
	{
		ModalMapping newIntroduction = event.getValue(NEW_INTRODUCTION_TEXT);
		if (newIntroduction == null)
		{
			event.reply("Impossible, this is required!").setEphemeral(true).queue();
			return;
		}

		long userID = event.getUser().getIdLong();
		String content = newIntroduction.getAsString(); //新的自我介紹

		if (RegularExpressions.CARTOLAND_MESSAGE_LINK_REGEX.matcher(content).matches()) //如果內容是創聯群組連結
			analyze(event, content);
		else //如果不是
		{
			MembersHandle.updateIntroduction(userID, content); //更新自我介紹
			event.reply(JsonHandle.getString(userID, "introduce.update.success")).queue();
		}
	}

	@Override
	public void whenSuccess(IReplyCallback event, Message message)
	{
		long userID = event.getUser().getIdLong();
		event.reply(JsonHandle.getString(userID, "introduce.update.success")).queue(); //趕緊回覆避免超過三秒

		List<Message.Attachment> attachments = message.getAttachments(); //附件
		if (attachments.isEmpty()) //如果沒有附件 這是比較常見的情形
		{
			MembersHandle.updateIntroduction(userID, message.getContentRaw()); //直接等於訊息內容
			return;
		}

		//有附件 改用StringBuilder處理
		StringBuilder introductionBuilder = new StringBuilder(message.getContentRaw()); //訊息內容
		for (Message.Attachment attachment : attachments)
			introductionBuilder.append('\n').append(attachment.getUrl()); //一一獲取附件的連結
		MembersHandle.updateIntroduction(userID, introductionBuilder.toString()); //更新介紹
	}

	@Override
	public void whenFail(IReplyCallback event, String link, int failCode)
	{
		long userID = event.getUser().getIdLong();
		String jsonKey = switch (failCode)
		{
			case NO_GUILD -> "introduce.update.no_guild";
			case NO_CHANNEL -> "introduce.update.no_channel";
			default -> "introduce.update.no_message";
		};
		event.reply(JsonHandle.getString(userID, jsonKey)).queue();
		MembersHandle.updateIntroduction(userID, link); //更新介紹 直接把連結放進內容中
	}
}