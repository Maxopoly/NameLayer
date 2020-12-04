package vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.github.civcraft.zeus.servers.ConnectedServer;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.requests.JoinGroup;

public class JoinGroupHandler extends GroupRequestHandler {

	@Override
	public void handle(String ticket, ConnectedServer sendingServer, JSONObject data, UUID executor, Group group) {
		if (group == null) {
			sendReject(ticket, JoinGroup.REPLY_ID, sendingServer, JoinGroup.FailureReason.GROUP_DOES_NOT_EXIST);
			return;
		}
		synchronized (group) {
			String password = group.getMetaData("password");
			if (password == null) {
				sendReject(ticket, JoinGroup.REPLY_ID, sendingServer, JoinGroup.FailureReason.GROUP_HAS_NO_PASSWORD);
				return;
			}
			if (!password.equals(data.getString("submittedPassword"))) {
				sendReject(ticket, JoinGroup.REPLY_ID, sendingServer, JoinGroup.FailureReason.WRONG_PASSWORD);
				return;
			}
			if (group.isTracked(executor)) {
				sendReject(ticket, JoinGroup.REPLY_ID, sendingServer, JoinGroup.FailureReason.ALREADY_MEMBER_OR_BLACKLISTED);
				return;
			}
			GroupRank targetType = group.getGroupRankHandler().getDefaultPasswordJoinRank();
			Map<String, Object> repValues = new HashMap<>();
			repValues.put("targetRank", targetType.getId());
			getGroupTracker().addPlayerToGroup(group, executor, targetType);
			sendAccept(ticket, JoinGroup.REPLY_ID, sendingServer, repValues);
		}
	}

	@Override
	public String getIdentifier() {
		return JoinGroup.REQUEST_ID;
	}
	
}
