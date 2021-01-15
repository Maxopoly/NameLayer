package vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits;

import com.github.maxopoly.zeus.servers.ConnectedServer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.json.JSONObject;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.NameLayerPermissions;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.core.requests.ChangeGroupColour;

public class ChangeGroupColourHandler extends GroupRequestHandler{
	@Override
	public void handle(String ticket, ConnectedServer sendingServer,
					   JSONObject data, UUID executor, Group group) {
		if (group == null) {
			sendReject(ticket, ChangeGroupColour.REPLY_ID, sendingServer, ChangeGroupColour.FailureReason.GROUP_DOES_NOT_EXIST);
			return;
		}
		synchronized (group) {
			PermissionType requiredPermission = getGroupTracker().getPermissionTracker().getPermission(
					NameLayerPermissions.EDIT_GROUP_COLOR);
			if (!getGroupTracker().hasAccess(group, executor, requiredPermission)) {
				Map<String, Object> repValues = new HashMap<>();
				repValues.put("missing_perm", requiredPermission);
				sendReject(ticket, ChangeGroupColour.REPLY_ID, sendingServer, ChangeGroupColour.FailureReason.NO_PERMISSION, repValues);
				return;
			}
			String colour = data.getString("colour");
			if (colour == null) {
				sendReject(ticket, ChangeGroupColour.REPLY_ID, sendingServer, ChangeGroupColour.FailureReason.COLOUR_NOT_VALID);
			}
			getGroupTracker().setMetaDataValue(group, "color", colour);
			sendAccept(ticket, ChangeGroupColour.REPLY_ID, sendingServer);
		}
	}

	@Override
	public String getIdentifier() {
		return ChangeGroupColour.REQUEST_ID;
	}
}
