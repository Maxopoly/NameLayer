package vg.civcraft.mc.namelayer.mc.rabbit.executions;

import org.json.JSONObject;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.requests.GroupModifications;

public class GroupMetaDataUpdate extends AbstractGroupModificationHandler {
	@Override
	protected void handle(Group group, JSONObject data) {
		String key = data.getString("meta_data_key");
		String value = data.getString("meta_data_value");
		getGroupTracker().setMetaDataValue(group, key, value);
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.UPDATE_META_DATA;
	}
}
