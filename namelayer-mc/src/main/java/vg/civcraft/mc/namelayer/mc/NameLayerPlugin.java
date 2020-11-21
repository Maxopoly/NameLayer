package vg.civcraft.mc.namelayer.mc;

import vg.civcraft.mc.civmodcore.ACivMod;
import vg.civcraft.mc.namelayer.core.GroupTracker;

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
