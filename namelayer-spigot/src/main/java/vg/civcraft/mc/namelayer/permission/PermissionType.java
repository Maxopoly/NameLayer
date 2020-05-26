package vg.civcraft.mc.namelayer.permission;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import vg.civcraft.mc.namelayer.NameLayerPlugin;

/**
 * Allows creating and retrieving instances in a static way, while also
 * representing an instance of a single permission. A permission can be
 * identified by both it's id and it's name, so both have to be unique.
 * Permissions are handled as singleton each, you can assume that for a
 * PermissionType with a given name or id always maximum one instance will exist
 *
 */
public final class PermissionType {

	private static Map<String, PermissionType> permissionByName;
	private static Map<Integer, PermissionType> permissionById;
	private static int maximumExistingId;

	public static void initialize() {
		permissionByName = new HashMap<>();
		permissionById = new TreeMap<>();
		maximumExistingId = 0;
		registerRankPermissions();
	}

	/**
	 * Retrieves a permission by it's name
	 * 
	 * @param name Name of the permission
	 * @return Permission with the given name or null if no such permission exists
	 */
	public static PermissionType getPermission(String name) {
		return permissionByName.get(name);
	}

	/**
	 * Retrieves a permission by it's id
	 * 
	 * @param id Id of the permission
	 * @return Permission with the given id or null if no such permission exists
	 */
	public static PermissionType getPermission(int id) {
		return permissionById.get(id);
	}

	/**
	 * Only used internally, specify your permissions with a description instead
	 */
	private static void registerPermission(String name) {
		registerPermission(NameLayerPlugin.getInstance(), name, DefaultPermissionLevel.OWNER, null);
	}

	/**
	 * Allows external plugins to register their own permissions. Additionally to a
	 * name and description, they can specify a list of permission levels, which
	 * will get this permision by default, when a new group is created. This follows
	 * a static mapping: 1 = Admin, 2 = Mod, 3 = Member, 4 = DefaultNonMember, 5 =
	 * Blacklisted Owner with an id of 0 will automatically have the permission, as
	 * it does with all others
	 * 
	 * This will not be applied to already existing groups, as they might have a
	 * different structure than the one this is intended to be applied to.
	 * 
	 * If a permission with the given name was already registed, doing so again will
	 * fail without any further issues
	 * 
	 * @param name
	 * @param defaultPermLevels
	 * @param description
	 */
	public static PermissionType registerPermission(JavaPlugin registeringPlugin, String name, DefaultPermissionLevel defaultPermLevel,
			String description) {
		if (name == null) {
			Bukkit.getLogger().severe("Could not register permission, name was null");
			return null;
		}
		if (permissionByName.get(name) != null) {
			Bukkit.getLogger().severe("Could not register permission " + name + ". It was already registered");
			return null;
		}
		int id = -1;
		Map<Integer, String> dbRegisteredPerms = NameLayerPlugin.getInstance().getGroupManagerDao()
				.getPermissionMapping();
		for (Entry<Integer, String> perm : dbRegisteredPerms.entrySet()) {
			if (perm.getValue().equals(name)) {
				id = perm.getKey();
				break;
			}
		}
		List<Integer> defaultPermLevels = defaultPermLevel.getAllowedRankIds();
		PermissionType perm;
		if (id == -1) {
			// not in db yet
			id = maximumExistingId + 1;
			while (dbRegisteredPerms.get(id) != null) {
				id++;
			}
			maximumExistingId = id;
			perm = new PermissionType(registeringPlugin, name, id, defaultPermLevels, description);
			NameLayerPlugin.getInstance().getGroupManagerDao().registerPermission(perm);
		} else {
			// already in db, so use existing id
			perm = new PermissionType(registeringPlugin, name, id, defaultPermLevels, description);
		}
		permissionByName.put(name, perm);
		permissionById.put(id, perm);
		return perm;
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
	public static PermissionType getInvitePermission(int id) {
		String invitePermName = "invitePlayer#" + id;
		PermissionType invPerm = PermissionType.getPermission(invitePermName);
		if (invPerm == null) {
			// register type, because it was never used before, we do this with
			// the deprecated register without a description
			// because any further description is handled by the UI and
			// dependent on the current name of the rank
			registerPermission(invitePermName);
			invPerm = getPermission(invitePermName);
		}
		return invPerm;
	}

	/**
	 * Gets the permission type required to remove players from the player type with
	 * the given id. This permission is independent from the group it is applied to,
	 * it will allow removing from the player type with the given id for any group
	 * 
	 * @param id ID of the player type to get the remove permission for
	 * @return Remove permission for the given id
	 */
	public static PermissionType getRemovePermission(int id) {
		String removePermName = "removePlayer#" + id;
		PermissionType removePerm = PermissionType.getPermission(removePermName);
		if (removePerm == null) {
			// register type, because it was never used before, we do this with
			// the deprecated register without a description
			// because any further description is handled by the UI and
			// dependent on the current name of the rank
			registerPermission(removePermName);
			removePerm = getPermission(removePermName);
		}
		return removePerm;
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
	public static PermissionType getListPermission(int id) {
		String listPermName = "listPlayer#" + id;
		PermissionType listPerm = PermissionType.getPermission(listPermName);
		if (listPerm == null) {
			// register type, because it was never used before, we do this with
			// the deprecated register without a description
			// because any further description is handled by the UI and
			// dependent on the current name of the rank
			registerPermission(listPermName);
			listPerm = getPermission(listPermName);
		}
		return listPerm;
	}

	/**
	 * Checks if the given PermissionType is a player type removal permission and if
	 * it is, it returns the id for which this permission allows removing members.
	 * If it is not a removal permission -1 will be returned
	 * 
	 * @param perm PermissionType to check
	 * @return removal id of the given perm or -1 if it isn't a removal perm
	 */
	public static int getRemovePermissionId(PermissionType perm) {
		if (perm.getDescription() != null) {
			return -1;
		}
		String[] parts = perm.getName().split("#");
		if (parts.length != 2) {
			return -1;
		}
		if (!parts[0].equals("removePlayer")) {
			return -1;
		}
		try {
			return Integer.parseInt(parts[1]);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * Checks if the given PermissionType is a player type invite permission and if
	 * it is, it returns the id for which this permission allows inviting members.
	 * If it is not an invite permission -1 will be returned
	 * 
	 * @param perm PermissionType to check
	 * @return invite id of the given perm or -1 if it isn't a invitation perm
	 */
	public static int getInvitePermissionId(PermissionType perm) {
		if (perm.getDescription() != null) {
			return -1;
		}
		String[] parts = perm.getName().split("#");
		if (parts.length != 2) {
			return -1;
		}
		if (!parts[0].equals("invitePlayer")) {
			return -1;
		}
		try {
			return Integer.parseInt(parts[1]);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * Checks if the given PermissionType is a player type list permission and if it
	 * is, it returns the id for which this permission allows listing members. If it
	 * is not a list permission -1 will be returned
	 * 
	 * @param perm PermissionType to check
	 * @return list id of the given perm or -1 if it isn't a listing perm
	 */
	public static int getListPermissionId(PermissionType perm) {
		if (perm.getDescription() != null) {
			return -1;
		}
		String[] parts = perm.getName().split("#");
		if (parts.length != 2) {
			return -1;
		}
		if (!parts[0].equals("listPlayer")) {
			return -1;
		}
		try {
			return Integer.parseInt(parts[1]);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * @return All existing permissions
	 */
	public static Collection<PermissionType> getAllPermissions() {
		return permissionByName.values();
	}

	/**
	 * Initializes all the permissions ranks use internally
	 */
	private static void registerRankPermissions() {
		for (int i = 0; i < GroupRankHandler.getMaximumTypeCount(); i++) {
			// a get call will ensure all of them are initiated both in cache and the
			// database
			getListPermission(i);
			getInvitePermission(i);
			getRemovePermission(i);
		}
	}

	private String name;
	private String registeringPlugin;
	private List<Integer> defaultPermLevels;
	private int id;
	private String description;

	private PermissionType(JavaPlugin registeringPlugin, String name, int id, List<Integer> defaultPermLevels,
			String description) {
		this.name = name;
		this.registeringPlugin = registeringPlugin.getName();
		this.id = id;
		this.defaultPermLevels = defaultPermLevels;
		this.description = description;
	}

	/**
	 * @return Name of this permission
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Name of the plugin which registered this permission
	 */
	public String getRegisteringPlugin() {
		return registeringPlugin;
	}

	/**
	 * List containing all player types, which will automatically get this
	 * permission when a new group is created. Player types are identified by id
	 * here: 1 = Admin, 2 = Mod, 3 = Member, 4 = DefaultNonMember, 5 = Blacklisted.
	 * 0, which is owner wont be in the list explicitly, but is implied to always
	 * have all permissions on group creation
	 * 
	 * @return All player type levels which get this permission by default
	 */
	public List<Integer> getDefaultPermLevels() {
		return defaultPermLevels;
	}

	/**
	 * @return Id of this permission
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return Description of this permission, which is displayed to players
	 */
	public String getDescription() {
		return description;
	}
}