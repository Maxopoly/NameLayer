package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.requests.RevokeInvite;

public class RabbitRevokeInvite extends RabbitGroupAction {


	private String playerName;

	public RabbitRevokeInvite(UUID executor, Group group, String playerName) {
		super(executor, group.getName());
		this.playerName = playerName;
	}

	@Override
	public void handleReply(JSONObject reply, boolean success) {
		Group group = getGroup();
		String rankInvitedTo = reply.optString("rank_invited_to", null);
		if (success) {
			sendMessage(String.format("%sRevoked an invite to %s%s as %s%s%s from %s%s", ChatColor.GREEN,
					group.getColoredName(), ChatColor.GREEN, ChatColor.GOLD, rankInvitedTo, ChatColor.GREEN,
					ChatColor.YELLOW, playerName));
			return;
		}
		RevokeInvite.FailureReason reason = RevokeInvite.FailureReason.valueOf(reply.getString("reason"));
		switch (reason) {
		case GROUP_DOES_NOT_EXIST:
			groupDoesNotExistMessage();
			return;
		case PLAYER_DOES_NOT_EXIST:
			playerDoesNotExistMessage(playerName);
			return;
		case NO_PERMISSION:
		case RANK_DOES_NOT_EXIST:
			sendMessage(String.format(
					"%s%s%s has not been invited to %s%s or you do not have permission to revoke their invite",
					ChatColor.YELLOW, playerName, ChatColor.RED, group.getColoredName(), ChatColor.RED));
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
		return RevokeInvite.REQUEST_ID;
	}

}
