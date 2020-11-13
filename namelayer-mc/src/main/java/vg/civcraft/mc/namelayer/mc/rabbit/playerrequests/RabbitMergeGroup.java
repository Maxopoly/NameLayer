package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;

public class RabbitMergeGroup extends RabbitGroupAction {
	public enum FailureReason {
		NO_PERMISSION_ORIG_GROUP, NO_PERMISSION_TARGET_GROUP, CANNOT_MERGE_INTO_SELF, GROUP_DOES_NOT_EXIST, HAS_INCOMING_LINKS, HAS_OUTGOING_LINKS;
	}
	
	Group groupToDelete;

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
		FailureReason reason = FailureReason.valueOf(reply.getString("reason"));
		String missingPerm = reply.optString("missing_perm", null);
		switch (reason) {
		case CANNOT_MERGE_INTO_SELF:
			sendMessage(ChatColor.RED + "You can not merge a group into itself");
			return;
		case GROUP_DOES_NOT_EXIST:
			groupDoesNotExistMessage();
			return;
		case HAS_INCOMING_LINKS:
			sendMessage(String.format("%s%s has active links, you need to remove them before merging",
					groupToDelete.getColoredName(), ChatColor.RED));
			return;
		case HAS_OUTGOING_LINKS:
			sendMessage(String.format("%s%s has active links, you need to remove them before merging",
					groupToDelete.getColoredName(), ChatColor.RED));
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
		json.put("groupTDoelete", groupToDelete.getName());
	}

}
