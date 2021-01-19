package vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges;

import org.json.JSONObject;
import vg.civcraft.mc.namelayer.core.log.abstr.LoggedGroupAction;
import vg.civcraft.mc.namelayer.core.requests.GroupModifications;

public class AddToActionLogMessage extends GroupChangeMessage{

	private LoggedGroupAction action;

	public AddToActionLogMessage(int groupID, LoggedGroupAction action) {
		super(groupID);
		this.action = action;
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("action", action.toJson());
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.ADD_TO_ACTION_LOG;
	}
}
