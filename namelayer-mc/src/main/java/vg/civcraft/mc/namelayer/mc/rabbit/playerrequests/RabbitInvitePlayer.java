package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.requests.InvitePlayer;

public class RabbitInvitePlayer extends RabbitGroupAction {

	private String playerName;
	private GroupRank targetRank;

	public RabbitInvitePlayer(UUID executor, Group group, String playerName, GroupRank targetRank) {
		super(executor, group.getName());
		this.playerName = playerName;
		this.targetRank = targetRank;
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("target_player", playerName);
		json.put("rank", targetRank.getId());
	}

	@Override
	public void handleReply(JSONObject reply, boolean success) {
		Group group = getGroup();
		if (success) {
			sendMessage(String.format("%s%s %shas been invited as %s%s%s to %s", ChatColor.YELLOW,
				playerName, ChatColor.GREEN, ChatColor.YELLOW, targetRank.getName(),
				ChatColor.GREEN, group.getColoredName()));
			return;
		}
		InvitePlayer.FailureReason reason = InvitePlayer.FailureReason.valueOf(reply.getString("reason"));
		String missingPerm = reply.optString("missing_perm", null);
		switch (reason) {
		case GROUP_DOES_NOT_EXIST:
			groupDoesNotExistMessage();
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
	public String getIdentifier() {
		return InvitePlayer.REQUEST_ID;
	}

}
