package vg.civcraft.mc.namelayer.group.log;

import java.util.UUID;

public class AddPermission extends LoggedGroupAction {

	private String rank;
	private String permission;
	
	public AddPermission(long time, UUID player, String rank, String permission) {
		super(time, player);
		this.rank = rank;
		this.permission = permission;
	}

	public String getRank() {
		return rank;
	}
	
	public String getPermission() {
		return permission;
	}
}
