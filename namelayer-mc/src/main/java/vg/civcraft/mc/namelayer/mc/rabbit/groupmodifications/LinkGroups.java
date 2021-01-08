package vg.civcraft.mc.namelayer.mc.rabbit.groupmodifications;

import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.IllegalGroupStateException;
import vg.civcraft.mc.namelayer.core.requests.GroupModifications;

public class LinkGroups extends AbstractGroupModificationHandler {

	@Override
	protected void handle(Group group, JSONObject data) {
		int targetGroupID = data.getInt("target_group_id");
		Group targetGroup = getGroupTracker().getGroup(targetGroupID);
		
		int sourceRankID = data.getInt("source_rank_id");
		GroupRank originalRank = group.getGroupRankHandler().getRank(sourceRankID);
		
		int targetRankID = data.getInt("target_rank_id");
		GroupRank targetRank = group.getGroupRankHandler().getRank(targetRankID);
		if (originalRank == null || targetRank == null) {
			throw new IllegalGroupStateException();
		}
		getGroupTracker().linkGroups(group, originalRank, targetGroup, targetRank);
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.ADD_LINK_ID;
	}

}
