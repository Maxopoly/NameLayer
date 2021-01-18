package vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges;

import org.json.JSONObject;
import vg.civcraft.mc.namelayer.core.requests.GroupModifications;

public class GroupMetaDataChangeMessage extends GroupChangeMessage{

	private String key;
	private String value;

	public GroupMetaDataChangeMessage(int groupID, String key, String value) {
		super(groupID);
		this.key = key;
		this.value = value;
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("meta_data_key", key);
		json.put("meta_data_value", value);
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.UPDATE_META_DATA;
	}
}
