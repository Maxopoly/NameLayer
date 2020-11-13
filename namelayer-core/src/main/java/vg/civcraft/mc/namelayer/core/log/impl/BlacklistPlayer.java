package vg.civcraft.mc.namelayer.core.log.impl;

import java.util.UUID;

import vg.civcraft.mc.namelayer.core.log.abstr.OtherMemberRankChange;

public class BlacklistPlayer extends OtherMemberRankChange {
	
	public static final String ID = "BLACKLIST_PLAYER";

	public BlacklistPlayer(long time, UUID player, String rank, UUID affectedPlayer) {
		super(time, player, rank, affectedPlayer);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}	
}
