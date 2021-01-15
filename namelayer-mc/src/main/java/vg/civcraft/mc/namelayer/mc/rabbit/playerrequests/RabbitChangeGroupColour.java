package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;
import org.bukkit.ChatColor;
import org.json.JSONObject;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.requests.ChangeGroupColor;

public class RabbitChangeGroupColour extends RabbitGroupAction {

	private ChatColor color;

	public RabbitChangeGroupColour(UUID executor, String groupName, ChatColor color) {
		super(executor, groupName);
		this.color = color;
	}

	@Override
	public void handleReply(JSONObject reply, boolean success) {
		Group group = getGroup();
		if (success) {
			sendMessage(String.format("%s%s%s's color has been set to: %s", ChatColor.GREEN, group.getName(), ChatColor.GREEN, color));
			return;
		}
		ChangeGroupColor.FailureReason reason = ChangeGroupColor.FailureReason.valueOf(reply.getString("reason"));
		String missingPerm = reply.optString("missing_perm", null);
		switch (reason) {
			case GROUP_DOES_NOT_EXIST:
				groupDoesNotExistMessage();
				return;
			case COLOR_NOT_VALID:
				sendMessage(String.format("%s%s is not a valid color.", ChatColor.GREEN, color));
				return;
			case NO_PERMISSION:
				noPermissionMessage(missingPerm);
				return;
			default:
				break;
		}
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("colour", color.toString());
	}

	@Override
	public String getIdentifier() {
		return ChangeGroupColor.REQUEST_ID;
	}
}
