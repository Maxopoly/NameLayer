package vg.civcraft.mc.namelayer.mc.rabbit.groupmodifications;

import java.util.UUID;

import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.IllegalGroupStateException;
import vg.civcraft.mc.namelayer.core.requests.GroupModifications;

public class UpdateMemberRank extends AbstractGroupModificationHandler {

	@Override
	protected void handle(Group group, JSONObject data) {
		UUID player = UUID.fromString(data.getString("player"));
		int rankID = data.getInt("rank_id");
		GroupRank rank = group.getGroupRankHandler().getRank(rankID);
		if (rank == null) {
			throw new IllegalGroupStateException();
		}
		getGroupTracker().updatePlayerRankInGroup(group, player, rank);
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.CHANGE_RANK_ID;
	}

}
