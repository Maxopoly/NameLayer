package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.GroupTracker;
import vg.civcraft.mc.namelayer.core.requests.AcceptInvite;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;

public class RabbitAcceptInvite extends RabbitGroupAction {

	public RabbitAcceptInvite(UUID executor, Group group) {
		super(executor, group.getName());
	}

	@Override
	public void handleReply(JSONObject reply, boolean success) {
		Group group = getGroup();
		if (success) {
			int rankId = reply.getInt("rank_id");
			GroupRank rank = group.getGroupRankHandler().getRank(rankId);
			sendMessage(String.format("%sYou have been added to %s%s as a %s%s", ChatColor.GREEN,
					group.getColoredName(), ChatColor.GREEN, ChatColor.YELLOW, rank.getName()));
			return;
		}
		AcceptInvite.FailureReason reason = AcceptInvite.FailureReason.valueOf(reply.getString("reason"));
		switch (reason) {
		case ALREADY_MEMBER:
			sendMessage(ChatColor.RED + "You are already a member or blacklisted. You cannot join again.");
			return;
		case GROUP_DOES_NOT_EXIST:
			sendMessage(String.format("%sThe group %s does not exist", ChatColor.RED, groupName));
			return;
		case NO_EXISTING_INVITE:
			sendMessage(String.format("%sYou were not invited to %s", ChatColor.RED, group.getColoredName()));
			return;
		default:
			break;

		}
	}

	@Override
	protected void fillJson(JSONObject json) {
		// all handled in super class
	}

	@Override
	public String getIdentifier() {
		return AcceptInvite.REQUEST_ID;
	}

}
