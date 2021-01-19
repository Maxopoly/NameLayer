package vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.ZeusMain;
import com.github.maxopoly.zeus.servers.ConnectedServer;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.GroupRankHandler;
import vg.civcraft.mc.namelayer.core.NameLayerPermissions;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.core.log.impl.ChangeMemberRank;
import vg.civcraft.mc.namelayer.core.log.impl.ChangeRankName;
import vg.civcraft.mc.namelayer.core.requests.PromotePlayer;

public class PromotePlayerHandler extends GroupRequestHandler {

	@Override
	public void handle(String ticket, ConnectedServer sendingServer, JSONObject data, UUID executor, Group group) {
		if (group == null) {
			sendReject(ticket, PromotePlayer.REPLY_ID, sendingServer, PromotePlayer.FailureReason.GROUP_DOES_NOT_EXIST);
			return;
		}
		String targetPlayerName = data.getString("playerName");
		UUID targetPlayer = ZeusMain.getInstance().getPlayerManager().getUUID(targetPlayerName);
		if (targetPlayer == null) {
			sendReject(ticket, PromotePlayer.REPLY_ID, sendingServer,
					PromotePlayer.FailureReason.PLAYER_DOES_NOT_EXIST);
			return;
		}
		synchronized (group) {
			if (targetPlayer.equals(executor)) {
				sendReject(ticket, PromotePlayer.REPLY_ID, sendingServer,
						PromotePlayer.FailureReason.CANNOT_CHANGE_YOURSELF);
				return;
			}
			GroupRankHandler handler = group.getGroupRankHandler();
			GroupRank targetType = handler.getRank(data.getInt("targetRank"));
			if (targetType == null) {
				sendReject(ticket, PromotePlayer.REPLY_ID, sendingServer,
						PromotePlayer.FailureReason.RANK_DOES_NOT_EXIST);
				return;
			}
			PermissionType permNeeded =
					getGroupTracker().getPermissionTracker().getInvitePermission(targetType.getId());
			if (!getGroupTracker().hasAccess(group, executor, permNeeded)) {
				sendReject(ticket, PromotePlayer.REPLY_ID, sendingServer, PromotePlayer.FailureReason.NO_PERMISSION);
				return;
			}
			GroupRank currentRank = group.getRank(targetPlayer);
			if (currentRank == handler.getDefaultNonMemberRank()) {
				sendReject(ticket, PromotePlayer.REPLY_ID, sendingServer, PromotePlayer.FailureReason.NOT_A_MEMBER);
				return;
			}
			//TODO
			PermissionType removalPerm =
					getGroupTracker().getPermissionTracker().getRemovePermission(currentRank.getId());
			if (!getGroupTracker().hasAccess(group, executor, removalPerm)) {
				sendReject(ticket, PromotePlayer.REPLY_ID, sendingServer, PromotePlayer.FailureReason.NO_PERMISSION);
				return;
			}
			if (handler.isBlacklistedRank(currentRank) && !handler.isBlacklistedRank(targetType)) {
				sendReject(ticket, PromotePlayer.REPLY_ID, sendingServer, PromotePlayer.FailureReason.BLACKLISTED);
				return;
			}

			getGroupTracker().updatePlayerRankInGroup(group, targetPlayer, targetType);
			Map<String, Object> repValues = new HashMap<>();
			repValues.put("oldRankId", currentRank.getId());
			getGroupTracker().addLogEntry(group,
					new ChangeMemberRank(System.currentTimeMillis(), executor, targetType.getName(), targetPlayer,
							currentRank.getName()));
			sendAccept(ticket, PromotePlayer.REPLY_ID, sendingServer, repValues);
		}
	}

	@Override
	public String getIdentifier() {
		return PromotePlayer.REQUEST_ID;
	}

}
