package vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges;

import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.requests.GroupModifications;

public class DeleteRankMessage extends GroupChangeMessage {

	private int rankID;
	
	public DeleteRankMessage(int groupID, int rankID) {
		super(groupID);
		this.rankID = rankID;
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("rank_id", rankID);
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.DELETE_RANK_ID;
	}

}
