package vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.github.civcraft.zeus.rabbit.DynamicRabbitMessage;
import com.github.civcraft.zeus.rabbit.incoming.GenericInteractiveRabbitCommand;
import com.github.civcraft.zeus.servers.ConnectedServer;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.zeus.NameLayerZPlugin;
import vg.civcraft.mc.namelayer.zeus.ZeusGroupTracker;

public abstract class GroupRequestHandler extends GenericInteractiveRabbitCommand {

	protected ZeusGroupTracker getGroupTracker() {
		return NameLayerZPlugin.getInstance().getGroupTracker();
	}
	
	@Override
	public void handleRequest(String ticket, ConnectedServer sendingServer, JSONObject data) {
		UUID requester = UUID.fromString(data.getString("player"));
		String groupName = data.getString("group");
		Group group = getGroupTracker().getGroup(groupName);
		handle(ticket, sendingServer, data, requester, group);
	}
	
	protected void sendAccept(String transactionID, String packetIdentifier, ConnectedServer server) {
		sendAccept(transactionID, packetIdentifier, server, new HashMap<>());
	}
	
	protected void sendAccept(String transactionID, String packetIdentifier, ConnectedServer server, Map<String, Object> parameters) {
		parameters.put("success", true);
		sendReply(server, new DynamicRabbitMessage(transactionID, packetIdentifier, parameters));
	}
	
	protected void sendReject(String transactionID, String packetIdentifier, ConnectedServer server, Enum<?> reason) {
		sendReject(transactionID, packetIdentifier, server, reason, new HashMap<>());
	}
	
	protected void sendReject(String transactionID, String packetIdentifier, ConnectedServer server, Enum<?> reason, Map<String, Object> parameters) {
		parameters.put("reason", reason.toString());
		parameters.put("success", false);
		sendReply(server, new DynamicRabbitMessage(transactionID, packetIdentifier, parameters));
	}
	
	public abstract void handle(String ticket, ConnectedServer sendingServer, JSONObject data, UUID executor, Group group);

}
