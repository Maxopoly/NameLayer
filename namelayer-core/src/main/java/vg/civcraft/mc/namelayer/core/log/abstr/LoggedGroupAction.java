package vg.civcraft.mc.namelayer.core.log.abstr;

import java.util.UUID;

import com.google.common.base.Preconditions;

public abstract class LoggedGroupAction {

	protected final UUID player;
	protected final long time;

	public LoggedGroupAction(long time, UUID player) {
		Preconditions.checkNotNull(player, "Can not store action for null player");
		Preconditions.checkArgument(time > 0, "Time stamp must be initialized");
		this.player = player;
		this.time = time;
	}

	/**
	 * @return UUID of the player who initiated this action
	 */
	public UUID getPlayer() {
		return player;
	}

	/**
	 * @return UNIX timestamp in ms of when this happened
	 */
	public long getTimeStamp() {
		return time;
	}
	
	public abstract String getIdentifier();
	
	public abstract LoggedGroupActionPersistence getPersistence();

}
