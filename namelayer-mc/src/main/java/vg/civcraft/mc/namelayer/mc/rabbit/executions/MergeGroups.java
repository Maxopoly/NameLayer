package vg.civcraft.mc.namelayer.mc.rabbit.executions;

import org.json.JSONObject;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.requests.GroupModifications;
import vg.civcraft.mc.namelayer.mc.GroupAPI;

public class MergeGroups extends AbstractGroupModificationHandler{
	@Override
	protected void handle(Group groupDeleted, JSONObject data) {
		Group mergedGroupState = Group.fromJson(data.getJSONObject("final_group_state"));
		Group oldGroupState = GroupAPI.getGroup(mergedGroupState.getPrimaryId());
		getGroupTracker().deleteGroup(groupDeleted);
		getGroupTracker().deleteGroup(oldGroupState);
		getGroupTracker().addGroup(mergedGroupState);
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.MERGE_GROUP_ID;
	}
}
