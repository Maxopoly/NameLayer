package vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges;

import org.json.JSONObject;

public class MergeGroupMessage extends GroupChangeMessage {

	private int groupMergedInto;
	
	public MergeGroupMessage(int groupID, int groupMergedInto) {
		super(groupID);
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("group_merged_into", groupMergedInto);
	}

	@Override
	public String getIdentifier() {
		return "nl_merge_group";
	}

}
