package vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges;

import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.rabbit.RabbitMessage;

import vg.civcraft.mc.namelayer.core.requests.GroupModifications;

public class PrivateChatMessageMessage extends RabbitMessage {
	
	private UUID sender;
	private UUID receiver;
	private String message;

	public PrivateChatMessageMessage(String transactionID, UUID sender, UUID receiver, String message) {
		super(transactionID);
		this.sender = sender;
		this.receiver = receiver;
		this.message = message;
	}

	@Override
	protected void enrichJson(JSONObject json) {
		json.put("sender", sender.toString());
		json.put("receiver", receiver.toString());
		json.put("message", message);
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.SEND_PRIVATE_MESSAGE;
	}

}
