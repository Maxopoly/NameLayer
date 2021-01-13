package vg.civcraft.mc.namelayer.mc.rabbit.executions;

import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.rabbit.incoming.StaticRabbitCommand;
import com.github.maxopoly.zeus.servers.ConnectedServer;

import vg.civcraft.mc.namelayer.core.requests.GroupModifications;
import vg.civcraft.mc.namelayer.mc.model.chat.PrivateChatMode;

public class SendPrivateMessage extends StaticRabbitCommand {

	@Override
	public void handleRequest(ConnectedServer sendingServer, JSONObject data) {
		UUID sender = UUID.fromString(data.getString("player"));
		String message = data.getString("message");
		UUID receiver = UUID.fromString(data.getString("receiver"));
		PrivateChatMode.showPMToReceiver(sender, receiver, message);
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.SEND_PRIVATE_MESSAGE;
	}

}
