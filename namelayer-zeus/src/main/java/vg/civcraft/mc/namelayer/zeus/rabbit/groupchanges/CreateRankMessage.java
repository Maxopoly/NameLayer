package vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges;

import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.requests.GroupModifications;

public class CreateRankMessage extends GroupChangeMessage {

	private int rankID;
	private String rankName;
	private int parentID;
	
	public CreateRankMessage(int groupID, int rankID, String rankName, int parentID) {
		super(groupID);
		this.rankID = rankID;
		this.rankName = rankName;
		this.parentID = parentID;
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("rank_id", rankID);
		json.put("rank_name", rankName);
		json.put("parent_id", parentID);
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.CREATE_RANK_ID;
	}

}
