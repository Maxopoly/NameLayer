package vg.civcraft.mc.namelayer.group.log;

import java.util.UUID;

public class MergeGroup extends LoggedGroupAction {

	private String groupMergedIn;
	
	public MergeGroup(long time, UUID player, String groupMergedIn) {
		super(time, player);
		this.groupMergedIn = groupMergedIn;
	}
	
	public String getGroupMergedIn() {
		return groupMergedIn;
	}

}
