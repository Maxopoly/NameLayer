package vg.civcraft.mc.namelayer.mc.rabbit.groupmodifications;

import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupLink;
import vg.civcraft.mc.namelayer.core.IllegalGroupStateException;
import vg.civcraft.mc.namelayer.core.requests.GroupModifications;

public class UnlinkGroups extends AbstractGroupModificationHandler {

	@Override
	protected void handle(Group group, JSONObject data) {
		int sourceRankID = data.getInt("source_rank_id");
		int targetRankID = data.getInt("target_rank_id");
		int targetGroupID = data.getInt("target_group_id");
		Group targetGroup = getGroupTracker().getGroup(targetGroupID);
		if (targetGroup == null) {
			throw new IllegalGroupStateException();
		}
		for(GroupLink link : group.getOutgoingLinks()) {
			if (link.getOriginatingRank().getId() != sourceRankID) {
				continue;
			}
			if (link.getTargetRank().getId() != targetRankID) {
				continue;
			}
			if (link.getTargetGroup() != targetGroup) {
				continue;
			}
 			getGroupTracker().deleteGroupLink(link);
			return;
		}
		throw new IllegalGroupStateException();
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.REMOVE_LINK_ID;
	}

}
