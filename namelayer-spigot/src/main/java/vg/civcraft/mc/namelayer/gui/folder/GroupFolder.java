package vg.civcraft.mc.namelayer.gui.folder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.inventorygui.IClickable;
import vg.civcraft.mc.civmodcore.inventorygui.LClickable;
import vg.civcraft.mc.namelayer.gui.GUIGroupOverview;

public class GroupFolder extends FolderElement{
	
	private Map<String, FolderElement> content;
	
	public GroupFolder(String folderName, FolderElement parent) {
		super(folderName, parent);
		this.content = new HashMap<>();
	}
	
	public void addElement(FolderElement element) {
		this.content.put(element.getIdentifier(), element);
	}
	
	public FolderElement getElement(String identifier) {
		return content.get(identifier);
	}
	
	public Collection<FolderElement> getContent() {
		return Collections.unmodifiableCollection(this.content.values());
	}

	@Override
	public IClickable getGUIEntry(GUIGroupOverview gui, Player player) {
		return new LClickable(Material.CHEST, ChatColor.GOLD + getIdentifier(), p -> {
			if (doMovingCheck(gui, player)) {
				return;
			}
 			gui.setViewedFolder(this);
		});
	}

}
