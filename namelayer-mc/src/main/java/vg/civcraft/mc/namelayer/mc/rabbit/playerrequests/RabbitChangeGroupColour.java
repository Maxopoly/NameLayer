package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;
import org.bukkit.ChatColor;
import org.json.JSONObject;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.requests.ChangeGroupColour;

public class RabbitChangeGroupColour extends RabbitGroupAction {

	private ChatColor colour;

	public RabbitChangeGroupColour(UUID executor, String groupName, ChatColor colour) {
		super(executor, groupName);
		this.colour = colour;
	}

	@Override
	public void handleReply(JSONObject reply, boolean success) {
		Group group = getGroup();
		if (success) {
			sendMessage(String.format("%s%s%s's colour has been set to: %s", ChatColor.GREEN, group.getName(), ChatColor.GREEN, colour));
			return;
		}
		ChangeGroupColour.FailureReason reason = ChangeGroupColour.FailureReason.valueOf(reply.getString("reason"));
		String missingPerm = reply.optString("missing_perm", null);
		switch (reason) {
			case GROUP_DOES_NOT_EXIST:
				groupDoesNotExistMessage();
				return;
			case COLOUR_NOT_VALID:
				sendMessage(String.format("%s%s is not a valid colour.", ChatColor.GREEN, colour));
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
		json.put("colour", colour.toString());
	}

	@Override
	public String getIdentifier() {
		return ChangeGroupColour.REQUEST_ID;
	}
}
