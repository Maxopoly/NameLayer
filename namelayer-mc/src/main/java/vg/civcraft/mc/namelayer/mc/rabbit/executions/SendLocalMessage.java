package vg.civcraft.mc.namelayer.mc.rabbit.executions;

import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.model.ZeusLocation;
import com.github.maxopoly.zeus.rabbit.incoming.StaticRabbitCommand;
import com.github.maxopoly.zeus.servers.ConnectedServer;

import vg.civcraft.mc.namelayer.core.requests.GroupModifications;
import vg.civcraft.mc.namelayer.mc.model.chat.LocalChatMode;

public class SendLocalMessage extends StaticRabbitCommand {

	@Override
	public void handleRequest(ConnectedServer sendingServer, JSONObject data) {
		UUID sender = UUID.fromString(data.getString("player"));
		String message = data.getString("message");
		ZeusLocation loc = ZeusLocation.parseLocation(data.getJSONObject("loc"));
		LocalChatMode.broadcastLocalMessage(sender, loc, message);
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.SEND_LOCAL_MESSAGE;
	}

}
