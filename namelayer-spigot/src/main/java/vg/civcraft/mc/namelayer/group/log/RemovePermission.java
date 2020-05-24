package vg.civcraft.mc.namelayer.group.log;

import java.util.UUID;

public class RemovePermission extends LoggedGroupAction {

	private String rank;
	private String permission;
	
	public RemovePermission(long time, UUID player, String rank, String permission) {
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
