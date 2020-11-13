package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;

import org.json.JSONObject;

import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.namelayer.core.Group;

public class RabbitCreateGroup extends RabbitGroupAction {
	public enum FailureReason {
		GROUP_ALREADY_EXISTS, GROUP_LIMIT_REACHED;
	}

	public RabbitCreateGroup(UUID executor, String groupName) {
		super(executor, groupName);
	}

	@Override
	public void handleReply(JSONObject reply, boolean success) {
		Group group = getGroup();
		if (success) {
			sendMessage(String.format("%sThe group %s was successfully created", ChatColor.GREEN, group.getName()));
		}
		FailureReason reason = FailureReason.valueOf(reply.getString("reason"));
		switch (reason) {
		case GROUP_ALREADY_EXISTS:
			sendMessage(String.format("%sThe group %s%s already exists", ChatColor.RED, group.getColoredName(),
					ChatColor.RED));
			return;
		case GROUP_LIMIT_REACHED:
			sendMessage(String.format("%s%s%s was not created since you have reached your personal group limit", ChatColor.RED, group.getName(), ChatColor.RED));
			return;
		default:
			break;
			
		}
		
	}

	@Override
	protected void fillJson(JSONObject json) {
		// Already handled in super class
	}

}
