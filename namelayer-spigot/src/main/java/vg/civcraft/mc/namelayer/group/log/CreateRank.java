package vg.civcraft.mc.namelayer.group.log;

import java.util.UUID;

public class CreateRank extends LoggedGroupAction {
	
	private String parent;
	private String name;
	
	public CreateRank(long time, UUID player, String name, String parent) {
		super(time, player);
		this.name = name;
		this.parent = parent;
	}
	
	public String getParent() {
		return parent;
	}
	
	public String getName() {
		return name;
	}
}
