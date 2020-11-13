package vg.civcraft.mc.namelayer.mc;

import vg.civcraft.mc.civmodcore.ACivMod;

public class NameLayerPlugin extends ACivMod {
	
	private static NameLayerPlugin instance;
	
	public static NameLayerPlugin getInstance() {
		return instance;
	}

	private GroupTracker groupTracker;
	
	public GroupTracker getGroupTracker() {
		return groupTracker;
	}

}
