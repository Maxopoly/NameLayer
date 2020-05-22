package vg.civcraft.mc.namelayer.permission;

public class NameLayerPermissionManager {
	
	private PermissionType password;
	private PermissionType listPerms;
	private PermissionType modifyPerms;
	private PermissionType deleteGroup;
	private PermissionType mergeGroup;
	private PermissionType createPlayerType;
	private PermissionType deletePlayerType;
	private PermissionType renamePlayerType;
	private PermissionType groupStats;
	
	public NameLayerPermissionManager() {
		password = PermissionType.getPermission("PASSWORD");
		listPerms = PermissionType.getPermission("LIST_PERMS");
		modifyPerms = PermissionType.getPermission("PERMS");
		deleteGroup = PermissionType.getPermission("DELETE");
		mergeGroup = PermissionType.getPermission("MERGE");
		createPlayerType = PermissionType.getPermission("CREATE_PLAYERTYPE");
		deletePlayerType = PermissionType.getPermission("DELETE_PLAYERTYPE");
		renamePlayerType = PermissionType.getPermission("RENAME_PLAYERTYPE");
		groupStats = PermissionType.getPermission("GROUPSTATS");
	}
	
	public PermissionType getPassword() {
		return password;
	}
	
	public PermissionType getListPerms() {
		return listPerms;
	}
	
	public PermissionType getModifyPerms() {
		return modifyPerms;
	}
	
	public PermissionType getDeleteGroup() {
		return deleteGroup;
	}
	
	public PermissionType getMergeGroup() {
		return mergeGroup;
	}
	
	public PermissionType getCreatePlayerType() {
		return createPlayerType;
	}
	
	public PermissionType getDeletePlayerType() {
		return deletePlayerType;
	}
	
	public PermissionType getRenamePlayerType() {
		return renamePlayerType;
	}
	
	public PermissionType getGroupStats() {
		return groupStats;
	}

}
