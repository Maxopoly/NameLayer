package vg.civcraft.mc.namelayer.mc.rabbit.executions;

import org.json.JSONObject;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.requests.GroupModifications;
import vg.civcraft.mc.namelayer.mc.GroupAPI;

public class MergeGroups extends AbstractGroupModificationHandler{
	@Override
	protected void handle(Group groupDeleted, JSONObject data) {
		int groupKeptID = data.getInt("group_merged_into");
		Group groupKept = GroupAPI.getGroup(groupKeptID);

		getGroupTracker().deleteGroup(groupDeleted);
		getGroupTracker().deleteGroup(groupKept);


	}

	@Override
	public String getIdentifier() {
		return GroupModifications.MERGE_GROUP_ID;
	}
}
