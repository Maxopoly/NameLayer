package vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges;

import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.requests.GroupModifications;

public class MergeGroupMessage extends GroupChangeMessage {

	private Group finalGroup;
	
	public MergeGroupMessage(int groupDeletedID, Group finalGroup) {
		super(groupDeletedID);
		this.finalGroup = finalGroup;
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("final_group_state", finalGroup.serialize());
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.MERGE_GROUP_ID;
	}

}
