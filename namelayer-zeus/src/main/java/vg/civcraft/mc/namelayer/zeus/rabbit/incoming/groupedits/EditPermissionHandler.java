package vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.servers.ConnectedServer;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.NameLayerPermissions;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.core.log.impl.AddPermission;
import vg.civcraft.mc.namelayer.core.log.impl.RemovePermission;
import vg.civcraft.mc.namelayer.core.requests.EditPermission;

public class EditPermissionHandler extends GroupRequestHandler {

	@Override
	public void handle(String ticket, ConnectedServer sendingServer, JSONObject data, UUID executor, Group group) {
		if (group == null) {
			sendReject(ticket, EditPermission.REPLY_ID, sendingServer, EditPermission.FailureReason.GROUP_DOES_NOT_EXIST);
			return;
		}
		synchronized (group) {
			PermissionType permNeeded = getGroupTracker().getPermissionTracker().getPermission(NameLayerPermissions.MODIFY_PERMS);
			if (!getGroupTracker().hasAccess(group, executor, permNeeded)) {
				Map<String, Object> repValues = new HashMap<>();
				repValues.put("missing_perm", permNeeded);
				sendReject(ticket, EditPermission.REPLY_ID, sendingServer, EditPermission.FailureReason.NO_PERMISSION, repValues);
				return;
			}
			GroupRank rank = group.getGroupRankHandler().getRank(data.getInt("target_rank_id"));
			if (rank == null) {
				sendReject(ticket, EditPermission.REPLY_ID, sendingServer, EditPermission.FailureReason.RANK_DOES_NOT_EXIST);
				return;
			}
			String permName = data.getString("permissionName");
			PermissionType permission = getGroupTracker().getPermissionTracker().getPermission(permName);
			if (permission == null) {
				sendReject(ticket, EditPermission.REPLY_ID, sendingServer, EditPermission.FailureReason.PERMISSION_DOES_NOT_EXIST);
				return;
			}
			boolean adding = data.getBoolean("adding");
			if (adding) {
				if (rank.hasPermission(permission)) {
					sendReject(ticket, EditPermission.REPLY_ID, sendingServer, EditPermission.FailureReason.RANK_ALREADY_HAS_PERMISSION);
					return;
				}
				getGroupTracker().addLogEntry(group, new AddPermission(System.currentTimeMillis(), executor, rank.getName(), permission.getName()));
				getGroupTracker().addPermissionToRank(group, rank, permission);
			} else {
				if (!rank.hasPermission(permission)) {
					sendReject(ticket, EditPermission.REPLY_ID, sendingServer, EditPermission.FailureReason.RANK_LACKS_PERMISSION);
					return;
				}
				getGroupTracker().addLogEntry(group, new RemovePermission(System.currentTimeMillis(), executor, rank.getName(), permission.getName()));
				getGroupTracker().removePermissionFromRank(group, rank, permission);
			};
			sendAccept(ticket, EditPermission.REPLY_ID, sendingServer);
		}
	}

	@Override
	public String getIdentifier() {
		return EditPermission.REQUEST_ID;
	}
}
