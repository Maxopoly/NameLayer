package vg.civcraft.mc.namelayer.core.log.abstr;

import java.util.UUID;

import com.google.common.base.Preconditions;

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
	
	@Override
	public LoggedGroupActionPersistence getPersistence() {
		return new LoggedGroupActionPersistence(time, player, rank, affectedPlayer.toString(), null);
	}
}
