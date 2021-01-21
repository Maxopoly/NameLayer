package vg.civcraft.mc.namelayer.core.log.impl;

import java.util.UUID;

import com.google.common.base.Preconditions;

import vg.civcraft.mc.namelayer.core.log.abstr.LoggedGroupActionPersistence;
import vg.civcraft.mc.namelayer.core.log.abstr.OtherMemberRankChange;

public class ChangeMemberRank extends OtherMemberRankChange {
	
	public static final String ID = "PROMOTE_MEMBER";

	private String oldRank;

	public ChangeMemberRank(long time, UUID player, String rank, UUID affectedPlayer, String oldRank) {
		super(time, player, rank, affectedPlayer);
		Preconditions.checkNotNull(oldRank, "Previous rank may not be null");
		this.oldRank = oldRank;
	}

	public String getOldRank() {
		return oldRank;
	}

	@Override
	public String getIdentifier() {
		return ID;
	}
	
	@Override
	public LoggedGroupActionPersistence getPersistence() {
		return new LoggedGroupActionPersistence(time, player, rank, affectedPlayer.toString(), oldRank);
	}
	
	public static ChangeMemberRank load(LoggedGroupActionPersistence persist) {
		return new ChangeMemberRank(persist.getTimeStamp(), persist.getPlayer(), persist.getRank(), UUID.fromString(persist.getName()), persist.getExtraText());
	}
}
