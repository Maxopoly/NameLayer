package vg.civcraft.mc.namelayer.events;

import org.bukkit.event.HandlerList;

import vg.civcraft.mc.namelayer.group.Group;

public class PostGroupMergeEvent extends GroupMergeEvent {

	public PostGroupMergeEvent(Group toKeep, Group notToKeep) {
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

}
