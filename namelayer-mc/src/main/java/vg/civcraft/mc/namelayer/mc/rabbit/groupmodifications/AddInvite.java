package vg.civcraft.mc.namelayer.mc.rabbit.groupmodifications;

import java.util.UUID;

import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.IllegalGroupStateException;
import vg.civcraft.mc.namelayer.core.requests.GroupModifications;

public class AddInvite extends AbstractGroupModificationHandler {

	@Override
	protected void handle(Group group, JSONObject data) {
		int rankID = data.getInt("rank_id");
		UUID player = UUID.fromString(data.getString("player"));
		GroupRank rank = group.getGroupRankHandler().getRank(rankID);
		if (rank == null) {
			throw new IllegalGroupStateException();
		}
		getGroupTracker().addInvite(player, rank, group);
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.ADD_INVITE_ID;
	}

}
