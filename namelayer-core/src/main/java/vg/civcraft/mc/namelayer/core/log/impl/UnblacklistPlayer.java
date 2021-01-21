package vg.civcraft.mc.namelayer.core.log.impl;

import java.util.UUID;

import vg.civcraft.mc.namelayer.core.log.abstr.LoggedGroupActionPersistence;
import vg.civcraft.mc.namelayer.core.log.abstr.OtherMemberRankChange;

public class UnblacklistPlayer extends OtherMemberRankChange {

	public static final String ID = "UNBLACKLIST_PLAYER";

	public UnblacklistPlayer(long time, UUID player, String rank, UUID affectedPlayer) {
		super(time, player, rank, affectedPlayer);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

	public static UnblacklistPlayer load(LoggedGroupActionPersistence persist) {
		return new UnblacklistPlayer(persist.getTimeStamp(), persist.getPlayer(), persist.getRank(), UUID.fromString(persist.getName()));
	}
}
