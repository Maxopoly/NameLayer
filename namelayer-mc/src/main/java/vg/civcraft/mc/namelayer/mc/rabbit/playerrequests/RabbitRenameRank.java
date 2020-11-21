package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.requests.RenameRank;

public class RabbitRenameRank extends RabbitGroupAction {

	private String newRankName;
	private String oldRankName;

	public RabbitRenameRank(UUID executor, Group group, GroupRank oldRank, String newRankName) {
		super(executor, group.getName());
		this.oldRankName = oldRank.getName();
		this.newRankName = newRankName;
	}

	@Override
	public void handleReply(JSONObject reply, boolean success) {
		Group group = getGroup();
		if (success) {
			sendMessage(String.format("%sThe rank %s%s%s in %s%s was renamed to %s%s", ChatColor.GREEN, ChatColor.YELLOW, oldRankName,
				ChatColor.GREEN, group.getColoredName(), ChatColor.GREEN, ChatColor.GOLD, newRankName));
			return;	
		}
		RenameRank.FailureReason reason = RenameRank.FailureReason.valueOf(reply.getString("reason"));
		String missingPerm = reply.optString("missing_perm", null);
		switch (reason) {
		case GROUP_DOES_NOT_EXIST:
			groupDoesNotExistMessage();
			return;
		case NAME_ALREADY_TAKEN:
			sendMessage(String.format("%sA rank with the name %s%s%s already exists", ChatColor.RED, ChatColor.YELLOW, newRankName,
					ChatColor.RED));
			return;
		case NO_PERMISSION:
			noPermissionMessage(missingPerm);
			return;
		case SAME_NAME:
			sendMessage(String.format("%sYou can not rename a rank to the exact same name", ChatColor.RED));
			return;
		default:
			break;
			
		}
		
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("oldRankName", oldRankName);
		json.put("newRankName", newRankName);
		
	}
	
	@Override
	public String getIdentifier() {
		return RenameRank.REQUEST_ID;
	}

}
