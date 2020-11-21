package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.requests.RemoveMember;

public class RabbitRemoveMember extends RabbitGroupAction {

	
	private String playerName;

	public RabbitRemoveMember(UUID executor, Group group, String playerName) {
		super(executor, group.getName());
		this.playerName = playerName;
	}

	@Override
	public void handleReply(JSONObject reply, boolean success) {
		Group group = getGroup();
		String currentRank = reply.getString("current_rank");
		if (success) {
			sendMessage(String.format("%s%s%s with the rank %s%s%s was kicked from %s", ChatColor.YELLOW,
				playerName, ChatColor.GREEN, ChatColor.YELLOW, currentRank,
				ChatColor.GREEN, group.getColoredName()));
			return;
		}
		RemoveMember.FailureReason reason = RemoveMember.FailureReason.valueOf(reply.getString("reason"));
		String missingPerm = reply.optString("missing_perm", null);
		switch (reason) {
		case CANNOT_KICK_SELF:
			sendMessage(ChatColor.RED + "You can not kick yourself from a group, leave it instead");
			return;
		case GROUP_DOES_NOT_EXIST:
			groupDoesNotExistMessage();
			return;
		case NOT_A_MEMBER:
			sendMessage(ChatColor.RED + "The player is not a member of the group or you do not have sufficient permission to demote them");
			return;
		case NO_PERMISSION:
			noPermissionMessage(missingPerm);
			return;
		case PLAYER_DOES_NOT_EXIST:
			playerDoesNotExistMessage(playerName);
			return;
		default:
			break;
			
		}
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("playerName", playerName);
	}
	
	@Override
	public String getIdentifier() {
		return RemoveMember.REQUEST_ID;
	}

}
