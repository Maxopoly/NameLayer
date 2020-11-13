package vg.civcraft.mc.namelayer.mc.rabbit.groupmodifications;

import org.json.JSONObject;

import com.github.civcraft.zeus.rabbit.incoming.StaticRabbitCommand;
import com.github.civcraft.zeus.servers.ConnectedServer;
import com.google.gson.JsonObject;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.mc.GroupAPI;
import vg.civcraft.mc.namelayer.mc.GroupTracker;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;

public abstract class AbstractGroupModificationHandler extends StaticRabbitCommand {

	@Override
	public void handleRequest(ConnectedServer sendingServer, JSONObject data) {
		String groupName = data.getString("group");
		Group group = GroupAPI.getGroup(groupName);
		if (group == null) {
			NameLayerPlugin.getInstance().getLogger().warning("Received update for group " + groupName + ", but held no data for it");
			return;
		}
		handle(group, data);
	}
	
	protected abstract void handle(Group group, JSONObject data);
	
	protected GroupTracker getGroupTracker() {
		return NameLayerPlugin.getInstance().getGroupTracker();
	}

}
