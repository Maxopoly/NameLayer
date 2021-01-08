package vg.civcraft.mc.namelayer.mc.rabbit.groupmodifications;

import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.IllegalGroupStateException;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.core.requests.GroupModifications;

public class RemovePermission extends AbstractGroupModificationHandler {

	@Override
	protected void handle(Group group, JSONObject data) {
		int rankID = data.getInt("rank_id");
		GroupRank rank = group.getGroupRankHandler().getRank(rankID);
		if (rank == null) {
			throw new IllegalGroupStateException();
		}
		int permID = data.getInt("perm_id");
		PermissionType permission = getGroupTracker().getPermissionTracker().getPermission(permID);
		getGroupTracker().removePermissionFromRank(group, rank, permission);
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.REMOVE_PERMISSION_ID;
	}
}
