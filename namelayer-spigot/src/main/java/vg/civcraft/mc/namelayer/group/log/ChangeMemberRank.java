package vg.civcraft.mc.namelayer.group.log;

import java.util.UUID;

public class ChangeMemberRank extends LoggedGroupAction {
	private UUID member;
	private String oldRank;
	private String newRank;
	
	public ChangeMemberRank(long time, UUID player, UUID member, String oldRank, String newRank) {
		super(time, player);
		this.member = member;
		this.oldRank = oldRank;
		this.newRank = newRank;
	}

	public UUID getMember() {
		return member;
	}

	public String getOldRank() {
		return oldRank;
	}

	public String getNewRank() {
		return newRank;
	}
}
