package vg.civcraft.mc.namelayer.events;

import java.util.UUID;
import java.util.logging.Level;

import vg.civcraft.mc.civmodcore.interfaces.CustomEvent;
import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.permission.PlayerType;

public class GroupPromotePlayerEvent extends CustomEvent {
	private UUID executor;
	private Group g;
	private PlayerType c;
	private PlayerType f;

	public GroupPromotePlayerEvent(UUID executor, Group g, PlayerType currentType, PlayerType futureType){
		this.executor = executor;
		this.g = g;
		this.c = currentType;
		this.f = futureType;
		NameLayerPlugin.log(Level.WARNING, "Promote Player Event Occured");
	}
	
	public UUID getExecutor(){
	    return executor;
	}
	
	public Group getGroup(){
		return g;
	}
	
	public PlayerType getCurrentPlayerType(){
		return c;
	}
	
	public PlayerType getFuturePlayerType(){
		return f;
	}

}
