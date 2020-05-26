package vg.civcraft.mc.namelayer;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.permission.DefaultPermissionLevel;
import vg.civcraft.mc.namelayer.permission.PermissionType;

/**
 * Access point for anything relating to groups and their permissions
 *
 */
public final class GroupAPI {

	private GroupAPI() {
	}

	/**
	 * Retrieves a group by its database id. Every group has a single primary id,
	 * which is guaranteed to be consistent across the groups entire life span.
	 * 
	 * Additionally groups may have a set of secondary ids, which are alternative
	 * identifiers pointing at them and can also be used in this method to get a
	 * group. These are created when groups are merged:
	 * 
	 * If a group is merged into another one, its id(s) will from then on point at
	 * the group it was merged into, this applies transitively.
	 * 
	 * Groups are never deleted, so if a group ever existed for a given id you can
	 * assume that this method will always return a valid group for the same id in
	 * the future
	 * 
	 * @param id Unqiue id used to identify the group
	 * @return Group the given id maps to or null if no such group exists
	 */
	public static Group getGroup(int id) {
		return NameLayerPlugin.getInstance().getGroupManager().getGroup(id);
	}

	/**
	 * Retrieves a group by its current name. Names may change arbitrarily over a
	 * groups lifespan and should thus not be used for storage. Names are
	 * case-insensitive and so is this lookup. There will never be two groups at the
	 * same time whose names equal ignoring case, but due to name changes at a later
	 * point the same name may point to a different group
	 * 
	 * @param name Name of the group to get
	 * @return Group with the given name or null if no group with the name exists
	 */
	public static Group getGroup(String name) {
		return NameLayerPlugin.getInstance().getGroupManager().getGroup(name);
	}

	/**
	 * Checks if the player with the given UUID has the given permission on the
	 * given group. This is true if one of the following applies:
	 * 
	 * - The player is OP
	 * 
	 * - The player has the permission 'namelayer.admin'
	 * 
	 * - The player has a rank on the group which has the permission
	 * 
	 * - The player has a rank in another group which is (possibly transitively)
	 * linked to a rank in this group which has the permission
	 * 
	 * @param player UUID of the player to check perms for, may not be null
	 * @param group  Group to check perms for, may not be null
	 * @param perm   Permission to check for, may not be null
	 * @return True if the player has the permission for the group, false otherwise
	 */
	public static boolean hasPermission(UUID player, Group group, PermissionType perm) {
		return NameLayerPlugin.getInstance().getGroupManager().hasAccess(group, player, perm);
	}

	/**
	 * Checks if the player with the given UUID has the given permission on the
	 * given group. This is true if one of the following applies:
	 * 
	 * - The player is OP
	 * 
	 * - The player has the permission 'namelayer.admin'
	 * 
	 * - The player has a rank on the group which has the permission
	 * 
	 * - The player has a rank in another group which is (possibly transitively)
	 * linked to a rank in this group which has the permission
	 * 
	 * @param player Player to check perms for, may not be null
	 * @param group  Group to check perms for, may not be null
	 * @param perm   Permission to check for, may not be null
	 * @return True if the player has the permission for the group, false otherwise
	 */
	public static boolean hasPermission(Player player, Group group, PermissionType perm) {
		return hasPermission(player.getUniqueId(), group, perm);
	}

	/**
	 * Allows external plugins to register their own permissions which will work
	 * exactly like NameLayers own permissions and loaded/persisted automatically
	 * 
	 * @param plugin           Plugin registering the permission, may not be null
	 * @param name             Name and primary identifier of the permission, must
	 *                         be unique globally and may not change after being
	 *                         used once, otherwise the permission will reset for
	 *                         all groups
	 * @param defaultPermLevel Minimum rank required to have this permission in a
	 *                         newly created group
	 * @param description      Description of this permission to use in UIs
	 * @return PermissionType object created which can be used to do permission
	 *         checks via other methods in this class
	 */
	public PermissionType registerPermission(JavaPlugin plugin, String name, DefaultPermissionLevel defaultPermLevel,
			String description) {
		return PermissionType.registerPermission(plugin, name, defaultPermLevel, description);
	}

}
