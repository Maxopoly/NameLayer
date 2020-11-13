package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.mc.rabbit.playerrequests.RabbitDeleteGroup.FailureReason;

public class RabbitLinkGroups extends RabbitGroupAction {
	public enum FailureReason {
		CANNOT_LINK_TO_SELF, NO_PERMISSION_ORIG_GROUP, NO_PERMISSION_TARGET_GROUP, ATTEMPTED_GROUP_CYCLING;
	}
	
	private Group targetGroup;
	private GroupRank originatingRank;
	private GroupRank targetRank;

	public RabbitLinkGroups(UUID executor, Group originatingGroup, Group targetGroup, GroupRank originatingRank, GroupRank targetRank) {
		super(executor, originatingGroup.getName());
		this.targetGroup = targetGroup;
		this.originatingRank = originatingRank;
		this.targetRank = targetRank;
	}

	@Override
	public void handleReply(JSONObject reply, boolean success) {
		if (success) {
			sendMessage(String.format("%sSuccessfully linked %s%s%s in %s%s to %s%s%s in %s", ChatColor.GREEN, ChatColor.GOLD,
				originatingRank.getName(), ChatColor.GREEN, getGroup().getColoredName(), ChatColor.GREEN,
				ChatColor.YELLOW, targetRank.getName(), ChatColor.GREEN, targetGroup.getColoredName()));
		}
		FailureReason reason = FailureReason.valueOf(reply.getString("reason"));
		String missingPerm = reply.optString("missing_perm", null);	
		switch (reason) {
		case ATTEMPTED_GROUP_CYCLING:
			sendMessage(ChatColor.RED + "Link could not be created, because it would create a cycle");
			return;
		case CANNOT_LINK_TO_SELF:
			sendMessage(ChatColor.RED + "You can not link a group to itself");
			return;
		case NO_PERMISSION_ORIG_GROUP:
			noPermissionMessage(missingPerm);
			return;
		case NO_PERMISSION_TARGET_GROUP:
			noPermissionMessage(missingPerm);
			return;
		default:
			break;
			
		}
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("targetGroup", targetGroup.getName());
		json.put("originatingRank", originatingRank.getId());
		json.put("targetRank", targetRank.getId());
		
	}

}
