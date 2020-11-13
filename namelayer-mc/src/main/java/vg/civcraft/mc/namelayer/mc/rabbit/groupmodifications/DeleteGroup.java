package vg.civcraft.mc.namelayer.mc.rabbit.groupmodifications;

import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;

public class DeleteGroup extends AbstractGroupModificationHandler {

	@Override
	protected void handle(Group group, JSONObject data) {
		getGroupTracker().deleteGroup(group);
	}

	@Override
	public String getIdentifier() {
		return "nl_delete_group";
	}

}
