package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;

public class RabbitPromotePlayer extends RabbitGroupAction {
	public enum FailureReason {
		NO_PERMISSION, GROUP_DOES_NOT_EXIST, PLAYER_DOES_NOT_EXIST, RANK_DOES_NOT_EXIST, SAME_AS_CURRENT_RANK, BLACKLISTED;
	}
	
	private String playerName;
	private GroupRank targetRank;
	private GroupRank oldRank;

	public RabbitPromotePlayer(UUID executor, Group group, String playerName, GroupRank targetRank) {
		super(executor, group.getName());
		this.playerName = playerName;
		this.targetRank = targetRank;
		this.oldRank = group.getRank(executor);
	}

	@Override
	public void handleReply(JSONObject reply, boolean success) {
		Group group = getGroup();
		if (success) {
			sendMessage(String.format("Changed rank of %s%s%s from %s%s%s to %s%s%s in %s%s", ChatColor.GREEN, ChatColor.YELLOW,
				playerName, ChatColor.GREEN, ChatColor.YELLOW, oldRank.getName(),
				ChatColor.GREEN, ChatColor.YELLOW, targetRank.getName(), ChatColor.GREEN, group.getColoredName()));
			return;	
		}
		FailureReason reason = FailureReason.valueOf(reply.getString("reason"));
		switch (reason) {
		case BLACKLISTED:
			sendMessage(String.format("%s%s%s currently has a blacklisted rank and can not be promoted to a member rank. "
							+ "Remove them from the blacklist rank and invite them first.",
					ChatColor.YELLOW, playerName, ChatColor.RED));
			return;
		case GROUP_DOES_NOT_EXIST:
			groupDoesNotExistMessage();
			return;
		case NO_PERMISSION:
		case RANK_DOES_NOT_EXIST:
			sendMessage(ChatColor.RED + "The rank you entered did not exist or you do not permission to promote to it");
			return;
		case PLAYER_DOES_NOT_EXIST:
			playerDoesNotExistMessage(playerName);
			return;
		case SAME_AS_CURRENT_RANK:
			sendMessage(String.format("%s%s%s already has the rank %s%s%s in %s", ChatColor.YELLOW,
					playerName, ChatColor.RED, ChatColor.GOLD, oldRank.getName(),
					ChatColor.RED, group.getColoredName()));
			return;
		default:
			break;
			
		}
		
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("playerName", playerName);
		json.put("targetRank", targetRank.getId());
	}

}
