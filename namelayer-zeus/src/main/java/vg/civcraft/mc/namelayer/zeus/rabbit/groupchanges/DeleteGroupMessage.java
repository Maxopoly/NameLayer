package vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges;

import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.requests.GroupModifications;

public class DeleteGroupMessage extends GroupChangeMessage {

	public DeleteGroupMessage(int groupID) {
		super(groupID);
	}

	@Override
	protected void fillJson(JSONObject json) {
		//Nothing else
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.DELETE_GROUP_ID;
	}

}
