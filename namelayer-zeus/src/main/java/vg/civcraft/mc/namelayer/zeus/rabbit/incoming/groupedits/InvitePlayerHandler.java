package vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits;

import java.util.UUID;

import org.json.JSONObject;

import com.github.civcraft.zeus.ZeusMain;
import com.github.civcraft.zeus.servers.ConnectedServer;

import vg.civcraft.mc.namelayer.core.Group;
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
			
		}
		
	}

	@Override
	public String getIdentifier() {
		return InvitePlayer.REQUEST_ID;
	}

}
