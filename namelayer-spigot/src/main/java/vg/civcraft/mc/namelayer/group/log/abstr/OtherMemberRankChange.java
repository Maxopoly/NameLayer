package vg.civcraft.mc.namelayer.group.log.abstr;

import java.util.UUID;

import com.google.common.base.Preconditions;

import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.group.log.LoggedGroupActionPersistence;

public abstract class OtherMemberRankChange extends MemberRankChange {

	protected final UUID affectedPlayer;
	
	public OtherMemberRankChange(long time, UUID player, String rank, UUID affectedPlayer) {
		super(time, player, rank);
		Preconditions.checkNotNull(affectedPlayer, "Victim player may not be null");
		this.affectedPlayer = affectedPlayer;
	}
	
	public UUID getAffectedPlayer() {
		return affectedPlayer;
	}
	
	public String getAffectedPlayerName() {
		return NameAPI.getCurrentName(affectedPlayer);
	}
	
	@Override
	public LoggedGroupActionPersistence getPersistence() {
		return new LoggedGroupActionPersistence(time, player, rank, affectedPlayer.toString(), null);
	}

}
