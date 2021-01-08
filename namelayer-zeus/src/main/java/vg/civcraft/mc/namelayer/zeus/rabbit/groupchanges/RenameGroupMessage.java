package vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges;

import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.requests.GroupModifications;

public class RenameGroupMessage extends GroupChangeMessage {

	private String newName;
	
	public RenameGroupMessage(int groupID, String newName) {
		super(groupID);
		this.newName = newName;
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("new_name", newName);
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.RENAME_GROUP_ID;
	}

}
