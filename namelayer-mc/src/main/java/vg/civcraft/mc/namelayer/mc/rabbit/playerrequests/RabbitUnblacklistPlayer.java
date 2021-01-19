package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import org.json.JSONObject;
import vg.civcraft.mc.namelayer.core.requests.UnblacklistPlayer;

public class RabbitUnblacklistPlayer extends RabbitGroupAction {

	private String targetPlayer;

	public RabbitUnblacklistPlayer(UUID executor, String groupName, String targetPlayer) {
		super(executor, groupName);
		this.targetPlayer = targetPlayer;
	}

	@Override
	public void handleReply(JSONObject reply, boolean success) {
		if (success) {
			sendMessage(ChatColor.GREEN + "You have successfully unblacklisted " + targetPlayer + " from " + groupName);
			return;
		}
		UnblacklistPlayer.FailureReason reason = UnblacklistPlayer.FailureReason.valueOf(reply.getString("reason"));
		String missingPerm = reply.optString("missing_perm", null);
		switch (reason) {
			case NO_PERMISSION:
				noPermissionMessage(missingPerm);
				return;
			case GROUP_DOES_NOT_EXIST:
				groupDoesNotExistMessage();
				return;
			case PLAYER_DOES_NOT_EXIST:
				playerDoesNotExistMessage(targetPlayer);
				return;
			case PLAYER_NOT_BLACKLISTED:
				sendMessage(ChatColor.RED + targetPlayer + " is not in a blacklisted rank.");
				return;
			default:
				break;
		}
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("targetPlayer", targetPlayer);
	}

	@Override
	public String getIdentifier() {
		return UnblacklistPlayer.REQUEST_ID;
	}
}
