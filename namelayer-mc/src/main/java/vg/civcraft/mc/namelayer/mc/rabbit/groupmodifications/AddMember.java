package vg.civcraft.mc.namelayer.mc.rabbit.groupmodifications;

import java.util.UUID;

import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;

public class AddMember extends AbstractGroupModificationHandler {

	@Override
	protected void handle(Group group, JSONObject data) {
		UUID player = UUID.fromString(data.getString("member"));
		int rankID = data.getInt("rank_id");
		GroupRank rank = group.getGroupRankHandler().getRank(rankID);
		getGroupTracker().addPlayerToGroup(group, player, rank);
	}

	@Override
	public String getIdentifier() {
		return "nl_add_member";
	}

}
