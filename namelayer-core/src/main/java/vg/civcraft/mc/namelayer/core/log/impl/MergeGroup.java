package vg.civcraft.mc.namelayer.core.log.impl;

import java.util.UUID;

import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.log.abstr.LoggedGroupAction;

public class MergeGroup extends LoggedGroupAction {
	
	public static final String ID = "MERGE_GROUP";

	private String groupMergedIn;
	
	public MergeGroup(long time, UUID player, String groupMergedIn) {
		super(time, player);
		this.groupMergedIn = groupMergedIn;
	}
	
	public String getGroupMergedIn() {
		return groupMergedIn;
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("group_merged", groupMergedIn);
	}

}
