package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;

public class RabbitBlacklistPlayer extends RabbitGroupAction {
	public enum FailureReason {
		GROUP_DOES_NOT_EXIST, PLAYER_DOES_NOT_EXIST, RANK_DOES_NOT_EXIST, NO_PERMISSION, NOT_BLACKLISTED_RANK;
	}
	
	private String targetPlayer;
	private GroupRank rank;

	public RabbitBlacklistPlayer(UUID executor, String groupName, String targetPlayer, GroupRank rank) {
		super(executor, groupName);
		this.targetPlayer = targetPlayer;
		this.rank = rank;
	}

	@Override
	public void handleReply(JSONObject reply, boolean success) {
		Group group = getGroup();
		if (success) {
			sendMessage(String.format("%s%s %shas been blacklisted as %s%s%s in %s", ChatColor.YELLOW,
				this.targetPlayer, ChatColor.GREEN, ChatColor.YELLOW, rank.getName(),
				ChatColor.YELLOW, group.getColoredName()));
		}
		FailureReason reason = FailureReason.valueOf(reply.getString("reason"));
		switch (reason) {
		case GROUP_DOES_NOT_EXIST:
			groupDoesNotExistMessage();
			return;
		case NO_PERMISSION:
			sendMessage(String.format("%s%s%s is not a blacklist rank in %s", ChatColor.YELLOW, rank.getName(),
					ChatColor.RED, getGroup().getColoredName()));
			return;
		case PLAYER_DOES_NOT_EXIST:
			playerDoesNotExistMessage(this.targetPlayer);
			return;
		case RANK_DOES_NOT_EXIST:
			sendMessage(ChatColor.RED + "The rank you entered did not exist or you do not have permission to blacklist on it");
			return;
		case NOT_BLACKLISTED_RANK:
			return;	
		default:
			break;
			
		}
		
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("target_player", targetPlayer);
		json.put("rank", rank);
	}

}
