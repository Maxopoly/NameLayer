package vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.servers.ConnectedServer;

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
			getGroupTracker().addLogEntry(group,
					new vg.civcraft.mc.namelayer.core.log.impl.RejectInvite(System.currentTimeMillis(), executor,
							rankInvitedTo.getName()));
			Map<String, Object> repValues = new HashMap<>();
			repValues.put("rank_invited_to", rankInvitedTo.getName());
			sendAccept(ticket, RejectInvite.REPLY_ID, sendingServer, repValues);
		}
	}

	@Override
	public String getIdentifier() {
		return RejectInvite.REQUEST_ID;
	}

}
