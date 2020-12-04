package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.requests.LinkGroups;

public class RabbitLinkGroups extends RabbitGroupAction {
	
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
		LinkGroups.FailureReason reason = LinkGroups.FailureReason.valueOf(reply.getString("reason"));
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
		case ORIGINAL_GROUP_DOES_NOT_EXIST:
			sendMessage(String.format("%s%s%s does not exist", ChatColor.YELLOW, getGroup().getName(), ChatColor.RED));
			return;
		case TARGET_GROUP_DOES_NOT_EXIST:
			sendMessage(String.format("%s%s%s does not exist", ChatColor.YELLOW, targetGroup.getName(), ChatColor.RED));
			return;
		case ORIGINAL_GROUP_RANK_DOES_NOT_EXIST:
			sendMessage(String.format("%s%s%s rank does not exist", ChatColor.YELLOW,  originatingRank.getName(), ChatColor.RED));
			return;
		case TARGET_GROUP_RANK_DOES_NOT_EXIST:
			sendMessage(String.format("%s%s%s rank does not exist", ChatColor.YELLOW,  targetRank.getName(), ChatColor.RED));
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
	
	@Override
	public String getIdentifier() {
		return LinkGroups.REQUEST_ID;
	}

}
