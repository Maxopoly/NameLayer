package vg.civcraft.mc.namelayer.mc.rabbit.executions;

import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.IllegalGroupStateException;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.core.requests.GroupModifications;

public class AddPermission extends AbstractGroupModificationHandler {

	@Override
	protected void handle(Group group, JSONObject data) {
		int rankID = data.getInt("rank_id");
		GroupRank rank = group.getGroupRankHandler().getRank(rankID);
		String permID = data.getString("perm_id");
		PermissionType permission = getGroupTracker().getPermissionTracker().getPermission(permID);
		if (rank == null) {
			throw new IllegalGroupStateException();
		}
		getGroupTracker().addPermissionToRank(group, rank, permission);
		
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.ADD_PERMISSION_ID;
	}

}
