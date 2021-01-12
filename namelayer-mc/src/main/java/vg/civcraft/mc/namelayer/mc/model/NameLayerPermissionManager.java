package vg.civcraft.mc.namelayer.mc.model;

import vg.civcraft.mc.namelayer.core.DefaultPermissionLevel;
import vg.civcraft.mc.namelayer.core.GroupRankHandler;
import vg.civcraft.mc.namelayer.core.NameLayerPermissions;
import vg.civcraft.mc.namelayer.core.PermissionTracker;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.mc.GroupAPI;

public class NameLayerPermissionManager {
	
	private PermissionTracker permTracker;

	public NameLayerPermissionManager(PermissionTracker permTracker) {
		this.permTracker = permTracker;
		registerInternalPermissions();
	}

	private void registerInternalPermissions() {
		// allows adding/modifying a password for the group
		GroupAPI.registerPermission(NameLayerPermissions.PASSWORD, DefaultPermissionLevel.ADMIN,
				"Allows viewing this groups password and changing or removing it");
		// allows to list the permissions for each permission group
		GroupAPI.registerPermission(NameLayerPermissions.LIST_PERMS, DefaultPermissionLevel.ADMIN,
				"Allows viewing how permissions for this group are set up");
		// allows to see general group stats
		GroupAPI.registerPermission(NameLayerPermissions.GROUP_STATS, DefaultPermissionLevel.ADMIN,
				"Gives access to various group statistics such as member "
						+ "counts by permission type, who owns the group etc.");
		// allows to modify the permissions for different permissions groups
		GroupAPI.registerPermission(NameLayerPermissions.MODIFY_PERMS, DefaultPermissionLevel.OWNER,
				"Allows modifying permissions for this group");
		// allows deleting the group
		GroupAPI.registerPermission(NameLayerPermissions.DELETE_GROUP, DefaultPermissionLevel.OWNER, "Allows deleting this group");
		// allows merging the group with another one
		GroupAPI.registerPermission(NameLayerPermissions.MERGE_GROUP, DefaultPermissionLevel.OWNER,
				"Allows merging this group into another or merging another group into this one");
		// allows creating player types
		GroupAPI.registerPermission(NameLayerPermissions.CREATE_RANK, DefaultPermissionLevel.OWNER,
				"Allows creating new player types for this group");
		// allows deleting player types
		GroupAPI.registerPermission(NameLayerPermissions.DELETE_RANK, DefaultPermissionLevel.OWNER,
				"Allows deleting player types for this group");
		// allows deleting player types
		GroupAPI.registerPermission(NameLayerPermissions.RENAME_RANK, DefaultPermissionLevel.OWNER,
				"Allows renaming player types for this group");
		GroupAPI.registerPermission(NameLayerPermissions.RENAME_GROUP, DefaultPermissionLevel.OWNER, "Allows renaming the group");
		GroupAPI.registerPermission(NameLayerPermissions.LINK_GROUP, DefaultPermissionLevel.OWNER,
				"Allows linking and unlinking the group");
		GroupAPI.registerPermission(NameLayerPermissions.EDIT_GROUP_COLOR, DefaultPermissionLevel.ADMIN,
				"Allows changing the groups color prefix");
		for (int i = 0; i < GroupRankHandler.getMaximumTypeCount(); i++) {
			GroupAPI.registerPermission(PermissionTracker.LIST_RANK_PREFIX + i, DefaultPermissionLevel.SPECIAL, "");
			GroupAPI.registerPermission(PermissionTracker.INVITE_RANK_PREFIX + i, DefaultPermissionLevel.SPECIAL, "");
			GroupAPI.registerPermission(PermissionTracker.REMOVE_RANK_PREFIX + i, DefaultPermissionLevel.SPECIAL, "");		
		}
		GroupAPI.registerPermission(NameLayerPermissions.READ_CHAT, DefaultPermissionLevel.MEMBER,
				"Allows receiving messages sent in the group chat");
		GroupAPI.registerPermission(NameLayerPermissions.WRITE_CHAT, DefaultPermissionLevel.MEMBER,
				"Allows sending messages to the group chat");		
	}
	
	public PermissionType getReadChat() {
		return permTracker.getPermission(NameLayerPermissions.READ_CHAT);
	}

}
