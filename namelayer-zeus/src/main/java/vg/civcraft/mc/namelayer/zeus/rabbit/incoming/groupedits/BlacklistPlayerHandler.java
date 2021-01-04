package vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.ZeusMain;
import com.github.maxopoly.zeus.servers.ConnectedServer;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.core.requests.BlacklistPlayer;

public class BlacklistPlayerHandler extends GroupRequestHandler {

	@Override
	public void handle(String ticket, ConnectedServer sendingServer, JSONObject data, UUID executor, Group group) {
		if (group == null) {
			sendReject(ticket, BlacklistPlayer.REPLY_ID, sendingServer, BlacklistPlayer.FailureReason.GROUP_DOES_NOT_EXIST);
			return;
		}
		String targetPlayerName = data.getString("target_player");
		UUID targetPlayer = ZeusMain.getInstance().getPlayerManager().getOfflinePlayerUUID(targetPlayerName);
		if (targetPlayer == null) {
			sendReject(ticket, BlacklistPlayer.REPLY_ID, sendingServer, BlacklistPlayer.FailureReason.PLAYER_DOES_NOT_EXIST);
			return;
		}
		synchronized (group) {
			int rankID = data.getInt("rank_id");
			GroupRank rank = group.getGroupRankHandler().getRank(rankID);
			if (rank == null) {
				sendReject(ticket, BlacklistPlayer.REPLY_ID, sendingServer, BlacklistPlayer.FailureReason.RANK_DOES_NOT_EXIST);
				return;
			}
			PermissionType requiredPermission = getGroupTracker().getPermissionTracker().getInvitePermission(rank.getId());
			if (!getGroupTracker().hasAccess(group, executor, requiredPermission)) {
				Map<String, Object> repValues = new HashMap<>();
				repValues.put("missing_perm", requiredPermission);
				sendReject(ticket, BlacklistPlayer.REPLY_ID, sendingServer, BlacklistPlayer.FailureReason.NO_PERMISSION, repValues);
				return;
			}
			if (!group.getGroupRankHandler().isBlacklistedRank(rank)) {
				sendReject(ticket, BlacklistPlayer.REPLY_ID, sendingServer, BlacklistPlayer.FailureReason.NOT_BLACKLISTED_RANK);
				return;
			}
			getGroupTracker().addPlayerToGroup(group, targetPlayer, rank);
			sendAccept(ticket, BlacklistPlayer.REPLY_ID, sendingServer);
		}
	}

	@Override
	public String getIdentifier() {
		return BlacklistPlayer.REQUEST_ID;
	}

}
