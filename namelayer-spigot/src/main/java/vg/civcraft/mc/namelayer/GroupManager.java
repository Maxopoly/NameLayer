package vg.civcraft.mc.namelayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Consumer;

import com.google.common.base.Preconditions;

import vg.civcraft.mc.namelayer.database.GroupManagerDao;
import vg.civcraft.mc.namelayer.events.GroupCreateEvent;
import vg.civcraft.mc.namelayer.events.GroupLinkEvent;
import vg.civcraft.mc.namelayer.events.PostGroupMergeEvent;
import vg.civcraft.mc.namelayer.events.PreGroupMergeEvent;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.group.GroupLink;
import vg.civcraft.mc.namelayer.permission.PermissionType;
import vg.civcraft.mc.namelayer.permission.PlayerType;

public class GroupManager {

	private GroupManagerDao groupManagerDao;

	private Map<String, Group> groupsByName = new HashMap<>();
	private Map<Integer, Group> groupsById = new HashMap<>();
	private Map<UUID, Set<Group>> groupsByMember;

	private Set<Integer> undergoingMerge;

	public GroupManager(GroupManagerDao groupManagerDao, Map<String, Group> groupsByName,
			Map<Integer, Group> groupsById) {
		this.groupManagerDao = groupManagerDao;
		this.undergoingMerge = new TreeSet<>();
		this.groupsByMember = new HashMap<>();
		for (Group group : groupsById.values()) {
			for (UUID member : group.getAllMembers()) {
				Set<Group> groups = groupsByMember.computeIfAbsent(member, s -> new HashSet<>());
				groups.add(group);
			}
		}
	}
	
	
	public Set<Group> getGroupsForPlayer(UUID player) {
		return groupsByMember.get(player);
	}

	/**
	 * This will create a group asynchronously. Always saves to database. Pass in a
	 * Runnable of type RunnableOnGroup that specifies what to run
	 * <i>synchronously</i> after the insertion of the group. Your runnable should
	 * handle the case where id = -1 (failure).
	 * 
	 * Note that internally, we setGroupId on the RunnableOnGroup; your run() method
	 * should use getGroupId() to retrieve it and react to it.
	 * 
	 * @param group             the Group placeholder to use in creating a group.
	 *                          Calls GroupCreateEvent synchronously, then insert
	 *                          the group asynchronously, then calls the
	 *                          RunnableOnGroup synchronously.
	 * @param postCreate        The RunnableOnGroup to run after insertion (whether
	 *                          successful or not!)
	 * @param checkBeforeCreate Checks if the group already exists (asynchronously)
	 *                          prior to creating it. Runs the CreateEvent
	 *                          synchronously, then behaves as normal after that
	 *                          (running async create).
	 */
	public void createGroupAsync(String name, UUID creator, Consumer<Group> postCreate) {
		GroupCreateEvent event = new GroupCreateEvent(name, creator);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return;
		}
		Bukkit.getScheduler().runTaskAsynchronously(NameLayerPlugin.getInstance(), () -> {
			int groupId = groupManagerDao.createGroup(name);
			if (groupId == -1) {
				return;
			}
			// TODO TODO
			Group group = groupManagerDao.getGroup(groupId);
			Bukkit.getScheduler().runTask(NameLayerPlugin.getInstance(), () -> {
				postCreate.accept(group);
			});
		});

	}

	public void mergeGroup(final Group toKeep, final Group notToKeep) {
		Preconditions.checkNotNull(toKeep, "Group to keep may not be null");
		Preconditions.checkNotNull(notToKeep, "Group not to keep may not be null");
		if (toKeep.equals(notToKeep)) {
			return;
		}

		// TODO TODO
		// TODO check for any linking and cancel if any exists in group not to keep

		PreGroupMergeEvent event = new PreGroupMergeEvent(toKeep, notToKeep);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			NameLayerPlugin.getInstance().log(Level.INFO,
					"Group merge event was cancelled for groups: " + toKeep.getName() + " and " + notToKeep.getName());
			return;
		}
		undergoingMerge.add(toKeep.getGroupId());
		undergoingMerge.add(notToKeep.getGroupId());
		NameLayerPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(NameLayerPlugin.getInstance(),
				() -> {
					groupManagerDao.mergeGroup(toKeep, notToKeep);
					NameLayerPlugin.getInstance().getServer().getScheduler().runTask(NameLayerPlugin.getInstance(),
							() -> {
								PostGroupMergeEvent postEvent = new PostGroupMergeEvent(toKeep, notToKeep);
								Bukkit.getPluginManager().callEvent(postEvent);
							});
				});
	}
	
	public void deleteGroup(Group group) {
		
	}

	public Group getGroup(String name) {
		Preconditions.checkNotNull(name, "Group name needs to not be null");
		return groupsByName.get(name.toLowerCase());
	}

	public Group getGroup(int groupId) {
		return groupsById.get(groupId);
	}

	public boolean hasAccess(Group group, UUID player, PermissionType perm) {
		Player p = Bukkit.getPlayer(player);
		if (p != null && (p.isOp() || p.hasPermission("namelayer.admin"))) {
			return true;
		}
		if (group == null || perm == null) {
			NameLayerPlugin.getInstance().getLogger().log(Level.INFO, "hasAccess failed, caller passed in null",
					new Exception());
			return false;
		}
		return hasPlayerInheritsPerms(group, player, perm);
	}

	/**
	 * Checks if a player has a permission in a group or one of its parent groups
	 * 
	 * @param group  the group, and its parents etc to check
	 * @param player the player
	 * @param perm   the permission to check
	 * @return if the player has the specified permission in a group or one of its
	 *         parents
	 */
	private boolean hasPlayerInheritsPerms(Group group, UUID uuid, PermissionType perm) {
		PlayerType rank = group.getPlayerType(uuid);
		if (rank.hasPermission(perm)) {
			return true;
		}
		// check group links
		for (GroupLink link : group.getIncomingLinks()) {
			if (!link.getTargetType().hasPermission(perm)) {
				// link wouldn't grant enough permission
				continue;
			}
			Group originatingGroup = link.getOriginatingGroup();
			PlayerType rankInOgGroup = originatingGroup.getPlayerType(uuid);
			if (link.getOriginatingType().isEqualOrAbove(rankInOgGroup)) {
				return true;
			} else {
				if (checkUpwardsLinks(originatingGroup, uuid)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkUpwardsLinks(Group group, UUID player) {
		for (GroupLink link : group.getIncomingLinks()) {
			Group originatingGroup = link.getOriginatingGroup();
			PlayerType rankInOgGroup = originatingGroup.getPlayerType(player);
			if (link.getOriginatingType().isEqualOrAbove(rankInOgGroup)) {
				return true;
			} else {
				if (checkUpwardsLinks(originatingGroup, player)) {
					return true;
				}
			}
		}
		return false;
	}

	private void deleteGroupPerms(Group group) {
		if (group == null) {
			NameLayerPlugin.getInstance().getLogger().log(Level.INFO, "deleteGroupPerms failed, caller passed in null",
					new Exception());
			return;
		}
		permhandle.deletePerms(group);
	}

	public List<String> getAllGroupNames(UUID uuid) {
		if (uuid == null) {
			NameLayerPlugin.getInstance().getLogger().log(Level.INFO, "getAllGroupNames failed, caller passed in null",
					new Exception());
			return new ArrayList<>();
		}
		return groupManagerDao.getGroupNames(uuid);
	}

	/**
	 * Creates a new link if it would not create cycles, calls a GroupLinkEvent,
	 * insert the group link into the database und updates the cache accordingly
	 * 
	 * @param originating     Group the link is originating from
	 * @param originatingType Player type of the originating group whose members
	 *                        (and above) will inherit the target player type in the
	 *                        target group
	 * @param target          Target group the link is pointing towards
	 * @param targetType      PlayerType whose perms will be given to players in the
	 *                        originating type
	 * @return GroupLink created or null if creation was aborted
	 */
	public GroupLink linkGroups(Group originating, PlayerType originatingType, Group target, PlayerType targetType) {
		Queue<Group> toProcess = new LinkedList<>();
		Set<Group> alreadyLinked = new HashSet<>();
		for (GroupLink link : target.getOutgoingLinks()) {
			toProcess.add(link.getTargetGroup());
			alreadyLinked.add(link.getTargetGroup());
		}
		while (!toProcess.isEmpty()) {
			Group todo = toProcess.poll();
			for (GroupLink link : todo.getOutgoingLinks()) {
				toProcess.add(link.getTargetGroup());
				alreadyLinked.add(link.getTargetGroup());
			}
		}
		if (alreadyLinked.contains(originating)) {
			// would create a cycle
			return null;
		}
		GroupLinkEvent event = new GroupLinkEvent(originating, originatingType, target, targetType);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return null;
		}
		GroupLink link = new GroupLink(originating, originatingType, target, targetType);
		Bukkit.getScheduler().runTaskAsynchronously(NameLayerPlugin.getInstance(), () -> {
			groupManagerDao.addLink(link);
		});
		originating.addOutgoingLink(link);
		target.addIncomingLink(link);
		return link;
	}

	/**
	 * Removes a link from both the target and source group and deletes it from the
	 * database
	 * 
	 * @param link
	 */
	public void deleteGroupLink(GroupLink link) {
		link.getOriginatingGroup().removeOutgoingLink(link);
		link.getTargetGroup().removeIncomingLink(link);
		Bukkit.getScheduler().runTaskAsynchronously(NameLayerPlugin.getInstance(), () -> {
			groupManagerDao.removeLink(link);
		});
	}
}
