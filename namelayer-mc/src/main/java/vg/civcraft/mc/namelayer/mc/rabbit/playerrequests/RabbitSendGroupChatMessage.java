package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.requests.SendGroupChatMessage;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;

public class RabbitSendGroupChatMessage extends RabbitGroupAction {
	
	private String message;

	public RabbitSendGroupChatMessage(UUID executor, String groupName, String message) {
		super(executor, groupName);
		this.message = message;
	}

	@Override
	public void handleReply(JSONObject reply, boolean success) {
		if (success) {
			return;
		}
		SendGroupChatMessage.FailureReason reason = SendGroupChatMessage.FailureReason.valueOf(reply.getString("reason"));
		switch (reason) {
		case GROUP_DOES_NOT_EXIST:
			groupDoesNotExistMessage();
			break;
		case NO_PERMISSION:
			String missingPerm = reply.getString("missing_perm");
			noPermissionMessage(missingPerm);
			break;
		default:
			break;
		}
		doSync(() -> NameLayerPlugin.getInstance().getChatTracker().resetChatMode(Bukkit.getPlayer(executor)));
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("message", message);
		
	}

	@Override
	public String getIdentifier() {
		return SendGroupChatMessage.REQUEST_ID;
	}

}
