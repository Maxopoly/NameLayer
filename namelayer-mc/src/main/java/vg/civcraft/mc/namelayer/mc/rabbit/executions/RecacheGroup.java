package vg.civcraft.mc.namelayer.mc.rabbit.executions;

import org.json.JSONObject;

import com.github.maxopoly.zeus.rabbit.incoming.StaticRabbitCommand;
import com.github.maxopoly.zeus.servers.ConnectedServer;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupTracker;
import vg.civcraft.mc.namelayer.core.requests.GroupModifications;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;

public class RecacheGroup extends StaticRabbitCommand {

	@Override
	public void handleRequest(ConnectedServer sendingServer, JSONObject data) {
		Group group = Group.fromJson(data.getJSONObject("group"));
		GroupTracker tracker = NameLayerPlugin.getInstance().getGroupTracker();
		Group old = tracker.getGroup(group.getPrimaryId());
		if (old != null) {
			tracker.deleteGroup(old);
		}
		for(int id : group.getSecondaryIds()) {
			Group altOld = tracker.getGroup(id);
			if (altOld != null) {
				tracker.deleteGroup(altOld);
			}
		}
		tracker.addGroup(group);
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.RECACHE_GROUP_ID;
	}

}
