package vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits;

import java.util.UUID;

import org.json.JSONObject;

import com.github.civcraft.zeus.servers.ConnectedServer;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.requests.RejectInvite;

public class RejectInviteHandler extends GroupRequestHandler {

	@Override
	public void handle(String ticket, ConnectedServer sendingServer, JSONObject data, UUID executor, Group group) {
		if (group == null) {
			sendReject(ticket, RejectInvite.REPLY_ID, sendingServer, RejectInvite.FailureReason.GROUP_DOES_NOT_EXIST);
			return;
		}
		synchronized (group) {
			GroupRank rankInvitedTo = group.getInvite(executor);
			if (rankInvitedTo == null) {
				sendReject(ticket, RejectInvite.REPLY_ID, sendingServer, RejectInvite.FailureReason.NOT_INVITED);
				return;
			}
			getGroupTracker().deleteInvite(group, executor);
			sendAccept(ticket, RejectInvite.REPLY_ID, sendingServer);
		}
	}

	@Override
	public String getIdentifier() {
		return RejectInvite.REQUEST_ID;
	}

}
