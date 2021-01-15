package vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.servers.ConnectedServer;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.NameLayerPermissions;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.core.requests.DeleteGroup;

public class DeleteGroupHandler extends GroupRequestHandler {

	@Override
	public void handle(String ticket, ConnectedServer sendingServer, JSONObject data, UUID executor, Group group) {
		if (group == null) {
			sendReject(ticket, DeleteGroup.REPLY_ID, sendingServer, DeleteGroup.FailureReason.GROUP_DOES_NOT_EXIST);
			return;
		}
		synchronized (group) {
			PermissionType perm = getGroupTracker().getPermissionTracker()
					.getPermission(NameLayerPermissions.DELETE_GROUP);
			if (!getGroupTracker().hasAccess(group, executor, perm)) {
				Map<String, Object> repValues = new HashMap<>();
				repValues.put("missing_perm", perm);
				sendReject(ticket, DeleteGroup.REPLY_ID, sendingServer, DeleteGroup.FailureReason.NO_PERMISSION,
						repValues);
				return;
			}
			getGroupTracker().deleteGroup(group);
			sendAccept(ticket, DeleteGroup.REPLY_ID, sendingServer);
		}
	}

	@Override
	public String getIdentifier() {
		return DeleteGroup.REQUEST_ID;
	}

}
