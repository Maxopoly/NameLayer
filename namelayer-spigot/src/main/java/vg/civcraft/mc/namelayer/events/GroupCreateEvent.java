package vg.civcraft.mc.namelayer.events;

import java.util.UUID;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

// Use this to create the group into the database
// Afterwards grab the group from the groupmanager
public class GroupCreateEvent extends Event implements Cancellable{
	private static final HandlerList handlers = new HandlerList();
	
	private String groupName;
	private UUID creator;
	private boolean cancelled = false;
	
	public GroupCreateEvent(String groupName, UUID creator){
		this.groupName = groupName;
		this.creator = creator;
	}

	/**
	 * @return Returns the GroupName.
	 */
	public String getGroupName(){
		return groupName;
	}
	
	/**
	 * @return UUID- The creators UUID
	 */
	public UUID getCreator(){
		return creator;
	}
	
	public boolean isCancelled(){
		return cancelled;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
	    return handlers;
	}

	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}
}
