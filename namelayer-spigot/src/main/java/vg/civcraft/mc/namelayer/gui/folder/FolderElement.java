package vg.civcraft.mc.namelayer.gui.folder;

import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.inventorygui.IClickable;
import vg.civcraft.mc.namelayer.gui.GUIGroupOverview;

public abstract class FolderElement {
	
	private FolderElement parent;
	private String identifier;
	
	public FolderElement(String identifier, FolderElement parent) {
		this.identifier = identifier;
		this.parent = parent;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public FolderElement getParent() {
		return parent;
	}
	
	public abstract IClickable getGUIEntry(GUIGroupOverview gui, Player player);
	
	protected boolean doMovingCheck(GUIGroupOverview gui, Player player) {
		//TODO
		return false;
	}

}
