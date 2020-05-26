package vg.civcraft.mc.namelayer.permission;

import java.util.Collection;
import java.util.HashMap;
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

	private static final String LIST_RANK_PREFIX = "listPlayer#";
	private static final String INVITE_RANK_PREFIX = "invitePlayer#";
	private static final String REMOVE_RANK_PREFIX = "removePlayer#";

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
	public static PermissionType registerPermission(JavaPlugin registeringPlugin, String name,
			DefaultPermissionLevel defaultPermLevel, String description) {
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
		PermissionType perm;
		if (id == -1) {
			// not in db yet
			id = maximumExistingId + 1;
			while (dbRegisteredPerms.get(id) != null) {
				id++;
			}
			maximumExistingId = id;
			perm = new PermissionType(registeringPlugin, name, id, defaultPermLevel, description);
			NameLayerPlugin.getInstance().getGroupManagerDao().registerPermission(perm);
		} else {
			// already in db, so use existing id
			perm = new PermissionType(registeringPlugin, name, id, defaultPermLevel, description);
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
		String invitePermName = INVITE_RANK_PREFIX + id;
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
		String removePermName = REMOVE_RANK_PREFIX + id;
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
		String listPermName = LIST_RANK_PREFIX + id;
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
	 * @return All existing permissions
	 */
	public static Collection<PermissionType> getAllPermissions() {
		return permissionByName.values();
	}

	/**
	 * Initializes all the permissions ranks use internally
	 */
	private static void registerRankPermissions() {
		NameLayerPlugin plugin = NameLayerPlugin.getInstance();
		// list admins
		registerPermission(plugin, LIST_RANK_PREFIX + GroupRankHandler.DEFAULT_ADMIN_ID, DefaultPermissionLevel.ADMIN,
				"");
		// list mods
		registerPermission(plugin, LIST_RANK_PREFIX + GroupRankHandler.DEFAULT_MOD_ID, DefaultPermissionLevel.ADMIN,
				"");
		// invite mods
		registerPermission(plugin, INVITE_RANK_PREFIX + GroupRankHandler.DEFAULT_MOD_ID, DefaultPermissionLevel.ADMIN,
				"");
		// remove mods
		registerPermission(plugin, REMOVE_RANK_PREFIX + GroupRankHandler.DEFAULT_MOD_ID, DefaultPermissionLevel.ADMIN,
				"");
		// list members
		registerPermission(plugin, LIST_RANK_PREFIX + GroupRankHandler.DEFAULT_MEMBER_ID, DefaultPermissionLevel.MOD,
				"");
		// invite members
		registerPermission(plugin, INVITE_RANK_PREFIX + GroupRankHandler.DEFAULT_MEMBER_ID, DefaultPermissionLevel.MOD,
				"");
		// remove members
		registerPermission(plugin, REMOVE_RANK_PREFIX + GroupRankHandler.DEFAULT_MEMBER_ID, DefaultPermissionLevel.MOD,
				"");
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
	private DefaultPermissionLevel defaultPermLevels;
	private int id;
	private String description;

	private PermissionType(JavaPlugin registeringPlugin, String name, int id, DefaultPermissionLevel defaultPermLevels,
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
	 * 
	 * @return Minimum permission level which will get this permission by default
	 */
	public DefaultPermissionLevel getDefaultPermLevels() {
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