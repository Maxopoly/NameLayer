package vg.civcraft.mc.namelayer.core.log.impl;

import java.util.UUID;

import vg.civcraft.mc.namelayer.core.log.abstr.LoggedGroupActionPersistence;
import vg.civcraft.mc.namelayer.core.log.abstr.MemberRankChange;

public class AcceptInvitation extends MemberRankChange {
	
	public static final String ID = "ACCEPT_INVITE";

	public AcceptInvitation(long time, UUID player, String rank) {
		super(time, player, rank);
	}
	
	@Override
	public String getIdentifier() {
		return ID;
	}
	
	public static AcceptInvitation load(LoggedGroupActionPersistence persist) {
		return new AcceptInvitation(persist.getTimeStamp(), persist.getPlayer(), persist.getRank());
	}

}
