package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;

import org.json.JSONObject;

import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;

public class RabbitUnlinkGroups extends RabbitGroupAction {
	public enum FailureReason {
		ORIGINAL_GROUP_DOES_NOT_EXIST, TARGET_GROUP_DOES_NOT_EXIST, CANNOT_UNLINK_SELF, NO_PERMISSION_ORIGINAL_GROUP, NO_PERMISSION_TARGET_GROUP,
		ORIGINAL_RANK_DOES_NOT_EXIST, TARGET_RANK_DOES_NOT_EXIST, NO_LINKS_FOUND;
	}
	
	private Group targetGroup;
	private GroupRank originatingRank;
	private GroupRank targetRank;

	public RabbitUnlinkGroups(UUID executor, Group originatingGroup, GroupRank originatingRank, Group targetGroup, GroupRank targetRank) {
		super(executor, originatingGroup.getName());
		this.targetGroup = targetGroup;
		this.originatingRank = originatingRank;
		this.targetRank = targetRank; 
	}

	@Override
	public void handleReply(JSONObject reply, boolean success) {
		Group originatingGroup = getGroup();
		if (success) {
			sendMessage(String.format(""));
			return;
		}
		FailureReason reason = FailureReason.valueOf(reply.getString("reason"));
		String missingPerm = reply.optString("missing_perm", null);
		switch (reason) {
		case CANNOT_UNLINK_SELF:
			sendMessage(String.format(""));
			return;
		case NO_LINKS_FOUND:
			sendMessage(String.format("%sNo link from %s%s%s in %s%s to %s%s%s in %s%s exists", ChatColor.RED, ChatColor.GOLD,
					originatingRank.getName(), ChatColor.RED, originatingGroup.getColoredName(), ChatColor.RED,
					ChatColor.GOLD, targetRank.getName(), ChatColor.GOLD, targetGroup.getColoredName(), ChatColor.RED));
			return;
		case NO_PERMISSION_ORIGINAL_GROUP:
		case NO_PERMISSION_TARGET_GROUP:
			noPermissionMessage(missingPerm);
			return;
		case ORIGINAL_GROUP_DOES_NOT_EXIST:
		case TARGET_GROUP_DOES_NOT_EXIST:
			groupDoesNotExistMessage();
			return;
		case ORIGINAL_RANK_DOES_NOT_EXIST:
		case TARGET_RANK_DOES_NOT_EXIST:
			sendMessage(ChatColor.RED + "The rank you entered did not exist or you do not permission to promote to it");
			return;
		default:
			break;
			
		}
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("targetGroup", targetGroup);
		json.put("originatingRank", originatingRank.getId());
		json.put("targetRank", targetRank.getId());
		
	}

}
