package vg.civcraft.mc.namelayer.group.log;

import java.util.UUID;

public class RemoveLink extends LoggedGroupAction {

	private boolean isSource;
	private String ownRank;
	private String otherGroup;
	private String otherGroupRank;
	
	public RemoveLink(long time, UUID player, boolean isSource, String ownRank, String otherGroup, String otherGroupRank) {
		super(time, player);
		this.isSource = isSource;
		this.ownRank = ownRank;
		this.otherGroup = otherGroup;
		this.otherGroupRank = otherGroupRank;
	}
	

	public boolean isSource() {
		return isSource;
	}

	public String getOwnRank() {
		return ownRank;
	}

	public String getOtherGroup() {
		return otherGroup;
	}

	public String getOtherGroupRank() {
		return otherGroupRank;
	}
}
