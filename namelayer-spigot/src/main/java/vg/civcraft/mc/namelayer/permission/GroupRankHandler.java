package vg.civcraft.mc.namelayer.permission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Bukkit;

import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.group.Group;

/**
 * The different ranks players can have in a group. Ranks can dynamically be
 * register, deleted and renamed. Each group has its own instance of this class
 */
public class GroupRankHandler {

	private Group group;
	private GroupRank root;
	private GroupRank defaultInvitationRank;
	private GroupRank defaultPasswordJoinRank;
	// storage in lookup map by name is only done in lower case
	private Map<String, GroupRank> ranksByName;
	private Map<Integer, GroupRank> ranksById;
	public static final int MAXIMUM_TYPE_COUNT = 27;
	public static final int OWNER_ID = 0;
	public static final int DEFAULT_ADMIN_ID = 1;
	public static final int DEFAULT_MOD_ID = 2;
	public static final int DEFAULT_MEMBER_ID = 3;
	private static final int DEFAULT_NON_MEMBER_ID = 4;

	public GroupRankHandler(GroupRank root, Group group) {
		this.root = root;
		this.group = group;
		this.ranksByName = new HashMap<>();
		this.ranksById = new TreeMap<>();
		putRank(root);
		for (GroupRank type : root.getChildren(true)) {
			putRank(type);
		}
	}

	/**
	 * Checks whether this instance has a rank with the given name
	 * 
	 * @param name Name to check for
	 * @return True if such a rank exists, false if not
	 */
	public boolean doesTypeExist(String name) {
		return ranksByName.get(name.toLowerCase()) != null;
	}

	/**
	 * @return Highest unused id available for this instance or -1 if no id is
	 *         available
	 */
	public int getUnusedId() {
		for (int i = 0; i < MAXIMUM_TYPE_COUNT; i++) {
			if (ranksById.get(i) == null) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @return The maximum amount of player types a group may have and also the
	 *         highest possible id a player type might have
	 */
	public static int getMaximumTypeCount() {
		return MAXIMUM_TYPE_COUNT;
	}

	/**
	 * Checks whether a given rank is a blacklist rank, which means it's an
	 * (indirect) child node of the default non member rank
	 * 
	 * @param type Rank to check
	 * @return True if the given rank is a blacklist type, false if not
	 */
	public boolean isBlacklistedRank(GroupRank type) {
		return isRelated(type, getDefaultNonMemberRank());
	}

	/**
	 * Checks whether a given rank is a member rank, which means its neither the
	 * default non member rank, nor a child of it
	 * 
	 * @param type Rank to check
	 * @return True if the given rank is a member type, false if not
	 */
	public boolean isMemberRank(GroupRank type) {
		return !isBlacklistedRank(type) && type != getDefaultNonMemberRank();
	}

	/**
	 * Retrieves a Rank by it's id
	 * 
	 * @param id
	 * @return Rank with that id or null if no such rank exists for this instance
	 */
	public GroupRank getRank(int id) {
		return ranksById.get(id);
	}

	/**
	 * Retrieves a rank by it's name
	 * 
	 * @param name
	 * @return Rank with that id or null if no such rank exists for this instance
	 */
	public GroupRank getRank(String name) {
		return ranksByName.get(name.toLowerCase());
	}

	/**
	 * @return Read-only collection of all ranks this instance is tracking,
	 *         including the default non-member rank
	 */
	public Collection<GroupRank> getAllRanks() {
		return Collections.unmodifiableCollection(ranksByName.values());
	}

	/**
	 * Each instance has an undeleteable rank, which is initially called
	 * "Owner" and will always be the root of the tree graph representing this
	 * instance's permission hierarchy. This rank will additionally always
	 * have the id 0.
	 * 
	 * @return Owner player type
	 */
	public GroupRank getOwnerRank() {
		return root;
	}

	/**
	 * Additionally to the owner-root type, there is a second non-deleteable type,
	 * the non-member type. By default any player will have this player type for any
	 * group, unless the player is a member of the group or explicitly blacklisted.
	 * This type is always a child node of the owner type and always has the id 4
	 * 
	 * @return Default-NonMember Player type
	 */
	public GroupRank getDefaultNonMemberRank() {
		return ranksById.get(DEFAULT_NON_MEMBER_ID);
	}

	/**
	 * When inviting new players to a group, the inviting player may chose to not
	 * explicitly specify a playertype as which the invitee is invited. If this is
	 * the case, the player will be invited as this player type. If this player type
	 * is not specified, inviting without naming a specific player type will not
	 * work
	 * 
	 * @return Default player type for invitation
	 */
	public GroupRank getDefaultInvitationRank() {
		return defaultInvitationRank;
	}

	/**
	 * If a player joins a group by password, no specific player type can be
	 * specified for him in particular, so anyone joining will be assigned this
	 * player type. If this player type is not specified, joining a group with a
	 * password is not possible
	 * 
	 * @return Default player type for anyone joining a group via password
	 */
	public GroupRank getDefaultPasswordJoinRank() {
		return defaultPasswordJoinRank;
	}

	/**
	 * Deletes the given player type from this instance. If this player type still
	 * has any children, they will all be deleted recursively
	 * 
	 * @param type    Player type to delete
	 * @param saveToD Whether the action should be persisted to the database and
	 *                broadcasted via Mercury
	 */
	public void deleteType(GroupRank type, boolean saveToD) {
		List<GroupRank> types = type.getChildren(true);
		// retrieving children deep is implemented as deep search, so deleting
		// nodes
		// in reverse is guaranteed to respect the tree structure and clean up
		// everything below the parent node
		for (int i = types.size() - 1; i >= 0; i--) {
			deleteType(types.get(i), saveToD);
		}
		if (type.getParent() != null) {
			type.getParent().removeChild(type);
		}
		PermissionType invPermission = PermissionType.getInvitePermission(type.getId());
		PermissionType remPermission = PermissionType.getRemovePermission(type.getId());
		PermissionType listPermission = PermissionType.getListPermission(type.getId());
		Map<GroupRank, List<PermissionType>> permsToRemove = new HashMap<>();
		for (GroupRank otherType : getAllRanks()) {
			List<PermissionType> perms = new LinkedList<>();
			if (otherType.hasPermission(invPermission)) {
				otherType.removePermission(invPermission, false);
				perms.add(invPermission);
			}
			if (otherType.hasPermission(remPermission)) {
				otherType.removePermission(remPermission, false);
				perms.add(remPermission);
			}
			if (otherType.hasPermission(listPermission)) {
				otherType.removePermission(listPermission, false);
				perms.add(listPermission);
			}
			if (!perms.isEmpty()) {
				permsToRemove.put(otherType, perms);
			}
		}
		if (defaultInvitationRank == type) {
			defaultInvitationRank = null;
		}
		if (defaultPasswordJoinRank == type) {
			defaultPasswordJoinRank = null;
		}
		ranksByName.remove(type.getName().toLowerCase());
		ranksById.remove(type.getId());
		if (saveToD) {
			NameLayerPlugin.getInstance().getGroupManagerDao().removePlayerType(group, type);
			NameLayerPlugin.getInstance().getGroupManagerDao().removeAllPermissions(group, permsToRemove);
		}
	}

	/**
	 * Registers the given rank for this instance. A new rank will
	 * always inherit all permissions of it's parent initially. Additionally all
	 * parents, meaning not only the direct parent, but all nodes above the newly
	 * created one get invite and remove permissions for this rank. The new
	 * rank will not inherit those permissions for itself
	 * 
	 * @param rank Rank to add
	 */
	public boolean createNewRank(GroupRank rank) {
		// we can always assume that the registered rank has a parent here,
		// because the root is created a different way and
		// all other nodes should have a parent
		if (rank == null || rank.getParent() == null || doesTypeExist(rank.getName())
				|| !doesTypeExist(rank.getParent().getName())) {
			return false;
		}
		PermissionType invPerm = PermissionType.getInvitePermission(rank.getId());
		PermissionType removePerm = PermissionType.getRemovePermission(rank.getId());
		PermissionType listPermission = PermissionType.getListPermission(rank.getId());
		Map<GroupRank, List<PermissionType>> permissionsToSave = new HashMap<>();
		// copy permissions from parent, we dont want to save the perm changes
		// to the db directly, because we will batch them
		for (PermissionType perm : rank.getParent().getAllPermissions()) {
			rank.addPermission(perm, false);
		}
		permissionsToSave.put(rank, rank.getParent().getAllPermissions());
		GroupRank parent = rank.getParent();
		while (parent != null) {
			if (!isMemberRank(parent)) {
				parent = parent.getParent();
				continue;
			}
			parent.addPermission(invPerm, false);
			parent.addPermission(removePerm, false);
			parent.addPermission(listPermission, false);
			permissionsToSave.put(parent, Arrays.asList(invPerm, removePerm, listPermission));
		}
		putRank(rank);
		Bukkit.getScheduler().runTaskAsynchronously(NameLayerPlugin.getInstance(), () -> {
			NameLayerPlugin.getInstance().getGroupManagerDao().registerPlayerType(group, rank);
			NameLayerPlugin.getInstance().getGroupManagerDao().addAllPermissions(group.getPrimaryId(),
					permissionsToSave);
		});
		return true;
	}

	/**
	 * This is used when loading a type directly from the database and bypasses the
	 * usual sanity checks and permission modifications. Never use this to create
	 * types that didnt already exist before
	 * 
	 * @param rank Type to add
	 */
	public void putRank(GroupRank rank) {
		ranksByName.put(rank.getName().toLowerCase(), rank);
		ranksById.put(rank.getId(), rank);
	}

	/**
	 * Checks whether the given PlayerTypes are related in the sense that the first
	 * one is an (indirect) child node of the second one. Important to avoid cycles.
	 * 
	 * @param child  Child node in the relation to check for
	 * @param parent Parent node in the relation to check for
	 * @return True if the first parameter is a child of the second one, false in
	 *         all other cases
	 */
	public boolean isRelated(GroupRank child, GroupRank parent) {
		GroupRank currentParent = child.getParent();
		while (currentParent != null) {
			if (currentParent.equals(parent)) {
				return true;
			}
			currentParent = currentParent.getParent();
		}
		return false;
	}

	/**
	 * Renames the given rank and updates it's name to the given one
	 * 
	 * @param rank      Rank to update
	 * @param name      New name for the player type
	 * @param writeToDb Whether the action should be persisted to the database
	 */
	public void renameType(GroupRank rank, String name, boolean writeToDb) {
		ranksByName.remove(rank.getName().toLowerCase());
		rank.setName(name);
		ranksByName.put(name.toLowerCase(), rank);
		if (writeToDb) {
			NameLayerPlugin.getInstance().getGroupManagerDao().updatePlayerTypeName(group, rank);
		}
	}

	/**
	 * Creates a set of standard permissions, which are assigned to any group when
	 * it is initially created. This roughly follows the permission schema NameLayer
	 * used to have when it's player types were completly static. The ranks
	 * created here are not saved to the database, because creating a default set of
	 * ranks on a database level is already done in the stored procedure for
	 * group creation. Make sure to also update this stored procedure when changing
	 * this method. Permissions set by this method aren't saved right away, but
	 * instead collected and batch saved at the end
	 * 
	 * @param g Group for which permissions should be created
	 * @return Completly initialized GroupRankHandler for new group
	 */
	public static GroupRankHandler createStandardTypes(Group g) {
		Map<GroupRank, List<PermissionType>> permsToSave = new HashMap<>();
		GroupRank owner = new GroupRank("Owner", OWNER_ID, null, g);
		GroupRankHandler handler = new GroupRankHandler(owner, g);
		GroupRank admin = new GroupRank("Admin", DEFAULT_ADMIN_ID, owner, g);
		handler.putRank(admin);
		GroupRank mod = new GroupRank("Mod", DEFAULT_MOD_ID, admin, g);
		handler.putRank(mod);
		GroupRank member = new GroupRank("Member", DEFAULT_MEMBER_ID, mod, g);
		handler.putRank(member);
		GroupRank defaultNonMember = new GroupRank("Default", DEFAULT_NON_MEMBER_ID, owner, g);
		handler.putRank(defaultNonMember);
		GroupRank blacklisted = new GroupRank("Blacklisted", 5, defaultNonMember, g);
		handler.putRank(blacklisted);
		for (GroupRank type : handler.getAllRanks()) {
			if (type == owner) {
				continue;
			}
			List<PermissionType> permList = new ArrayList<>();
			for (PermissionType perm : PermissionType.getAllPermissions()) {
				if (perm.getDefaultPermLevels().getAllowedRankIds().contains(type.getId())) {
					type.addPermission(perm, false);
					permList.add(perm);
				}
			}
			permsToSave.put(type, permList);
		}
		handler.defaultInvitationRank = member;
		handler.defaultPasswordJoinRank = member;
		Bukkit.getScheduler().runTaskAsynchronously(NameLayerPlugin.getInstance(), () -> NameLayerPlugin.getInstance()
				.getGroupManagerDao().addAllPermissions(g.getPrimaryId(), permsToSave));
		return handler;
	}
}