package vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits;

import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.servers.ConnectedServer;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.NameLayerPermissions;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.core.requests.InvitePlayer;
import vg.civcraft.mc.namelayer.core.requests.SendGroupChatMessage;
import vg.civcraft.mc.namelayer.zeus.NameLayerZPlugin;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.GroupChatMessageMessage;

public class SendGroupChatMessageHandler extends GroupRequestHandler {

	@Override
	public String getIdentifier() {
		return vg.civcraft.mc.namelayer.core.requests.SendGroupChatMessage.REQUEST_ID;
	}

	@Override
	public void handle(String ticket, ConnectedServer sendingServer, JSONObject data, UUID executor, Group group) {
		if (group == null) {
			sendReject(ticket, SendGroupChatMessage.REPLY_ID, sendingServer,
					SendGroupChatMessage.FailureReason.GROUP_DOES_NOT_EXIST);
			return;
		}
		String message = data.getString("message");
		PermissionType permRequired = getGroupTracker().getPermissionTracker()
				.getPermission(NameLayerPermissions.WRITE_CHAT);
		if (!getGroupTracker().hasAccess(group, executor, permRequired)) {
			sendReject(ticket, SendGroupChatMessage.REPLY_ID, sendingServer,
					SendGroupChatMessage.FailureReason.NO_PERMISSION);
			return;
		}
		NameLayerZPlugin.getInstance().getGroupKnowledgeTracker().sendToInterestedServers(group,
				() -> new GroupChatMessageMessage(ticket, group, executor, message));
		sendAccept(ticket, SendGroupChatMessage.REPLY_ID, sendingServer);
	}

}
