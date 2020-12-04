package vg.civcraft.mc.namelayer.core.log.impl;

import java.util.UUID;

import vg.civcraft.mc.namelayer.core.log.abstr.LinkStateChange;
import vg.civcraft.mc.namelayer.core.log.abstr.LoggedGroupActionPersistence;

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
	
	public static AddLink load(LoggedGroupActionPersistence persist) {
		boolean isSelfOrigin = persist.getExtraText().charAt(0) == 't';
		String otherRank = persist.getExtraText().substring(1);
		return new AddLink(persist.getTimeStamp(), persist.getPlayer(), persist.getRank(), persist.getName(),
				otherRank, isSelfOrigin);
	}

}
