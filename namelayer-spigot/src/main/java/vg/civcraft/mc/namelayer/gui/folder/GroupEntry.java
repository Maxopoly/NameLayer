package vg.civcraft.mc.namelayer.gui.folder;

import vg.civcraft.mc.namelayer.group.Group;

public class GroupEntry extends FolderElement {

	private String groupName;
	
	public GroupEntry(FolderElement parent, String groupName) {
		super(parent);
		this.groupName = groupName;
	}
	
	public String getGroupName() {
		return groupName;
	}

}
