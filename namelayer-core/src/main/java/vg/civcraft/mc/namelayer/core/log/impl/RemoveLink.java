package vg.civcraft.mc.namelayer.core.log.impl;

import java.util.UUID;

import vg.civcraft.mc.namelayer.core.log.abstr.LinkStateChange;
import vg.civcraft.mc.namelayer.core.log.abstr.LoggedGroupActionPersistence;

public class RemoveLink extends LinkStateChange {
	
	public static final String ID = "REMOVE_LINK";

	public RemoveLink(long time, UUID player, String ownRankLinked, String otherGroup, String otherGroupRank,
			boolean isSelfOrigin) {
		super(time, player, ownRankLinked, otherGroup, otherGroupRank, isSelfOrigin);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}
	
	public static RemoveLink load(LoggedGroupActionPersistence persist) {
		boolean isSelfOrigin = persist.getExtraText().charAt(0) == 't';
		String otherRank = persist.getExtraText().substring(1);
		return new RemoveLink(persist.getTimeStamp(), persist.getPlayer(), persist.getRank(), persist.getName(),
				otherRank, isSelfOrigin);
	}
}
