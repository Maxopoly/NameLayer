package vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges;

import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.rabbit.RabbitMessage;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.requests.GroupModifications;

public class GroupChatMessageMessage extends RabbitMessage {

	private int groupID;
	private UUID sender;
	private String message;
	
	public GroupChatMessageMessage(String transactionID, Group group, UUID sender, String msg) {
		super(transactionID);
		this.groupID = group.getPrimaryId();
		this.sender = sender;
		this.message = msg;
	}

	@Override
	protected void enrichJson(JSONObject json) {
		json.put("group_id", groupID);
		json.put("player", sender.toString());
		json.put("message", message);
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.SEND_GROUP_MESSAGE;
	}

}
