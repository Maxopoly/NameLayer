package vg.civcraft.mc.namelayer.mc.rabbit.groupmodifications;

import java.util.UUID;

import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;

public class RemoveInvite extends AbstractGroupModificationHandler {

	@Override
	protected void handle(Group group, JSONObject data) {
		UUID player = UUID.fromString(data.getString("member"));
		getGroupTracker().deleteInvite(group, player);
	}

	@Override
	public String getIdentifier() {
		return "nl_remove_invite";
	}

}
