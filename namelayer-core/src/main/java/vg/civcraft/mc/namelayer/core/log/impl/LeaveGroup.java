package vg.civcraft.mc.namelayer.core.log.impl;

import java.util.UUID;

import vg.civcraft.mc.namelayer.core.log.abstr.LoggedGroupActionPersistence;
import vg.civcraft.mc.namelayer.core.log.abstr.MemberRankChange;

public class LeaveGroup extends MemberRankChange {
	
	public static final String ID = "LEAVE_GROUP";

	public LeaveGroup(long time, UUID player, String rank) {
		super(time, player, rank);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}
	
	public static LeaveGroup load(LoggedGroupActionPersistence persist) {
		return new LeaveGroup(persist.getTimeStamp(), persist.getPlayer(), persist.getRank());
	}

}
