package vg.civcraft.mc.namelayer.mc.rabbit.executions;

import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.rabbit.incoming.StaticRabbitCommand;
import com.github.maxopoly.zeus.servers.ConnectedServer;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.requests.GroupModifications;
import vg.civcraft.mc.namelayer.mc.GroupAPI;
import vg.civcraft.mc.namelayer.mc.model.chat.GroupChatMode;

public class SendGroupChatMessage extends StaticRabbitCommand {

	@Override
	public void handleRequest(ConnectedServer sendingServer, JSONObject data) {
		UUID sender = UUID.fromString(data.getString("player"));
		String message = data.getString("message");
		int groupID = data.getInt("group_id");
		Group group = GroupAPI.getGroup(groupID);
		if (group != null) {
			GroupChatMode.doLocalGroupChatMessageDistribute(group, sender, message);
		}
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.SEND_GROUP_MESSAGE;
	}

}
