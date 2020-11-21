package vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits;

import java.util.UUID;

import org.json.JSONObject;

import com.github.civcraft.zeus.servers.ConnectedServer;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.requests.EditPermission;

public class EditPermissionHandler extends GroupRequestHandler {

	@Override
	public void handle(String ticket, ConnectedServer sendingServer, JSONObject data, UUID executor, Group group) {
		if (group == null) {
			sendReject(ticket, EditPermission.REPLY_ID, sendingServer, EditPermission.FailureReason.GROUP_DOES_NOT_EXIST);
			return;
		}
		
	}

	@Override
	public String getIdentifier() {
		return EditPermission.REQUEST_ID;
	}
}
