package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.requests.LeaveGroup;

public class RabbitLeaveGroup extends RabbitGroupAction {

	public RabbitLeaveGroup(UUID executor, Group group) {
		super(executor, group.getName());
	}

	@Override
	public void handleReply(JSONObject reply, boolean success) {
		Group group = getGroup();
		if (success) {
			sendMessage(String.format("%sYou have left %s", ChatColor.GREEN, group.getColoredName()));
			return;
		}
		LeaveGroup.FailureReason reason = LeaveGroup.FailureReason.valueOf(reply.getString("reason"));
		switch (reason) {
		case GROUP_DOES_NOT_EXIST:
			groupDoesNotExistMessage();
			return;
		case NOT_A_MEMBER:
			sendMessage(String.format("%sYou are not a member of %s", ChatColor.RED, group.getColoredName()));
			return;
		case NO_OTHER_OWNER:
			GroupRank ownerRank = group.getGroupRankHandler().getOwnerRank();
			sendMessage(String.format("%sYou are the last remaining %s%s%s of %s%s. You can not leave until you have added another %s%s",
					ChatColor.RED, ChatColor.YELLOW, ownerRank.getName(), ChatColor.RED, group.getColoredName(),
					ChatColor.RED, ChatColor.YELLOW, ownerRank.getName()));
			return;
		default:
			break;
			
		}
	}

	@Override
	protected void fillJson(JSONObject json) {
		//Already handled by super class
	}
	
	@Override
	public String getIdentifier() {
		return LeaveGroup.REQUEST_ID;
	}

}
