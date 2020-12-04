package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.NameLayerPermissions;
import vg.civcraft.mc.namelayer.core.requests.MergeGroups;
import vg.civcraft.mc.namelayer.mc.util.MsgUtils;

public class RabbitMergeGroup extends RabbitGroupAction {
	
	private Group groupToDelete;

	public RabbitMergeGroup(UUID executor, Group groupToKeep, Group groupToDelete) {
		super(executor, groupToKeep.getName());
		this.groupToDelete = groupToDelete;
	}

	@Override
	public void handleReply(JSONObject reply, boolean success) {
		Group groupKept = getGroup();
		if (success) {
			sendMessage(String.format("%s%s%s was merged into %s", ChatColor.WHITE, groupToDelete.getName(), ChatColor.GREEN,
				groupKept.getColoredName()));
			return;
		}
		MergeGroups.FailureReason reason = MergeGroups.FailureReason.valueOf(reply.getString("reason"));
		String missingPerm = reply.optString("missing_perm", null);
		switch (reason) {
		case CANNOT_MERGE_INTO_SELF:
			sendMessage(ChatColor.RED + "You can not merge a group into itself");
			return;
		case ORIGINAL_GROUP_DOES_NOT_EXIST:
			groupDoesNotExistMessage();
			return;
		case TARGET_GROUP_DOES_NOT_EXIST:
			sendMessage(String.format("%sGroup: %s%s%s does not exist", ChatColor.RED, ChatColor.YELLOW, groupToDelete.getName(), ChatColor.RED));
			return;	
		case HAS_ACTIVE_LINKS:
			sendMessage(String.format("%s%s has active links, you need to remove them before merging",
					groupToDelete.getColoredName(), ChatColor.RED));
			return;
		case NO_PERMISSION_ORIG_GROUP:
			noPermissionMessage(missingPerm);
			return;
		case NO_PERMISSION_TARGET_GROUP:
			MsgUtils.sendNoPermissionMsg(executor, NameLayerPermissions.MERGE_GROUP, groupToDelete.getColoredName());
			return;
		default:
			break;
			
		}
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("groupToDelete", groupToDelete.getName());
	}
	
	@Override
	public String getIdentifier() {
		return MergeGroups.REQUEST_ID;
	}

}
