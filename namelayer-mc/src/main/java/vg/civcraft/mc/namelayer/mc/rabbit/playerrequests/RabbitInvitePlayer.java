package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;
import java.util.function.Consumer;

import org.json.JSONObject;

import com.github.civcraft.artemis.rabbit.MCStandardRequest;

import vg.civcraft.mc.namelayer.core.GroupRank;

public class RabbitInvitePlayer extends RabbitGroupAction {

	public enum FailureReason {
		GROUP_DOES_NOT_EXIST, PLAYER_DOES_NOT_EXIST, NO_PERMISSION;
	}

	private String playerName;
	private GroupRank targetRank;

	public RabbitInvitePlayer(UUID executor, String groupName, String playerName, GroupRank targetRank) {
		super(executor, groupName);
		this.playerName = playerName;
		this.targetRank = targetRank;
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("target_player", targetPlayer);
		json.put("rank", rank.getId());
	}

	@Override
	public void handleReply(JSONObject reply, boolean success) {
		Group group = getGroup();
		if (success) {
			sendMessage("%s%s %shas been invited as %s%s%s to %s", ChatColor.YELLOW,
				playerName, ChatColor.GREEN, ChatColor.YELLOW, targetRank.getName(),
				ChatColor.GREEN, group.getColoredName());
			return;
		}
		FailureReason reason = FailureReason.valueOf(reply.getString("reason"));
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

}
