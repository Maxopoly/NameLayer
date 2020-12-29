package vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.github.civcraft.zeus.servers.ConnectedServer;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.NameLayerPermissions;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.core.requests.MergeGroups;

public class MergeGroupHandler extends GroupRequestHandler {

	@Override
	public void handle(String ticket, ConnectedServer sendingServer, JSONObject data, UUID executor, Group group) {
		if (group == null) {
			sendReject(ticket, MergeGroups.REPLY_ID, sendingServer, MergeGroups.FailureReason.ORIGINAL_GROUP_DOES_NOT_EXIST);
			return;
		}
		Group targetGroup = getGroupTracker().getGroup(data.getString("groupToDelete"));
		if (targetGroup == null) {
			sendReject(ticket, MergeGroups.REPLY_ID, sendingServer, MergeGroups.FailureReason.TARGET_GROUP_DOES_NOT_EXIST);
			return;
		}
		synchronized (group) {
			if (group.equals(targetGroup)) {
				sendReject(ticket, MergeGroups.REPLY_ID, sendingServer, MergeGroups.FailureReason.CANNOT_MERGE_INTO_SELF);
				return;
			}
			PermissionType permNeeded = getGroupTracker().getPermissionTracker().getPermission(NameLayerPermissions.MERGE_GROUP);
			Map<String, Object> repValues = new HashMap<>();
			repValues.put("missing_perm", permNeeded);
			if (!getGroupTracker().hasAccess(group, executor, permNeeded)) {
				sendReject(ticket, MergeGroups.REPLY_ID, sendingServer, MergeGroups.FailureReason.NO_PERMISSION_ORIG_GROUP, repValues);
				return;
			}
			if (!getGroupTracker().hasAccess(targetGroup, executor, permNeeded)) {
				sendReject(ticket, MergeGroups.REPLY_ID, sendingServer, MergeGroups.FailureReason.NO_PERMISSION_TARGET_GROUP, repValues);
				return;
			}
			if (!targetGroup.getIncomingLinks().isEmpty() || !targetGroup.getOutgoingLinks().isEmpty()) {
				sendReject(ticket, MergeGroups.REPLY_ID, sendingServer, MergeGroups.FailureReason.HAS_ACTIVE_LINKS);
				return;
			}
			getGroupTracker().mergeGroups(group, targetGroup);
			sendAccept(ticket, MergeGroups.REPLY_ID, sendingServer);
		}
	}

	@Override
	public String getIdentifier() {
		return MergeGroups.REQUEST_ID;
	}
}
