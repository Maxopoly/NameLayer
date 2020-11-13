package vg.civcraft.mc.namelayer.core.log.impl;

import java.util.UUID;

import vg.civcraft.mc.namelayer.core.log.abstr.OtherMemberRankChange;

public class RemoveMember extends OtherMemberRankChange {
	
	public static final String ID = "REMOVE_MEMBER";

	public RemoveMember(long time, UUID player, String rank, UUID affectedPlayer) {
		super(time, player, rank, affectedPlayer);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

}
