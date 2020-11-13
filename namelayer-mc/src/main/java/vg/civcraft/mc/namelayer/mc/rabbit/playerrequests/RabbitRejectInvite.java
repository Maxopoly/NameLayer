package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;

public class RabbitRejectInvite extends RabbitGroupAction {
	public enum FailureReason {
		NOT_INVITED;
	}

	public RabbitRejectInvite(UUID executor, Group groupName) {
		super(executor, groupName.getName());
 	}

	public void handleReply(JSONObject reply, boolean success) {
		Group group = getGroup();
		if (success) {
			GroupRank rankInvitedTo = group.getInvite(executor);
			sendMessage(String.format("%sYou rejected the invite to %s%s as %s%s", ChatColor.GREEN, group.getColoredName(),
				ChatColor.GREEN, ChatColor.GOLD, rankInvitedTo.getName()));
		}
		FailureReason reason = FailureReason.valueOf(reply.getString("reason"));
		switch (reason) {
			case NOT_INVITED:
				sendMessage(String.format("%sYou have not been invited to %s", ChatColor.RED, group.getColoredName()));
				return;
			default:
				break;
		}
	}

	@Override
	protected void fillJson(JSONObject json) {
		// Already handled by super class
		
	}

}
