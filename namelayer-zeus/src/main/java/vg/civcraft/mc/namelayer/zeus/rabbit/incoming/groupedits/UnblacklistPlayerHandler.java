package vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits;

import com.github.maxopoly.zeus.ZeusMain;
import com.github.maxopoly.zeus.servers.ConnectedServer;
import java.util.UUID;
import org.json.JSONObject;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.core.requests.UnblacklistPlayer;

public class UnblacklistPlayerHandler extends GroupRequestHandler {
	@Override
	public void handle(String ticket, ConnectedServer sendingServer,
					   JSONObject data, UUID executor, Group group) {
		if (group == null) {
			sendReject(ticket, UnblacklistPlayer.REPLY_ID, sendingServer, UnblacklistPlayer.FailureReason.GROUP_DOES_NOT_EXIST);
			return;
		}
		String playerName = data.getString("targetPlayer");
		UUID targetPlayer = ZeusMain.getInstance().getPlayerManager().getUUID(playerName);
		if (targetPlayer == null) {
			sendReject(ticket, UnblacklistPlayer.REPLY_ID, sendingServer, UnblacklistPlayer.FailureReason.PLAYER_DOES_NOT_EXIST);
			return;
		}
		synchronized (group) {
			GroupRank blacklistedRank = group.getRank(targetPlayer);
			if (group.getGroupRankHandler().isBlacklistedRank(blacklistedRank)) {
				sendReject(ticket, UnblacklistPlayer.REPLY_ID, sendingServer, UnblacklistPlayer.FailureReason.PLAYER_NOT_BLACKLISTED);
				return;
			}
			PermissionType permNeeded = getGroupTracker().getPermissionTracker().getRemovePermission(blacklistedRank.getId());
			if (!getGroupTracker().hasAccess(group, executor, permNeeded)) {
				sendReject(ticket, UnblacklistPlayer.REPLY_ID, sendingServer, UnblacklistPlayer.FailureReason.NO_PERMISSION);
				return;
			}
			getGroupTracker().unBlacklistPlayer(group, targetPlayer);
			sendAccept(ticket, UnblacklistPlayer.REPLY_ID, sendingServer);

		}
	}

	@Override
	public String getIdentifier() {
		return UnblacklistPlayer.REQUEST_ID;
	}
}
