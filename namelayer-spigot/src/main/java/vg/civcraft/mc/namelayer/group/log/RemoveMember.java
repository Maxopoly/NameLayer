package vg.civcraft.mc.namelayer.group.log;

import java.util.UUID;

public class RemoveMember extends LoggedGroupAction {

	private String rank;
	private UUID member;
	
	public RemoveMember(long time, UUID player, UUID member, String rank) {
		super(time, player);
		this.rank = rank;
		this.member = member;
	}
	
	public UUID getMember() {
		return member;
	}
	
	public String getRank() {
		return rank;
	}

}
