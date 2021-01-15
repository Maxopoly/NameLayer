package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.requests.CreateRank;

public class RabbitCreateRank extends RabbitGroupAction {

	private String newRankName;
	private GroupRank parentRank;

	public RabbitCreateRank(UUID executor, Group group, GroupRank parentRank, String newRankName) {
		super(executor, group.getName());
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
		CreateRank.FailureReason reason = CreateRank.FailureReason.valueOf(reply.getString("reason"));
		switch (reason) {
		case GROUP_DOES_NOT_EXIST:
			groupDoesNotExistMessage();
			return;	
		case NO_PERMISSION:
			String missingPerm = reply.getString("missing_perm");
			noPermissionMessage(missingPerm);
			return;
		case PARENT_RANK_DOES_NOT_EXIST:
			sendMessage(String.format("%sThe rank %s%s%s does not exist", ChatColor.RED, ChatColor.YELLOW, parentRank.getName(),
					ChatColor.RED));
			return;
		case RANK_ALREADY_EXISTS:
			sendMessage(String.format("%sA rank named %s%s%s already exists", ChatColor.RED, ChatColor.YELLOW, newRankName,
					ChatColor.RED));
			return;
		case INVALID_RANK_NAME:
			sendMessage(String.format("%sThe rank %s%s%s is an invalid format", ChatColor.RED, ChatColor.YELLOW, newRankName, ChatColor.RED));
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

	@Override
	public String getIdentifier() {
		return CreateRank.REQUEST_ID;
	}

}
