package vg.civcraft.mc.namelayer.core.log.impl;

import java.util.UUID;

import vg.civcraft.mc.namelayer.core.log.abstr.LoggedGroupActionPersistence;
import vg.civcraft.mc.namelayer.core.log.abstr.OtherMemberRankChange;

public class RevokeInvite extends OtherMemberRankChange {

	public static final String ID = "REVOKE_INVITE";
	
	public RevokeInvite(long time, UUID player, String rank, UUID affectedPlayer) {
		super(time, player, rank, affectedPlayer);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}
	
	public static RevokeInvite load(LoggedGroupActionPersistence persist) {
		return new RevokeInvite(persist.getTimeStamp(), persist.getPlayer(), persist.getRank(), UUID.fromString(persist.getName()));
	}
}
