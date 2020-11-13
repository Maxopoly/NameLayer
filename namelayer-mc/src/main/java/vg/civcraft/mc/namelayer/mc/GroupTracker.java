package vg.civcraft.mc.namelayer.mc;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.Consumer;

import com.google.common.base.Preconditions;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import vg.civcraft.mc.civmodcore.command.MailBoxAPI;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupLink;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.PermissionType;

public class GroupTracker {

	private static final String hiddenDeletedGroupName = "Name_Layer_Special";

	private GroupTracker groupManagerDao;

	private Map<String, Group> groupsByName;
	private Map<Integer, Group> groupsById;
	private Map<UUID, Set<Group>> groupsByMember;

	public GroupTracker() {
		this.groupsById = new HashMap<>();
		this.groupsByName = new HashMap<>();
		this.groupsByMember = new HashMap<>();
	}

	public Set<Group> getGroupsForPlayer(UUID player) {
		return Collections.unmodifiableSet(groupsByMember.computeIfAbsent(player, s ->new HashSet<>()));
	}

	public void renameGroup(Group group, String newName) {
		groupsByName.remove(group.getName().toLowerCase());
		group.setName(newName);
		groupsByName.put(newName.toLowerCase(), group);
	}

	public void invitePlayer(UUID inviterUUID, UUID toInvite, GroupRank rank, Group group) {
		Player player = Bukkit.getPlayer(toInvite);
		if (NameLayerPlugin.getInstance().getSettingsManager().getAutoAcceptInvites().getValue(toInvite)) {
			if (player != null) {
				player.sendMessage(ChatColor.GREEN + "You have auto-accepted an invite to " + group.getColoredName()
						+ ChatColor.GREEN + " from " + ChatColor.YELLOW + NameAPI.getName(inviterUUID));
			} else {
				MailBoxAPI.addMail(toInvite,
						ChatColor.GREEN + "While gone you auto accepted an invite to " + group.getColoredName()
								+ ChatColor.GREEN + " from " + ChatColor.YELLOW + NameAPI.getName(inviterUUID));
			}
			group.getActionLog().addAction(new AcceptInvitation(System.currentTimeMillis(), toInvite, rank.getName()),
					true);
			addPlayerToGroup(group, toInvite, rank, true);
		} else {
			if (player != null) {
				TextComponent message = new TextComponent(ChatColor.GREEN + "You have been invited to the group "
						+ group.getColoredName() + ChatColor.GREEN + " by " + ChatColor.YELLOW
						+ NameAPI.getCurrentName(inviterUUID) + ChatColor.GREEN + ".\nClick this message to accept.");
				message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nlag " + group.getName()));
				message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						new ComponentBuilder("  ---  Click to accept").create()));
				player.spigot().sendMessage(message);
			}
			group.addInvite(toInvite, rank);
		}
	}
	
	public void addGroup(Group group) {
		groupsById.put(group.getPrimaryId(), group);
		for(int id : group.getSecondaryIds()) {
			groupsById.put(id, group);
		}
		groupsByName.put(group.getName().toLowerCase(), group);
		for(UUID uuid : group.getAllTracked()) {
			groupsByMember.computeIfAbsent(uuid, u -> new HashSet<>()).add(group);
		}
	}

	public void addPlayerToGroup(Group group, UUID player, GroupRank rank) {
		Set<Group> groups = groupsByMember.computeIfAbsent(player, u -> new HashSet<>());
		groups.add(group);
		group.addToTracking(player, rank);
	}

	public void updatePlayerRankInGroup(Group group, UUID player, GroupRank rank) {
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
		Player p = Bukkit.getPlayer(player);
		if (p != null && (p.isOp() || p.hasPermission("namelayer.admin"))) {
			return true;
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
		GroupRank rank = group.getRank(uuid);
		if (rank == group.getGroupRankHandler().getOwnerRank() || rank.hasPermission(perm)) {
			return true;
		}
		// check group links
		for (GroupLink link : group.getIncomingLinks()) {
			if (!link.getTargetType().hasPermission(perm)) {
				// link wouldn't grant enough permission
				continue;
			}
			Group originatingGroup = link.getOriginatingGroup();
			GroupRank rankInOgGroup = originatingGroup.getRank(uuid);
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

	private static boolean checkUpwardsLinks(Group group, UUID player) {
		for (GroupLink link : group.getIncomingLinks()) {
			Group originatingGroup = link.getOriginatingGroup();
			GroupRank rankInOgGroup = originatingGroup.getRank(player);
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

