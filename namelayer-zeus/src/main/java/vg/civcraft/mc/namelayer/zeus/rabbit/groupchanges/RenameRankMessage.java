package vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges;

import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.requests.GroupModifications;

public class RenameRankMessage extends GroupChangeMessage {
	
	private int rankID;
	private String newRankName;

	public RenameRankMessage(int groupID, int rankID, String newRankName) {
		super(groupID);
		this.rankID = rankID;
		this.newRankName = newRankName;
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("rank_id", rankID);
		json.put("new_name", newRankName);
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.RENAME_RANK_ID;
	}

}
