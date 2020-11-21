package vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges;

import java.util.UUID;

import org.json.JSONObject;

public class RemoveMemberMessage extends GroupMemberModifyMessage {

	public RemoveMemberMessage(int groupID, UUID player) {
		super(groupID, player);
	}

	@Override
	public String getIdentifier() {
		return "nl_remove_member";
	}

	@Override
	protected void fillJson(JSONObject json) {
		//nothing needed
	}

}
