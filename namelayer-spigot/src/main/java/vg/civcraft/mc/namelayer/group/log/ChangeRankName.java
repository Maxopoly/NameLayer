package vg.civcraft.mc.namelayer.group.log;

import java.util.UUID;

public class ChangeRankName extends LoggedGroupAction {
	
	private String oldName;
	private String newName;
	
	public ChangeRankName(long time, UUID player, String oldName, String newName) {
		super(time, player);
		this.oldName = oldName;
		this.newName = newName;
	}
	
	public String getOldName() {
		return oldName;
	}
	
	public String getNewName() {
		return newName;
	}
}
