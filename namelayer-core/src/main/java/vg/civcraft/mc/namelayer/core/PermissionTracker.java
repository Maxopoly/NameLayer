package vg.civcraft.mc.namelayer.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class PermissionTracker {
	
	private static final String LIST_RANK_PREFIX = "listPlayer#";
	private static final String INVITE_RANK_PREFIX = "invitePlayer#";
	private static final String REMOVE_RANK_PREFIX = "removePlayer#";
	
	private Map<String, PermissionType> permissionByName;
	private Map<Integer, PermissionType> permissionById;
	
	public PermissionTracker() {
		this.permissionByName = new HashMap<>();
		this.permissionById = new TreeMap<>();
	}
	
	void putPermission(PermissionType perm) {
		this.permissionByName.put(perm.getName(), perm);
		this.permissionById.put(perm.getId(), perm);
	}

	/**
	 * Retrieves a permission by it's name
	 * 
	 * @param name Name of the permission
	 * @return Permission with the given name or null if no such permission exists
	 */
	public PermissionType getPermission(String name) {
		return permissionByName.get(name);
	}

	/**
	 * Retrieves a permission by it's id
	 * 
	 * @param id Id of the permission
	 * @return Permission with the given id or null if no such permission exists
	 */
	public PermissionType getPermission(int id) {
		return permissionById.get(id);
	}
	
	/**
	 * Gets the permission type required to invite or add players to the player type
	 * with the given id. This permission is independent from the group it is
	 * applied to, it will allow inviting to the player type with the given id for
	 * any group
	 * 
	 * @param id ID of the player type to get the invite permission for
	 * @return Invite permission for the given id
	 */
	public PermissionType getInvitePermission(int id) {
		return getPermission(INVITE_RANK_PREFIX + id);
	}

	/**
	 * Gets the permission type required to remove players from the player type with
	 * the given id. This permission is independent from the group it is applied to,
	 * it will allow removing from the player type with the given id for any group
	 * 
	 * @param id ID of the player type to get the remove permission for
	 * @return Remove permission for the given id
	 */
	public PermissionType getRemovePermission(int id) {
		return getPermission(REMOVE_RANK_PREFIX + id);
	}

	/**
	 * Gets the permission type required to list players for a player type with the
	 * given id. This permission is independent from the group it is applied to, it
	 * will allow listing members for the player type with the given id for any
	 * group
	 * 
	 * @param id ID of the player type to get the list permission for
	 * @return List permission for the given id
	 */
	public PermissionType getListPermission(int id) {
		return getPermission(LIST_RANK_PREFIX + id);
	}

	/**
	 * @return All existing permissions
	 */
	public Collection<PermissionType> getAllPermissions() {
		return permissionByName.values();
	}
	
	

}
