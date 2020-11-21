package vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges;

import java.util.UUID;

import org.json.JSONObject;

public class ChangeMemberRankMessage extends GroupMemberRankModifyMessage {

	public ChangeMemberRankMessage(int groupID, UUID player, int rankID) {
		super(groupID, player, rankID);
	}

	@Override
	protected void fillJson(JSONObject json) {
		//Nothing to add
	}

	@Override
	public String getIdentifier() {
		return "nl_change_rank";
	}

}
