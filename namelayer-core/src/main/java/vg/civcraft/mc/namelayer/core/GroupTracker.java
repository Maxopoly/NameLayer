package vg.civcraft.mc.namelayer.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.common.base.Preconditions;

public class GroupTracker {

	private final Map<String, Group> groupsByName;
	private final Map<Integer, Group> groupsById;
	private final Map<UUID, Set<Group>> groupsByMember;
	private final Map<UUID, Set<Group>> invitesByPlayer;
	protected final PermissionTracker permissionTracker;

	public GroupTracker() {
		this.groupsById = new HashMap<>();
		this.groupsByName = new HashMap<>();
		this.groupsByMember = new HashMap<>();
		this.invitesByPlayer = new HashMap<>();
		this.permissionTracker = new PermissionTracker();
	}
	
	public PermissionTracker getPermissionTracker() {
		return permissionTracker;
	}
	
	public void registerPermission(PermissionType perm) {
		permissionTracker.putPermission(perm);
	}

	public Set<Group> getGroupsForPlayer(UUID player) {
		return Collections.unmodifiableSet(groupsByMember.computeIfAbsent(player, s -> new HashSet<>()));
	}
	
	public Set<Group> getInvitesForPlayer(UUID player) {
		return Collections.unmodifiableSet(invitesByPlayer.computeIfAbsent(player, s -> new HashSet<>()));
	}

	public void renameGroup(Group group, String newName) {
		groupsByName.remove(group.getName().toLowerCase());
		group.setName(newName);
		groupsByName.put(newName.toLowerCase(), group);
	}

	public void addInvite(UUID toInvite, GroupRank rank, Group group) {
		group.addInvite(toInvite, rank);
	}
	
	public void addPermissionToRank(Group group, GroupRank rank, PermissionType permission) {
		rank.addPermission(permission);
	}
	
	public void removePermissionFromRank(Group group, GroupRank rank, PermissionType permission) {
		rank.removePermission(permission);
	}
	
	public void renameRank(Group group, GroupRank rank, String newName) {
		group.getGroupRankHandler().renameRank(rank,newName);
	}
	
	public void setMetaDataValue(Group group, String key, String value) {
		group.setMetaData(key, value);
	}

	public void addGroup(Group group) {
		groupsById.put(group.getPrimaryId(), group);
		for (int id : group.getSecondaryIds()) {
			groupsById.put(id, group);
		}
		groupsByName.put(group.getName().toLowerCase(), group);
		for (UUID uuid : group.getAllTracked()) {
			groupsByMember.computeIfAbsent(uuid, u -> new HashSet<>()).add(group);
		}
	}

	public void addPlayerToGroup(Group group, UUID player, GroupRank rank) {
		if (group.isTracked(player)) {
			throw new IllegalGroupStateException();
		}
		Set<Group> groups = groupsByMember.computeIfAbsent(player, u -> new HashSet<>());
		groups.add(group);
		group.addToTracking(player, rank);
	}

	public void updatePlayerRankInGroup(Group group, UUID player, GroupRank rank) {
		if (!group.isTracked(player)) {
			throw new IllegalGroupStateException();
		}
		group.updateTracking(player, rank);
	}

	public void removePlayerFromGroup(Group group, UUID player) {
		Set<Group> groups = groupsByMember.computeIfAbsent(player, u -> new HashSet<>());
		groups.remove(group);
		group.removeFromTracking(player);
	}

	public void deleteInvite(Group group, UUID player) {
		group.removeInvite(player);
	}

	public void deleteGroup(Group group) {
		groupsByName.remove(group.getName().toLowerCase());
		groupsById.remove(group.getPrimaryId());
		for(int id : group.getSecondaryIds()) {
			groupsById.remove(id);
		}
		for(UUID member : group.getAllTracked()) {
			Set<Group> perPlayer = groupsByMember.get(member);
			if (perPlayer != null) {
				perPlayer.remove(group);
			}
		}

	}
	
	public void deleteRank(Group group, GroupRank rank) {
		List<GroupRank> children = rank.getChildren(true);
		if (!children.isEmpty()) {
			throw new IllegalGroupStateException();
		}
		if (!group.getAllTrackedByType(rank).isEmpty()) {
			throw new IllegalGroupStateException();
		}
		group.getGroupRankHandler().deleteRank(rank);
	}

	public Group getGroup(String name) {
		Preconditions.checkNotNull(name, "Group name needs to not be null");
		return groupsByName.get(name.toLowerCase());
	}

	public Group getGroup(int groupId) {
		return groupsById.get(groupId);
	}

	public boolean hasAccess(Group group, UUID player, PermissionType perm) {
		Preconditions.checkNotNull(group, "Group may not be null");
		Preconditions.checkNotNull(player, "Player may not be null");
		Preconditions.checkNotNull(perm, "Permission may not be null");
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
		GroupRank rank = group.getRank(uuid);
		if (rank == group.getGroupRankHandler().getOwnerRank() || rank.hasPermission(perm)) {
			return true;
		}
		// check group links
		for (GroupLink link : group.getIncomingLinks()) {
			if (!link.getTargetRank().hasPermission(perm)) {
				// link wouldn't grant enough permission
				continue;
			}
			Group originatingGroup = link.getOriginatingGroup();
			GroupRank rankInOgGroup = originatingGroup.getRank(uuid);
			if (link.getOriginatingRank().isEqualOrAbove(rankInOgGroup)) {
				return true;
			} else {
				if (checkUpwardsLinks(originatingGroup, uuid)) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean checkUpwardsLinks(Group group, UUID player) {
		for (GroupLink link : group.getIncomingLinks()) {
			Group originatingGroup = link.getOriginatingGroup();
			GroupRank rankInOgGroup = originatingGroup.getRank(player);
			if (link.getOriginatingRank().isEqualOrAbove(rankInOgGroup)) {
				return true;
			} else {
				if (checkUpwardsLinks(originatingGroup, player)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Creates a new link
	 * 
	 * @param originating     Group the link is originating from
	 * @param originatingType Player type of the originating group whose members
	 *                        (and above) will inherit the target player type in the
	 *                        target group
	 * @param target          Target group the link is pointing towards
	 * @param targetType      PlayerType whose perms will be given to players in the
	 *                        originating type
	 * @return GroupLink created
	 */
	public GroupLink linkGroups(Group originating, GroupRank originatingType, Group target, GroupRank targetType) {
		GroupLink link = new GroupLink(originating, originatingType, target, targetType);
		originating.addOutgoingLink(link);
		target.addIncomingLink(link);
		return link;
	}

	/**
	 * Removes a link from both the target and source group
	 * 
	 * @param link Link to remove
	 */
	public void deleteGroupLink(GroupLink link) {
		link.getOriginatingGroup().removeOutgoingLink(link);
		link.getTargetGroup().removeIncomingLink(link);
	}
}
