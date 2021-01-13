package vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.ZeusMain;
import com.github.maxopoly.zeus.model.GlobalPlayerData;
import com.github.maxopoly.zeus.rabbit.DynamicRabbitMessage;
import com.github.maxopoly.zeus.rabbit.incoming.GenericInteractiveRabbitCommand;
import com.github.maxopoly.zeus.servers.ConnectedServer;

import vg.civcraft.mc.namelayer.core.requests.SendPrivateMessage;
import vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges.PrivateChatMessageMessage;

public class SendPrivateChatMessageHandler extends GenericInteractiveRabbitCommand {

	@Override
	public void handleRequest(String ticket, ConnectedServer sendingServer, JSONObject data) {
		UUID sender = UUID.fromString(data.getString("sender"));
		UUID receiver = UUID.fromString(data.getString("receiver"));
		String message = data.getString("message");
		GlobalPlayerData receiverData = ZeusMain.getInstance().getPlayerManager().getOnlinePlayerData(receiver);
		Map<String, Object> replyParameter = new HashMap<>();
		if (receiverData == null) {
			replyParameter.put("success", false);
			replyParameter.put("reason", SendPrivateMessage.FailureReason.PLAYER_DOES_NOT_EXIST.toString());
		} else {
			replyParameter.put("success", true);
		}
		sendReply(sendingServer, new DynamicRabbitMessage(ticket, SendPrivateMessage.REPLY_ID, replyParameter));
		sendReply(receiverData.getMCServer(), new PrivateChatMessageMessage(ticket, sender, receiver, message));
	}

	@Override
	public String getIdentifier() {
		return SendPrivateMessage.REQUEST_ID;
	}

}
