package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.requests.CreateGroup;

public class RabbitCreateGroup extends RabbitGroupAction {

	public RabbitCreateGroup(UUID executor, String groupName) {
		super(executor, groupName);
	}

	@Override
	public void handleReply(JSONObject reply, boolean success) {
		Group group = getGroup();
		if (success) {
			sendMessage(String.format("%sThe group %s was successfully created", ChatColor.GREEN, group.getName()));
			return;
		}
		CreateGroup.FailureReason reason = CreateGroup.FailureReason.valueOf(reply.getString("reason"));
		switch (reason) {
		case GROUP_ALREADY_EXISTS:
			sendMessage(String.format("%sThe group %s%s already exists", ChatColor.RED, groupName,
					ChatColor.RED));
			return;
		case GROUP_LIMIT_REACHED:
			sendMessage(String.format("%s%s%s was not created since you have reached your personal group limit", ChatColor.RED, groupName, ChatColor.RED));
			return;
		case NAME_INVALID:
			sendMessage(String.format("%s%s%s was not created since the name format was invalid", ChatColor.RED, groupName, ChatColor.RED));
			return;
		case UNKNOWN_ERROR:
			sendMessage(String.format("%sAn unknown error occured during the creation of %s", ChatColor.RED, groupName));
		default:
			break;
			
		}
		
	}

	@Override
	protected void fillJson(JSONObject json) {
		// Already handled in super class
	}

	@Override
	public String getIdentifier() {
		return CreateGroup.REQUEST_ID;
	}

}
