package vg.civcraft.mc.namelayer.mc.model;

import vg.civcraft.mc.namelayer.core.DefaultPermissionLevel;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.mc.GroupAPI;

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
	private PermissionType renameGroup;
	private PermissionType linkGroup;
	private PermissionType changeGroupColor;

	public NameLayerPermissionManager() {
		registerInternalPermissions();
	}

	private void registerInternalPermissions() {
		// allows adding/modifying a password for the group
		GroupAPI.registerPermission("PASSWORD", DefaultPermissionLevel.ADMIN,
				"Allows viewing this groups password and changing or removing it");
		// allows to list the permissions for each permission group
		GroupAPI.registerPermission("LIST_PERMS", DefaultPermissionLevel.ADMIN,
				"Allows viewing how permissions for this group are set up");
		// allows to see general group stats
		GroupAPI.registerPermission("GROUPSTATS", DefaultPermissionLevel.ADMIN,
				"Gives access to various group statistics such as member "
						+ "counts by permission type, who owns the group etc.");
		// allows to modify the permissions for different permissions groups
		GroupAPI.registerPermission("PERMS", DefaultPermissionLevel.OWNER,
				"Allows modifying permissions for this group");
		// allows deleting the group
		GroupAPI.registerPermission("DELETE", DefaultPermissionLevel.OWNER,
				"Allows deleting this group");
		// allows merging the group with another one
		GroupAPI.registerPermission("MERGE", DefaultPermissionLevel.OWNER,
				"Allows merging this group into another or merging another group into this one");
		// allows creating player types
		GroupAPI.registerPermission("CREATE_PLAYERTYPE", DefaultPermissionLevel.OWNER,
				"Allows creating new player types for this group");
		// allows deleting player types
		GroupAPI.registerPermission("DELETE_PLAYERTYPE", DefaultPermissionLevel.OWNER,
				"Allows deleting player types for this group");
		// allows deleting player types
		GroupAPI.registerPermission("RENAME_PLAYERTYPE", DefaultPermissionLevel.OWNER,
				"Allows renaming player types for this group");
		GroupAPI.registerPermission("RENAME_GROUP", DefaultPermissionLevel.OWNER,
				"Allows renaming the group");
		GroupAPI.registerPermission("LINK_GROUP", DefaultPermissionLevel.OWNER,
				"Allows linking and unlinking the group");
		GroupAPI.registerPermission("GROUP_COLOR", DefaultPermissionLevel.ADMIN,
				"Allows changing the groups color prefix");
	}

	public PermissionType getLinkGroup() {
		return linkGroup;
	}

	public PermissionType getChangeGroupColor() {
		return changeGroupColor;
	}

	public PermissionType getRenameGroup() {
		return renameGroup;
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
