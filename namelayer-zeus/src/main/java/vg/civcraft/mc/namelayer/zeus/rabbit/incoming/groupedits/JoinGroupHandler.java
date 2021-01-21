package vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.servers.ArtemisServer;
import com.github.maxopoly.zeus.servers.ConnectedServer;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.NameLayerMetaData;
import vg.civcraft.mc.namelayer.core.requests.JoinGroup;
import vg.civcraft.mc.namelayer.zeus.NameLayerZPlugin;

public class JoinGroupHandler extends GroupRequestHandler {

	@Override
	public void handle(String ticket, ConnectedServer sendingServer, JSONObject data, UUID executor, Group group) {
		if (group == null) {
			sendReject(ticket, JoinGroup.REPLY_ID, sendingServer, JoinGroup.FailureReason.GROUP_DOES_NOT_EXIST);
			return;
		}
		synchronized (group) {
			String password = group.getMetaData(NameLayerMetaData.PASSWORD_KEY);
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
			NameLayerZPlugin.getInstance().getGroupKnowledgeTracker().ensureIsCached(group, (ArtemisServer) sendingServer);
			getGroupTracker().addPlayerToGroup(group, executor, targetType);
			getGroupTracker().addLogEntry(group, new vg.civcraft.mc.namelayer.core.log.impl.JoinGroup(System.currentTimeMillis(), executor, targetType.getName()));
			sendAccept(ticket, JoinGroup.REPLY_ID, sendingServer, repValues);
		}
	}

	@Override
	public String getIdentifier() {
		return JoinGroup.REQUEST_ID;
	}
	
}
