package vg.civcraft.mc.namelayer.mc.rabbit.groupmodifications;

import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;

public class DeleteRank extends AbstractGroupModificationHandler {

	@Override
	protected void handle(Group group, JSONObject data) {
		int rankId = data.getInt("rank_id");
		GroupRank rank = group.getGroupRankHandler().getRank(rankId);
		group.getGroupRankHandler().deleteRank(rank);
	}

	@Override
	public String getIdentifier() {
		return "nl_delete_rank";
	}

}
