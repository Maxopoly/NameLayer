package vg.civcraft.mc.namelayer.mc.rabbit.executions;

import org.json.JSONObject;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.log.abstr.LoggedGroupAction;
import vg.civcraft.mc.namelayer.core.log.abstr.LoggedGroupActionPersistence;
import vg.civcraft.mc.namelayer.core.requests.GroupModifications;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;

public class AddToActionLog extends AbstractGroupModificationHandler {
	@Override
	protected void handle(Group group, JSONObject data) {
		String key = data.getString("key");
		LoggedGroupActionPersistence persist = LoggedGroupActionPersistence.fromJSON(data.getJSONObject("action"));
		LoggedGroupAction action = NameLayerPlugin.getInstance().getActionLogFactory().instanciate(key, persist);
		if (action == null) {
			NameLayerPlugin.getInstance().getLogger().severe("Failed to instanciate group log " + data.toString());
			return;
		}
		getGroupTracker().addLogEntry(group, action);
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.ADD_TO_ACTION_LOG;
	}
}
