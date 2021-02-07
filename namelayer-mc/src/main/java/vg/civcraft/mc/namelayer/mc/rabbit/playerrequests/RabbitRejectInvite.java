package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.requests.RejectInvite;

public class RabbitRejectInvite extends RabbitGroupAction {


	public RabbitRejectInvite(UUID executor, Group groupName) {
		super(executor, groupName.getName());
 	}

	public void handleReply(JSONObject reply, boolean success) {
		Group group = getGroup();
		if (success) {
			GroupRank rankInvitedTo = group.getGroupRankHandler().getRank(reply.getString("rank_invited_to"));
			sendMessage(String.format("%sYou rejected the invite to %s%s as %s%s", ChatColor.GREEN, group.getColoredName(),
				ChatColor.GREEN, ChatColor.GOLD, rankInvitedTo.getName()));
			return;
		}
		RejectInvite.FailureReason reason = RejectInvite.FailureReason.valueOf(reply.getString("reason"));
		switch (reason) {
			case NOT_INVITED:
				sendMessage(String.format("%sYou have not been invited to %s", ChatColor.RED, group.getColoredName()));
				return;
			case GROUP_DOES_NOT_EXIST:
				groupDoesNotExistMessage();	
				return;
			default:
				break;
		}
	}

	@Override
	protected void fillJson(JSONObject json) {
		// Already handled by super class
		
	}
	
	@Override
	public String getIdentifier() {
		return RejectInvite.REQUEST_ID;
	}

}
