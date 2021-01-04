package vg.civcraft.mc.namelayer.mc;

import vg.civcraft.mc.civmodcore.ACivMod;
import vg.civcraft.mc.namelayer.core.GroupTracker;
import vg.civcraft.mc.namelayer.mc.model.NameLayerPermissionManager;

public class NameLayerPlugin extends ACivMod {
	
	private static NameLayerPlugin instance;
	
	public static NameLayerPlugin getInstance() {
		return instance;
	}

	private GroupTracker groupTracker;
	private NameLayerPermissionManager nameLayerPermManager;
	
	public void onEnable() {
		instance = this;
		groupTracker = new GroupTracker();
		nameLayerPermManager = new NameLayerPermissionManager();
	}
	
	public GroupTracker getGroupTracker() {
		return groupTracker;
	}
	
	public NameLayerPermissionManager getNameLayerPermissionManager() {
		return nameLayerPermManager;
	}
}
