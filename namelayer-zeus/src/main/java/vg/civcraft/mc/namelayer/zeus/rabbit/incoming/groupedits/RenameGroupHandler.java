package vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.servers.ConnectedServer;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.NameLayerPermissions;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.core.log.impl.ChangeGroupName;
import vg.civcraft.mc.namelayer.core.requests.RenameGroup;

public class RenameGroupHandler extends GroupRequestHandler {

	@Override
	public void handle(String ticket, ConnectedServer sendingServer, JSONObject data, UUID executor, Group group) {
		if (group == null) {
			sendReject(ticket, RenameGroup.REPLY_ID, sendingServer, RenameGroup.FailureReason.GROUP_DOES_NOT_EXIST);
			return;
		}
		synchronized (group) {
			PermissionType permNeeded = getGroupTracker().getPermissionTracker().getPermission(NameLayerPermissions.RENAME_GROUP);
			if (!getGroupTracker().hasAccess(group, executor, permNeeded)) {
				Map<String,Object> repValues = new HashMap<>();
				repValues.put("missing_perm", permNeeded.getName());
				sendReject(ticket, RenameGroup.REPLY_ID, sendingServer, RenameGroup.FailureReason.NO_PERMISSION, repValues);
				return;
			}
			String newName = data.getString("newName");
			if (newName.equals(group.getName())) {
				sendReject(ticket, RenameGroup.REPLY_ID, sendingServer, RenameGroup.FailureReason.SAME_NAME);
				return;
			}
			if (!newName.equalsIgnoreCase(group.getName()) && getGroupTracker().getGroup(newName) != null) {
				sendReject(ticket, RenameGroup.REPLY_ID, sendingServer, RenameGroup.FailureReason.NAME_ALREADY_TAKEN);
				return;
			}
			getGroupTracker().renameGroup(group, newName);
			getGroupTracker().addLogEntry(group, new ChangeGroupName(System.currentTimeMillis(), executor, group.getName(), newName));
			sendAccept(ticket, RenameGroup.REPLY_ID, sendingServer);
		}
	}

	@Override
	public String getIdentifier() {
		return RenameGroup.REQUEST_ID;
	}

}
