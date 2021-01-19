package vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.servers.ConnectedServer;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.log.impl.AcceptInvitation;
import vg.civcraft.mc.namelayer.core.requests.AcceptInvite;

public class AcceptInviteHandler extends GroupRequestHandler {

	@Override
	public String getIdentifier() {
		return AcceptInvite.REQUEST_ID;
	}

	@Override
	public void handle(String ticket, ConnectedServer sendingServer, JSONObject data, UUID executor, Group group) {
		if (group == null) {
			sendReject(ticket, AcceptInvite.REPLY_ID, sendingServer, AcceptInvite.FailureReason.GROUP_DOES_NOT_EXIST);
			return;
		}
		synchronized (group) {
			if (group.isTracked(executor)) {
				sendReject(ticket, AcceptInvite.REPLY_ID, sendingServer, AcceptInvite.FailureReason.ALREADY_MEMBER);
				return;
			}
			GroupRank invitedRank = group.getInvite(executor);
			if (invitedRank == null) {
				sendReject(ticket, AcceptInvite.REPLY_ID, sendingServer, AcceptInvite.FailureReason.NO_EXISTING_INVITE);
				return;
			}
			getGroupTracker().acceptInvite(group, executor);
			Map<String, Object> repValues = new HashMap<>();
			repValues.put("rank_id", invitedRank.getId());
			getGroupTracker().addLogEntry(group, new AcceptInvitation(System.currentTimeMillis(), executor, invitedRank.getName()));
			sendAccept(ticket, AcceptInvite.REPLY_ID, sendingServer, repValues);
		}
	}

}
