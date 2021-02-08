package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.core.requests.EditPermission;

public class RabbitEditPermission extends RabbitGroupAction {


	private boolean adding;
	private GroupRank rankName;
	private PermissionType permissionName;

	public RabbitEditPermission(UUID executor, Group group, boolean adding, GroupRank rankName,
			PermissionType permissionName) {
		super(executor, group.getName());
		this.adding = adding;
		this.rankName = rankName;
		this.permissionName = permissionName;
	}

	@Override
	public void handleReply(JSONObject reply, boolean success) {
		Group group = getGroup();
		if (success) {
			if (adding) {
				sendMessage(String.format("%sAdded the permission %s%s%s to %s%s%s for %s", ChatColor.GREEN,
						ChatColor.YELLOW, permissionName.getName(), ChatColor.GREEN, ChatColor.YELLOW,
						rankName.getName(), ChatColor.GREEN, group.getColoredName()));
			} else {
				sendMessage(String.format("%sRemoved the permission %s%s%s from %s%s%s for %s", ChatColor.GREEN,
						ChatColor.YELLOW, permissionName.getName(), ChatColor.GREEN, ChatColor.YELLOW, rankName.getName(),
						ChatColor.GREEN, group.getColoredName()));
			}
			return;
		}
		EditPermission.FailureReason reason = EditPermission.FailureReason.valueOf(reply.getString("reason"));
		String missingPerm = reply.optString("missing_perm", null);
		switch (reason) {
		case GROUP_DOES_NOT_EXIST:
			groupDoesNotExistMessage();
			return;
		case NO_PERMISSION:
			noPermissionMessage(missingPerm);
			return;
		case PERMISSION_DOES_NOT_EXIST:
			sendMessage(String.format("%sThe permission %s%s%s does not exist", ChatColor.RED, ChatColor.YELLOW, rankName,
					ChatColor.RED));
			return;
		case RANK_ALREADY_HAS_PERMISSION:
			sendMessage(String.format("%sThe rank %s%s%s of %s%s already has the permission %s%s", ChatColor.RED,
						ChatColor.YELLOW, rankName.getName(), ChatColor.RED, group.getColoredName(), ChatColor.RED,
						ChatColor.YELLOW, permissionName.getName()));
			return;
		case RANK_DOES_NOT_EXIST:
			sendMessage(ChatColor.RED + "That rank does not exist");
			return;
		case RANK_LACKS_PERMISSION:
			sendMessage(String.format("%sThe rank %s%s%s of %s%s does not have the permission %s%s", ChatColor.RED,
						ChatColor.YELLOW, rankName.getName(), ChatColor.RED, group.getColoredName(), ChatColor.RED,
						ChatColor.YELLOW, permissionName.getName()));
			return;
		default:
			break;
		}
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("adding", adding);
		json.put("target_rank_id", rankName.getId());
		json.put("permissionName", permissionName.getName());
	}
		
	@Override
	public String getIdentifier() {
		return EditPermission.REQUEST_ID;
	}

}
