package vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges;

import org.json.JSONObject;

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
		return "nl_delete_group";
	}

}
