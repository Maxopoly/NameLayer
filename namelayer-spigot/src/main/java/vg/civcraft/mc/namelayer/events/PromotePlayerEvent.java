package vg.civcraft.mc.namelayer.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.permission.PlayerType;

public class PromotePlayerEvent extends Event implements Cancellable{
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	private Player player;
	private Group group;
	private PlayerType currentType;
	private PlayerType futureType;

	public PromotePlayerEvent(Player player, Group group, PlayerType currentType, PlayerType futureType){
		this.player = player;
		this.group = group;
		this.currentType = currentType;
		this.futureType = futureType;
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public Group getGroup(){
		return group;
	}
	
	public PlayerType getCurrentPlayerType(){
		return currentType;
	}
	
	public PlayerType getFuturePlayerType(){
		return futureType;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean setvalue) {
		cancelled = setvalue;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
