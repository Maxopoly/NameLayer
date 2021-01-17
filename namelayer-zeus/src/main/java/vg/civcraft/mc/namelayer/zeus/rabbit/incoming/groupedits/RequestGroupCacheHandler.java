package vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits;

import org.json.JSONObject;

import com.github.maxopoly.zeus.rabbit.incoming.StaticRabbitCommand;
import com.github.maxopoly.zeus.servers.ArtemisServer;
import com.github.maxopoly.zeus.servers.ConnectedServer;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.requests.GroupModifications;
import vg.civcraft.mc.namelayer.zeus.NameLayerZPlugin;

public class RequestGroupCacheHandler extends StaticRabbitCommand {

	@Override
	public void handleRequest(ConnectedServer sendingServer, JSONObject data) {
		int id = data.getInt("group_id");
		Group group = NameLayerZPlugin.getInstance().getGroupTracker().loadOrGetGroup(id);
		if (group != null) {
			NameLayerZPlugin.getInstance().getGroupKnowledgeTracker().ensureIsCached(group, (ArtemisServer) sendingServer);
		}
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.GET_GROUP;
	}

}
