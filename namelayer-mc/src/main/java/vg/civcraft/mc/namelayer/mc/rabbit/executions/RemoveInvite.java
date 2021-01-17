package vg.civcraft.mc.namelayer.mc.rabbit.executions;

import java.util.UUID;

import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.requests.GroupModifications;

public class RemoveInvite extends AbstractGroupModificationHandler {

	@Override
	protected void handle(Group group, JSONObject data) {
		UUID player = UUID.fromString(data.getString("player"));
		getGroupTracker().deleteInvite(group, player);
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.REMOVE_INVITE_ID;
	}

}
