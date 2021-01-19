package vg.civcraft.mc.namelayer.mc.rabbit.executions;

import org.json.JSONObject;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.log.abstr.LoggedGroupAction;
import vg.civcraft.mc.namelayer.core.requests.GroupModifications;

public class AddToActionLog extends AbstractGroupModificationHandler{
	@Override
	protected void handle(Group group, JSONObject data) {
		//TODO
		LoggedGroupAction action = null;
		getGroupTracker().addLogEntry(group, action);
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.ADD_TO_ACTION_LOG;
	}
}
