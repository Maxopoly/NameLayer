package vg.civcraft.mc.namelayer.core.log.impl;

import java.util.UUID;

import vg.civcraft.mc.namelayer.core.log.abstr.LoggedGroupActionPersistence;
import vg.civcraft.mc.namelayer.core.log.abstr.OtherMemberRankChange;

public class InviteMember extends OtherMemberRankChange {
	
	public static final String ID = "INVITE_MEMBER";

	public InviteMember(long time, UUID player, String rank, UUID affectedPlayer) {
		super(time, player, rank, affectedPlayer);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}	
	
	public static InviteMember load(LoggedGroupActionPersistence persist) {
		return new InviteMember(persist.getTimeStamp(), persist.getPlayer(), persist.getRank(), UUID.fromString(persist.getName()));
	}
}
