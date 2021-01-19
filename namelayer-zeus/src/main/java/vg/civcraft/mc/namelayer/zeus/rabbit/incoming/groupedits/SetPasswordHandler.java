package vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.servers.ConnectedServer;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.NameLayerMetaData;
import vg.civcraft.mc.namelayer.core.NameLayerPermissions;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.core.requests.SetPassword;

public class SetPasswordHandler extends GroupRequestHandler {

	@Override
	public void handle(String ticket, ConnectedServer sendingServer, JSONObject data, UUID executor, Group group) {
		if (group == null) {
			sendReject(ticket, SetPassword.REPLY_ID, sendingServer, SetPassword.FailureReason.GROUP_DOES_NOT_EXIST);
			return;
		}
		synchronized (group) {
			PermissionType permNeeded = getGroupTracker().getPermissionTracker().getPermission(NameLayerPermissions.PASSWORD);
			if (!getGroupTracker().hasAccess(group, executor, permNeeded)) {
				Map<String, Object> repValues = new HashMap<>();
				repValues.put("missing_perm", permNeeded);
				sendReject(ticket, SetPassword.REPLY_ID, sendingServer, SetPassword.FailureReason.NO_PERMISSION, repValues);
				return;
			}
			String password = data.getString("password");
			if (password == null) {
				sendReject(ticket, SetPassword.REPLY_ID, sendingServer, SetPassword.FailureReason.NULL_PASSWORD);
				return;
			}
			String oldPassword = group.getMetaData(NameLayerMetaData.PASSWORD_KEY);
			getGroupTracker().setMetaDataValue(group, NameLayerMetaData.PASSWORD_KEY, password);
			getGroupTracker().addLogEntry(group, new vg.civcraft.mc.namelayer.core.log.impl.SetPassword(System.currentTimeMillis(), executor, oldPassword, password));
			sendAccept(ticket, SetPassword.REPLY_ID, sendingServer);
		}
		
	}

	@Override
	public String getIdentifier() {
		return SetPassword.REQUEST_ID;
	}
	

}
