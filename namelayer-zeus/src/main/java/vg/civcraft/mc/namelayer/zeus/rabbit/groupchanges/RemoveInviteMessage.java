package vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges;

import java.util.UUID;

import org.json.JSONObject;

public class RemoveInviteMessage extends GroupMemberModifyMessage {

	public RemoveInviteMessage(int groupID, UUID player) {
		super(groupID, player);
	}

	@Override
	public String getIdentifier() {
		return "nl_remove_invite";
	}

	@Override
	protected void fillJson(JSONObject json) {
		//nothing needed
	}

}
