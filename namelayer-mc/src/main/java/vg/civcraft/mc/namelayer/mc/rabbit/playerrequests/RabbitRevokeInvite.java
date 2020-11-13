package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.json.JSONObject;
import vg.civcraft.mc.namelayer.mc.GroupAPI;

public class RabbitRevokeInvite extends RabbitGroupAction {
	public enum FailureReason {
		GROUP_DOES_NOT_EXIST, PLAYER_DOES_NOT_EXIST, RANK_DOES_NOT_EXIST, NO_PERMISSION;
	}

	private String playerName;

	public RabbitRevokeInvite(UUID executor, Group groupName, String playerName) {
		super(executor, groupName.getName());
		this.playerName = playerName;
	}

	@Override
	public void handleReply(JSONObject reply, boolean success) {
		Group group = getGroup();
		if (success) {
			GroupAPI rankInvitedTo = group.getInvite(executor);
			sendMessage(String.format("%sRevoked an invite to %s%s as %s%s%s from %s%s", ChatColor.GREEN,
					group.getColoredName(), ChatColor.GREEN, ChatColor.GOLD, rankInvitedTo.getName(), ChatColor.GREEN,
					ChatColor.YELLOW, playerName));
			return;		
		}
		FailureReason reason = FailureReason.valueOf(reply.getString("reason"));
		String missingPerm = reply.optString("missing_perm", null);
		switch (reason) {
		case GROUP_DOES_NOT_EXIST:
			groupDoesNotExistMessage();
			return;
		case NO_PERMISSION:
			sendMessage(String.format("%s%s%s has not been invited to %s%s or you do not have permission to revoke their invite",
					ChatColor.YELLOW, playerName, ChatColor.RED, group.getColoredName(),
					ChatColor.RED));
			return;
		case PLAYER_DOES_NOT_EXIST:
			playerDoesNotExistMessage(playerName);
			return;
		case RANK_DOES_NOT_EXIST:
			sendMessage(String.format("%s%s%s has not been invited to %s%s or you do not have permission to revoke their invite",
					ChatColor.YELLOW, playerName, ChatColor.RED, group.getColoredName(),
					ChatColor.RED));
			return;
		default:
			break;

		}

	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("playerName", playerName);
		
	}

}
