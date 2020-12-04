package vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.github.civcraft.zeus.ZeusMain;
import com.github.civcraft.zeus.servers.ConnectedServer;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.GroupRankHandler;
import vg.civcraft.mc.namelayer.core.NameLayerPermissions;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.core.requests.InvitePlayer;

public class InvitePlayerHandler extends GroupRequestHandler {

	@Override
	public void handle(String ticket, ConnectedServer sendingServer, JSONObject data, UUID executor, Group group) {
		if (group == null) {
			sendReject(ticket, InvitePlayer.REPLY_ID, sendingServer, InvitePlayer.FailureReason.GROUP_DOES_NOT_EXIST);
			return;
		}
		String playerName = data.getString("target_player");
		UUID targetUUID = ZeusMain.getInstance().getPlayerManager().getOfflinePlayerUUID(playerName);
		if (targetUUID == null) {
			sendReject(ticket, InvitePlayer.REPLY_ID, sendingServer, InvitePlayer.FailureReason.PLAYER_DOES_NOT_EXIST);
			return;
		}
		synchronized (group) {
			int rankID = data.getInt("rank");
			GroupRankHandler handler = group.getGroupRankHandler();
			GroupRank targetType = handler.getRank(rankID);
			if (targetType == null) {
				sendReject(ticket, InvitePlayer.REPLY_ID, sendingServer, InvitePlayer.FailureReason.RANK_DOES_NOT_EXIST);
				return;
			}
			PermissionType permRequired = getGroupTracker().getPermissionTracker().getInvitePermission(targetType.getId());
			if (!getGroupTracker().hasAccess(group, executor, permRequired)) {
				Map<String, Object> repValues = new HashMap<>();
				repValues.put("missing_perm", permRequired);
				sendReject(ticket, InvitePlayer.REPLY_ID, sendingServer, InvitePlayer.FailureReason.NO_PERMISSION, repValues);
				return;
			}
			if (handler.isBlacklistedRank(targetType)) {
				sendReject(ticket, InvitePlayer.REPLY_ID, sendingServer, InvitePlayer.FailureReason.BLACKLISTED_RANK);
				return;
			}
			if (group.isTracked(targetUUID)) {
				sendReject(ticket, InvitePlayer.REPLY_ID, sendingServer, InvitePlayer.FailureReason.ALREADY_INVITED);
				return;
			}
			getGroupTracker().addInvite(targetUUID, targetType, group);
			sendAccept(ticket, InvitePlayer.REPLY_ID, sendingServer);
		}
		
	}

	@Override
	public String getIdentifier() {
		return InvitePlayer.REQUEST_ID;
	}

}
