package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;
import org.json.JSONObject;

import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.requests.ChangeGroupColor;

public class RabbitChangeGroupColor extends RabbitGroupAction {

	private ChatColor color;

	public RabbitChangeGroupColor(UUID executor, String groupName, ChatColor color) {
		super(executor, groupName);
		this.color = color;
	}

	@Override
	public void handleReply(JSONObject reply, boolean success) {
		Group group = getGroup();
		if (success) {
			sendMessage(String.format("%s%s%s's color has been set to: %s%s", group.getColoredName(), ChatColor.RESET, ChatColor.GREEN, color, color.name()));
			return;
		}
		ChangeGroupColor.FailureReason reason = ChangeGroupColor.FailureReason.valueOf(reply.getString("reason"));
		String missingPerm = reply.optString("missing_perm", null);
		switch (reason) {
			case GROUP_DOES_NOT_EXIST:
				groupDoesNotExistMessage();
				return;
			case COLOR_NOT_VALID:
				sendMessage(String.format("%s%s is not a valid color.", ChatColor.GREEN, color.name()));
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
		json.put("color", color.toString());
	}

	@Override
	public String getIdentifier() {
		return ChangeGroupColor.REQUEST_ID;
	}
}
