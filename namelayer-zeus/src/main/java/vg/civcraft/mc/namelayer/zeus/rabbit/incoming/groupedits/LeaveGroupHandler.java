package vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits;

import java.util.UUID;

import org.json.JSONObject;

import com.github.civcraft.zeus.servers.ConnectedServer;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.requests.LeaveGroup;

public class LeaveGroupHandler extends GroupRequestHandler {

	@Override
	public void handle(String ticket, ConnectedServer sendingServer, JSONObject data, UUID executor, Group group) {
		if (group == null) {
			sendReject(ticket, LeaveGroup.REPLY_ID, sendingServer, LeaveGroup.FailureReason.GROUP_DOES_NOT_EXIST);
			return;
		}
		synchronized (group) {
			GroupRank rank = group.getRank(executor);
			if (!group.getGroupRankHandler().isMemberRank(rank)) {
				sendReject(ticket, LeaveGroup.REPLY_ID, sendingServer, LeaveGroup.FailureReason.NOT_A_MEMBER);
				return;
			}
			if (rank.getParent() == null && group.getAllTrackedByType(rank).size() == 1) {
				sendReject(ticket, LeaveGroup.REPLY_ID, sendingServer, LeaveGroup.FailureReason.NO_OTHER_OWNER);
				return;
			}
			getGroupTracker().removePlayerFromGroup(group, executor);
			sendAccept(ticket, LeaveGroup.REPLY_ID, sendingServer);
		}
	}

	@Override
	public String getIdentifier() {
		return LeaveGroup.REQUEST_ID;
	}
	
}
