package vg.civcraft.mc.namelayer.mc.rabbit.executions;

import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.requests.GroupModifications;

public class DeleteRank extends AbstractGroupModificationHandler {

	@Override
	protected void handle(Group group, JSONObject data) {
		int rankId = data.getInt("rank_id");
		GroupRank rank = group.getGroupRankHandler().getRank(rankId);
		group.getGroupRankHandler().deleteRank(rank);
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.DELETE_RANK_ID;
	}

}
