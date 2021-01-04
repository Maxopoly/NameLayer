package vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.servers.ConnectedServer;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.GroupRankHandler;
import vg.civcraft.mc.namelayer.core.NameLayerPermissions;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.core.requests.CreateRank;

public class CreateRankHandler extends GroupRequestHandler {

	@Override
	public void handle(String ticket, ConnectedServer sendingServer, JSONObject data, UUID executor, Group group) {
		if (group == null) {
			sendReject(ticket, CreateRank.REPLY_ID, sendingServer, CreateRank.FailureReason.GROUP_DOES_NOT_EXIST);
			return;
		}
		PermissionType perm = getGroupTracker().getPermissionTracker().getPermission(NameLayerPermissions.CREATE_RANK);
		if (!getGroupTracker().hasAccess(group, executor, perm)) {
			Map<String, Object> repValues = new HashMap<>();
			repValues.put("missing_perm", perm);
			sendReject(ticket, CreateRank.REPLY_ID, sendingServer, CreateRank.FailureReason.NO_PERMISSION, repValues);
			return;
		}
		String rankName = data.getString("newRankName");
		if (!CreateGroupHandler.isConformName(rankName)) {
			sendReject(ticket, CreateRank.REPLY_ID, sendingServer, CreateRank.FailureReason.INVALID_RANK_NAME);
			return;
		}
		if (group.getGroupRankHandler().doesTypeExist(rankName)) {
			sendReject(ticket, CreateRank.REPLY_ID, sendingServer, CreateRank.FailureReason.RANK_ALREADY_EXISTS);
			return;
		}
		synchronized (group) {
			GroupRankHandler rankHandler = group.getGroupRankHandler();
			if (rankHandler.getAllRanks().size() >= GroupRankHandler.MAXIMUM_TYPE_COUNT) {
				Map<String, Object> repValues = new HashMap<>();
				repValues.put("max_ranks", GroupRankHandler.MAXIMUM_TYPE_COUNT);
				sendReject(ticket, CreateRank.REPLY_ID, sendingServer, CreateRank.FailureReason.RANK_LIMIT_REACHED, repValues);
				return;
			}
			int parentRankId = data.getInt("parentRank");
			GroupRank parentRank = rankHandler.getRank(parentRankId);
			if (parentRank == null) {
				sendReject(ticket, CreateRank.REPLY_ID, sendingServer, CreateRank.FailureReason.PARENT_RANK_DOES_NOT_EXIST);
				return;
			}
			getGroupTracker().createRank(group, rankName, parentRank);
			sendAccept(ticket, CreateRank.REPLY_ID, sendingServer);
		}		
	}

	@Override
	public String getIdentifier() {
		return CreateRank.REQUEST_ID;
	}



}
