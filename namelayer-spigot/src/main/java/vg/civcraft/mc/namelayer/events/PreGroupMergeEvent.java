package vg.civcraft.mc.namelayer.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import vg.civcraft.mc.namelayer.group.Group;

public class PreGroupMergeEvent extends GroupMergeEvent implements Cancellable{

	private boolean isCancelled = false;
	
	public PreGroupMergeEvent(Group toKeep, Group notToKeep) {
		super(toKeep, notToKeep);
	}

	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean value) {
		isCancelled = value;
	}

}
