package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.artemis.rabbit.MCStandardRequest;
import com.google.common.base.Preconditions;

import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.namelayer.core.requests.SendPrivateMessage;
import vg.civcraft.mc.namelayer.mc.model.chat.PrivateChatMode;
import vg.civcraft.mc.namelayer.mc.util.MsgUtils;

public class RabbitSendPrivateChatMessage extends MCStandardRequest {
	
	private UUID sender;
	private UUID receiver;
	private String message;

	public RabbitSendPrivateChatMessage(UUID executor, UUID receiver, String message) {
		Preconditions.checkNotNull(executor);
		Preconditions.checkNotNull(receiver);
		Preconditions.checkNotNull(message);
		this.sender = executor;
		this.receiver = receiver;
		this.message = message;
	}

	@Override
	public void handleReply(JSONObject reply) {
		boolean success = reply.getBoolean("success");
		if (success) {
			PrivateChatMode.showPMSentToSender(sender, receiver, message);
			return;
		}
		SendPrivateMessage.FailureReason reason = SendPrivateMessage.FailureReason.valueOf(reply.getString("reason"));
		switch (reason) {
		case PLAYER_DOES_NOT_EXIST:
			MsgUtils.sendMsg(sender, ChatColor.RED + "The player you were last talking to is currently offline");
			break;
		default:
			break;
		}
	}

	@Override
	public String getIdentifier() {
		return SendPrivateMessage.REQUEST_ID;
	}

	@Override
	protected void enrichJson(JSONObject json) {
		json.put("sender", sender.toString());
		json.put("receiver", receiver.toString());
		json.put("message", message);
	}
	
}
