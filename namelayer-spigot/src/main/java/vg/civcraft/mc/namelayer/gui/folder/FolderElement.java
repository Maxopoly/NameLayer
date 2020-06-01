package vg.civcraft.mc.namelayer.gui.folder;

import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.inventorygui.IClickable;
import vg.civcraft.mc.namelayer.gui.GUIGroupOverview;

public abstract class FolderElement {
	
	private FolderElement parent;
	
	public FolderElement(FolderElement parent) {
		this.parent = parent;
	}
	
	public FolderElement getParent() {
		return parent;
	}
	
	public abstract IClickable getGUIEntry(GUIGroupOverview gui, Player player);

}
