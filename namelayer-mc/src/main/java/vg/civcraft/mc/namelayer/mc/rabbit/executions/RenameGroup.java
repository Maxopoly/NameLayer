package vg.civcraft.mc.namelayer.mc.rabbit.executions;

import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.requests.GroupModifications;

public class RenameGroup extends AbstractGroupModificationHandler {

	@Override
	protected void handle(Group group, JSONObject data) {
		String newName = data.getString("new_name");
		getGroupTracker().renameGroup(group, newName);
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.RENAME_GROUP_ID;
	}

}
