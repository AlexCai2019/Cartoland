package cartoland.modals;

import cartoland.methods.IAnalyzeCTLCLink;
import cartoland.utilities.JsonHandle;
import cartoland.utilities.MembersHandle;
import cartoland.utilities.ReturnResult;
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
		if (newIntroduction != null)
			analyze(event, newIntroduction.getAsString()); //新的自我介紹
	}

	@Override
	public void afterAnalyze(IReplyCallback event, String link, ReturnResult<Message> result)
	{
		long userID = event.getUser().getIdLong();
		if (!result.isSuccess()) //如果不成功
		{
			String errorMessage = result.getError();
			String jsonKey;
			boolean ephemeral;
			if (INVALID_LINK.equals(errorMessage)) //如果內容不是創聯群組連結 依然算成功 代表是普通的文字
			{
				jsonKey = "introduce.update.success";
				ephemeral = false; //要顯示
			}
			else
			{
				jsonKey = "introduce.update." + errorMessage;
				ephemeral = true;
			}
			event.reply(JsonHandle.getString(userID, jsonKey)).setEphemeral(ephemeral).queue();
			MembersHandle.updateIntroduction(userID, link); //更新自我介紹 直接把連結放進內容中
			return;
		}

		//以下是成功的時候
		event.reply(JsonHandle.getString(userID, "introduce.update.success")).queue(); //趕緊回覆避免超過三秒
		Message message = result.getValue(); //解析出的訊息

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
}