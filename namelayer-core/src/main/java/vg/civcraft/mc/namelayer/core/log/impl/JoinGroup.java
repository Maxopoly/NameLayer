package vg.civcraft.mc.namelayer.core.log.impl;

import java.util.UUID;

import vg.civcraft.mc.namelayer.core.log.abstr.LoggedGroupActionPersistence;
import vg.civcraft.mc.namelayer.core.log.abstr.MemberRankChange;

public class JoinGroup extends MemberRankChange {
	
	public static final String ID = "JOIN_GROUP";

	public JoinGroup(long time, UUID player, String rank) {
		super(time, player, rank);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}
	
	public static JoinGroup load(LoggedGroupActionPersistence persist) {
		return new JoinGroup(persist.getTimeStamp(), persist.getPlayer(), persist.getRank());
	}
}
