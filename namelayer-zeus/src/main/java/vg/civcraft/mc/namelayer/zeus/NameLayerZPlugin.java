package vg.civcraft.mc.namelayer.zeus;

import com.github.civcraft.zeus.plugin.ZeusLoad;
import com.github.civcraft.zeus.plugin.ZeusPlugin;

@ZeusLoad(name = "NameLayer", version = "1.0", description = "Player driven group management")
public class NameLayerZPlugin extends ZeusPlugin{
	
	private static NameLayerZPlugin instance;
	
	public static NameLayerZPlugin getInstance() {
		return instance;
	}
	
	private ZeusGroupTracker groupTracker;

	
	
	
	@Override
	public void onEnable() {
		instance = this;
		groupTracker = new ZeusGroupTracker(null);
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		
	}
	
	public ZeusGroupTracker getGroupTracker() {
		return groupTracker;
	}

}
