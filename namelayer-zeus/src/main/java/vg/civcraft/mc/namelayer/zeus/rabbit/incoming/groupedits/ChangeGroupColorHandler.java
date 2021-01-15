package vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits;

import com.github.maxopoly.zeus.servers.ConnectedServer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.json.JSONObject;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.NameLayerPermissions;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.core.requests.ChangeGroupColor;

public class ChangeGroupColorHandler extends GroupRequestHandler{
	@Override
	public void handle(String ticket, ConnectedServer sendingServer,
					   JSONObject data, UUID executor, Group group) {
		if (group == null) {
			sendReject(ticket, ChangeGroupColor.REPLY_ID, sendingServer, ChangeGroupColor.FailureReason.GROUP_DOES_NOT_EXIST);
			return;
		}
		synchronized (group) {
			PermissionType requiredPermission = getGroupTracker().getPermissionTracker().getPermission(
					NameLayerPermissions.EDIT_GROUP_COLOR);
			if (!getGroupTracker().hasAccess(group, executor, requiredPermission)) {
				Map<String, Object> repValues = new HashMap<>();
				repValues.put("missing_perm", requiredPermission);
				sendReject(ticket, ChangeGroupColor.REPLY_ID, sendingServer, ChangeGroupColor.FailureReason.NO_PERMISSION, repValues);
				return;
			}
			String colour = data.getString("colour");
			if (colour == null) {
				sendReject(ticket, ChangeGroupColor.REPLY_ID, sendingServer, ChangeGroupColor.FailureReason.COLOR_NOT_VALID);
			}
			getGroupTracker().setMetaDataValue(group, "color", colour);
			sendAccept(ticket, ChangeGroupColor.REPLY_ID, sendingServer);
		}
	}

	@Override
	public String getIdentifier() {
		return ChangeGroupColor.REQUEST_ID;
	}
}
