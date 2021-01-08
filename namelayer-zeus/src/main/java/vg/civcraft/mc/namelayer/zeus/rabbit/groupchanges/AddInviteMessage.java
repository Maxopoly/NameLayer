package vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges;

import java.util.UUID;

import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.requests.GroupModifications;

public class AddInviteMessage extends GroupMemberRankModifyMessage {

	public AddInviteMessage(int groupID, UUID player, int rankID) {
		super(groupID, player, rankID);
	}

	@Override
	protected void fillJson(JSONObject json) {
		//nothing needed
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.ADD_INVITE_ID;
	}
}
