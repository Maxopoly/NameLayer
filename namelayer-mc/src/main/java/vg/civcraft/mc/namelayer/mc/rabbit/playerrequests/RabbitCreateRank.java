package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.GroupRank;

public class RabbitCreateRank extends RabbitGroupAction {
	public enum FailureReason {
		RANK_ALREADY_EXISTS, NO_PERMISSION, RANK_LIMIT_REACHED, PARENT_RANK_DOES_NOT_EXIST;
	}

	private String newRankName;
	private GroupRank parentRank;

	public RabbitCreateRank(UUID executor, String groupName, GroupRank parentRank, String newRankName) {
		super(executor, groupName);
		this.newRankName = newRankName;
		this.parentRank = parentRank;
	}

	@Override
	public void handleReply(JSONObject reply, boolean success) {
		if (success) {
			sendMessage(String.format("%sSuccessfully added %s%s%s as sub rank of %s%s", ChatColor.GREEN, ChatColor.GOLD, newRankName,
				ChatColor.GREEN, ChatColor.YELLOW, parentRank.getName()));
			return;
		}
		FailureReason reason = FailureReason.valueOf(reply.getString("reason"));
		switch (reason) {
		case NO_PERMISSION:
			String missingPerm = reply.getString("missing_perm");
			noPermissionMessage(missingPerm);
			return;
		case PARENT_RANK_DOES_NOT_EXIST:
			sendMessage(String.format("%sThe rank %s%s%s does not exist", ChatColor.RED, ChatColor.YELLOW, parentRank.getName(),
					ChatColor.RED));
			return;
		case RANK_ALREADY_EXISTS:
			sendMessage(String.format("%sA rank named %s%s%s already exists", ChatColor.RED, ChatColor.YELLOW, parentRank.getName(),
					ChatColor.RED));
			return;
		case RANK_LIMIT_REACHED:
			int maxRanks = reply.getInt("max_ranks");
			sendMessage(String.format("%sYou have reached the maximum amount of ranks (%d). You'll have to delete some before creating new ones",
					ChatColor.RED, maxRanks));
			return;
		default:
			break;
		}
		
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("newRankName", newRankName);
		json.put("parentRank", parentRank.getId());
		
	}

}
