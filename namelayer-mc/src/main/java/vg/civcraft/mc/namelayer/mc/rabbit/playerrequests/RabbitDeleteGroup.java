package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.requests.DeleteGroup;

public class RabbitDeleteGroup extends RabbitGroupAction {


	public RabbitDeleteGroup(UUID executor, String groupName) {
		super(executor, groupName);
	}

	@Override
	public void handleReply(JSONObject reply, boolean success) {
		Group group = getGroup();
		if (success) {
			sendMessage(String.format("%s%s has been deleted", ChatColor.GREEN, group.getName()));
			return;
		}
		DeleteGroup.FailureReason reason = DeleteGroup.FailureReason.valueOf(reply.getString("reason"));
		switch (reason) {
			case GROUP_DOES_NOT_EXIST:
			groupDoesNotExistMessage();
			return;
		case NO_PERMISSION:
			String missingPerm = reply.getString("missing_perm");
			noPermissionMessage(missingPerm);
			return;	
		default:
			break;
		}
	}

	@Override
	protected void fillJson(JSONObject json) {
		//Alrehaandled by super class
		
	}

	@Override
	public String getIdentifier() {
		return DeleteGroup.REQUEST_ID;
	}

}
