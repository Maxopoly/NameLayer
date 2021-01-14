package vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.ZeusMain;
import com.github.maxopoly.zeus.model.ZeusLocation;
import com.github.maxopoly.zeus.rabbit.DynamicRabbitMessage;
import com.github.maxopoly.zeus.rabbit.incoming.GenericInteractiveRabbitCommand;
import com.github.maxopoly.zeus.servers.ArtemisServer;
import com.github.maxopoly.zeus.servers.ConnectedServer;

import vg.civcraft.mc.namelayer.core.requests.GroupModifications;
import vg.civcraft.mc.namelayer.core.requests.SendLocalMessage;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.LocalChatMessageMessage;

public class SendLocalChatMessageHandler extends GenericInteractiveRabbitCommand {

	@Override
	public void handleRequest(String ticket, ConnectedServer sendingServer, JSONObject data) {
		UUID sender = UUID.fromString(data.getString("sender"));
		double range = data.getDouble("range");
		ZeusLocation loc = ZeusLocation.parseLocation(data.getJSONObject("loc"));
		String message = data.getString("message");
		// TODO actually do range checks to only send it to servers that need to see
		// this
		for (ConnectedServer server : ZeusMain.getInstance().getServerManager().getAllServer()) {
			if (!(server instanceof ArtemisServer)) {
				continue;
			}
			ZeusMain.getInstance().getPlayerNameKnowledgeTracker().ensureIsCached(sender, (ArtemisServer) server);
			sendReply(server, new LocalChatMessageMessage(ticket,sender, loc, range, message));
		}
	}

	@Override
	public String getIdentifier() {
		return SendLocalMessage.ID;
	}

}
