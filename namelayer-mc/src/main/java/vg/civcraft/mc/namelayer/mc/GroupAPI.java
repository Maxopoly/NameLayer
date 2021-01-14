package vg.civcraft.mc.namelayer.mc;

import java.util.UUID;

import org.bukkit.entity.Player;

import com.github.maxopoly.artemis.ArtemisPlugin;

import vg.civcraft.mc.namelayer.core.DefaultPermissionLevel;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.mc.rabbit.outgoing.PermissionCreation;
import vg.civcraft.mc.namelayer.mc.rabbit.outgoing.RequestGroupCache;

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
		return NameLayerPlugin.getInstance().getGroupTracker().getGroup(id);
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
		return NameLayerPlugin.getInstance().getGroupTracker().getGroup(name);
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
		return NameLayerPlugin.getInstance().getGroupTracker().hasAccess(group, player, perm);
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
	 * @param name             Name and primary identifier of the permission, must
	 *                         be unique globally and may not change after being
	 *                         used once, otherwise the permission will reset for
	 *                         all groups
	 * @param defaultPermLevel Minimum rank required to have this permission in a
	 *                         newly created group
	 * @param description      Description of this permission to use in UIs
	 */
	public static void registerPermission(String name, DefaultPermissionLevel defaultPermLevel,
			String description) {
		PermissionType perm = NameLayerPlugin.getInstance().getGroupTracker().getPermissionTracker().getPermission(name);
		if (perm != null) {
			return;
		}
		ArtemisPlugin.getInstance().getRabbitHandler().sendMessage(new PermissionCreation(name, defaultPermLevel));
	}
	
	public static Group getDefaultGroup(Player player) {
		return NameLayerPlugin.getInstance().getSettingsManager().getDefaultGroup().getGroup(player);
	}
	
	public static void requestToBeCached(int groupID) {
		if (getGroup(groupID) != null) {
			return;
		}
		ArtemisPlugin.getInstance().getRabbitHandler().sendMessage(new RequestGroupCache(groupID));
	}

}
