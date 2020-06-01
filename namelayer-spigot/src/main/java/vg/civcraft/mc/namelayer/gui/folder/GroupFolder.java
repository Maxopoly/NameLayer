package vg.civcraft.mc.namelayer.gui.folder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.inventorygui.IClickable;
import vg.civcraft.mc.namelayer.gui.GUIGroupOverview;

public class GroupFolder extends FolderElement{
	
	private List<FolderElement> content;
	
	public GroupFolder(FolderElement parent) {
		super(parent);
		this.content = new ArrayList<>();
	}
	
	public void addElement(FolderElement element) {
		this.content.add(element);
	}
	
	public List<FolderElement> getContent() {
		return Collections.unmodifiableList(this.content);
	}

	@Override
	public IClickable getGUIEntry(GUIGroupOverview gui, Player player) {
		// TODO Auto-generated method stub
		return null;
	}

}
