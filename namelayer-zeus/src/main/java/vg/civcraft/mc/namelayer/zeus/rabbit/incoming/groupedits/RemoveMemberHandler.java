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
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.core.requests.RemoveMember;

public class RemoveMemberHandler extends GroupRequestHandler {

	@Override
	public void handle(String ticket, ConnectedServer sendingServer, JSONObject data, UUID executor, Group group) {
		if (group == null) {
			sendReject(ticket, RemoveMember.REPLY_ID, sendingServer, RemoveMember.FailureReason.GROUP_DOES_NOT_EXIST);
			return;
		}
		String targetPlayerName = data.getString("playerName");
		UUID targetPlayer = ZeusMain.getInstance().getPlayerManager().getUUID(targetPlayerName);
		if (targetPlayer == null) {
			sendReject(ticket, RemoveMember.REPLY_ID, sendingServer, RemoveMember.FailureReason.PLAYER_DOES_NOT_EXIST);
			return;
		}
		synchronized (group) {
			if (targetPlayer.equals(executor)) {
			sendReject(ticket, RemoveMember.REPLY_ID, sendingServer, RemoveMember.FailureReason.CANNOT_KICK_SELF);
			return;
			}
			GroupRankHandler handler = group.getGroupRankHandler();
			GroupRank currentRank = group.getRank(targetPlayer);
			if (currentRank == handler.getDefaultNonMemberRank()) {
				sendReject(ticket, RemoveMember.REPLY_ID, sendingServer, RemoveMember.FailureReason.NOT_A_MEMBER);
				return;
			}
			PermissionType permRequired = getGroupTracker().getPermissionTracker().getRemovePermission(currentRank.getId());
			if (!getGroupTracker().hasAccess(group, executor, permRequired)) {
				Map<String, Object> repValues = new HashMap<>();
				repValues.put("missing_perm", permRequired);
				sendReject(ticket, RemoveMember.REPLY_ID, sendingServer, RemoveMember.FailureReason.NO_PERMISSION, repValues);
				return;
			}
			getGroupTracker().removePlayerFromGroup(group, targetPlayer);
			Map<String, Object> repValues = new HashMap<>();
			repValues.put("current_rank_id", currentRank.getId());
			sendAccept(ticket, RemoveMember.REPLY_ID, sendingServer, repValues);
		}
	}

	@Override
	public String getIdentifier() {
		return RemoveMember.REQUEST_ID;
	}

}
