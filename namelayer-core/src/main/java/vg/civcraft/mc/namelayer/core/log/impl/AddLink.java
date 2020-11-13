package vg.civcraft.mc.namelayer.core.log.impl;

import java.util.UUID;

import vg.civcraft.mc.namelayer.core.log.abstr.LinkStateChange;

public class AddLink extends LinkStateChange {

	public static final String ID = "ADD_LINK";

	public AddLink(long time, UUID player, String ownRankLinked, String otherGroup, String otherGroupRank,
			boolean isSelfOrigin) {
		super(time, player, ownRankLinked, otherGroup, otherGroupRank, isSelfOrigin);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

}
