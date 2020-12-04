package vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges;

import java.util.UUID;

import org.json.JSONObject;

public abstract class GroupMemberRankModifyMessage extends GroupMemberModifyMessage {

	private int rankID;
	
	public GroupMemberRankModifyMessage(int groupID, UUID player, int rankID) {
		super(groupID, player);
		this.rankID = rankID;
	}

	@Override
	protected void enrichJson(JSONObject json) {
		super.enrichJson(json);
		json.put("rank_id", rankID);
	}
}
