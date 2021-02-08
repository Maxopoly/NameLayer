package vg.civcraft.mc.namelayer.mc.rabbit.executions;

import org.json.JSONObject;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.requests.GroupModifications;

public class MergeGroups extends AbstractGroupModificationHandler{
	@Override
	protected void handle(Group group, JSONObject data) {
		//TODO: Fix when group merging is added to groupTracker
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.MERGE_GROUP_ID;
	}
}
