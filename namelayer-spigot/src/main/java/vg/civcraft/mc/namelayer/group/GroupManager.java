package vg.civcraft.mc.namelayer.group;

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
import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.events.GroupCreateEvent;
import vg.civcraft.mc.namelayer.events.GroupLinkEvent;
import vg.civcraft.mc.namelayer.events.PostGroupMergeEvent;
import vg.civcraft.mc.namelayer.events.PreGroupMergeEvent;
import vg.civcraft.mc.namelayer.group.log.impl.AcceptInvitation;
import vg.civcraft.mc.namelayer.permission.GroupRank;
import vg.civcraft.mc.namelayer.permission.GroupRankHandler;
import vg.civcraft.mc.namelayer.permission.PermissionType;
import vg.civcraft.mc.namelayer.zeus.GroupManagerDao;

public class GroupManager {

	private static final String hiddenDeletedGroupName = "Name_Layer_Special";

	private GroupManagerDao groupManagerDao;

	private Map<String, Group> groupsByName;
	private Map<Integer, Group> groupsById;
	private Map<UUID, Set<Group>> groupsByMember;
	private Set<Integer> undergoingMerge;

	public GroupManager(GroupManagerDao groupManagerDao, Map<String, Group> groupsByName,
			Map<Integer, Group> groupsById) {
		this.groupManagerDao = groupManagerDao;
		this.groupsById = groupsById;
		this.groupsByName = groupsByName;
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
		return Collections.unmodifiableSet(groupsByMember.computeIfAbsent(player, s ->new HashSet<>()));
	}

	public void renameGroup(Group group, String newName) {
		groupsByName.remove(group.getName().toLowerCase());
		String oldName = group.getName();
		group.setName(newName);
		groupsByName.put(newName.toLowerCase(), group);
		groupManagerDao.renameGroup(oldName, newName);
	}

	public void invitePlayer(UUID inviterUUID, UUID toInvite, GroupRank rank, Group group) {
		Player player = Bukkit.getPlayer(toInvite);
		if (NameLayerPlugin.getInstance().getSettingsManager().getAutoAcceptInvites().getValue(toInvite)) {
			if (player != null) {
				player.sendMessage(ChatColor.GREEN + "You have auto-accepted an invite to " + group.getColoredName()
						+ ChatColor.GREEN + " from " + ChatColor.YELLOW + NameAPI.getCurrentName(inviterUUID));
			} else {
				MailBoxAPI.addMail(toInvite,
						ChatColor.GREEN + "While gone you auto accepted an invite to " + group.getColoredName()
								+ ChatColor.GREEN + " from " + ChatColor.YELLOW + NameAPI.getCurrentName(inviterUUID));
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
			Bukkit.getScheduler().runTaskAsynchronously(NameLayerPlugin.getInstance(),
					() -> groupManagerDao.addGroupInvitation(toInvite, group, rank));
		}
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
			int groupId = groupManagerDao.createGroup(name, creator);
			if (groupId == -1) {
				return;
			}
			Group group = new Group(name, groupId);
			groupsByName.put(name.toLowerCase(), group);
			groupsById.put(groupId, group);
			group.setGroupRankHandler(GroupRankHandler.createStandardTypes(group));
			addPlayerToGroup(group, creator, group.getGroupRankHandler().getOwnerRank(), false);
			// force instanciation of meta data time stamp
			NameLayerPlugin.getInstance().getNameLayerMeta().getMetaData(group);
			if (postCreate != null) {
				Bukkit.getScheduler().runTask(NameLayerPlugin.getInstance(), () -> {
					postCreate.accept(group);
				});
			}
		});
	}

	public void addPlayerToGroup(Group group, UUID player, GroupRank rank, boolean saveToDatabase) {
		Set<Group> groups = groupsByMember.computeIfAbsent(player, u -> new HashSet<>());
		groups.add(group);
		group.addToTracking(player, rank);
		if (saveToDatabase) {
			Bukkit.getScheduler().runTaskAsynchronously(NameLayerPlugin.getInstance(),
					() -> groupManagerDao.addMember(player, group, rank));
		}
	}

	public void updatePlayerRankInGroup(Group group, UUID player, GroupRank rank) {
		group.updateTracking(player, rank);
		Bukkit.getScheduler().runTaskAsynchronously(NameLayerPlugin.getInstance(), () -> {
			groupManagerDao.updateMember(player, group, rank);
		});
	}

	public void removePlayerFromGroup(Group group, UUID player) {
		Set<Group> groups = groupsByMember.computeIfAbsent(player, u -> new HashSet<>());
		groups.remove(group);
		group.removeFromTracking(player);
		Bukkit.getScheduler().runTaskAsynchronously(NameLayerPlugin.getInstance(), () -> {
			groupManagerDao.removeMember(player, group);
		});
	}

	public void deleteInvite(Group group, UUID player) {
		group.removeInvite(player);
		Bukkit.getScheduler().runTaskAsynchronously(NameLayerPlugin.getInstance(),
				() -> groupManagerDao.removeGroupInvitation(player, group));
	}

	public void mergeGroup(final Group toKeep, final Group notToKeep) {
		Preconditions.checkNotNull(toKeep, "Group to keep may not be null");
		Preconditions.checkNotNull(notToKeep, "Group not to keep may not be null");
		if (toKeep.equals(notToKeep)) {
			return;
		}

		if (undergoingMerge.contains(toKeep.getPrimaryId()) || undergoingMerge.contains(notToKeep.getPrimaryId())) {
			return;
		}
		PreGroupMergeEvent event = new PreGroupMergeEvent(toKeep, notToKeep);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			NameLayerPlugin.getInstance().getLogger().log(Level.INFO,
					"Group merge event was cancelled for groups: " + toKeep.getName() + " and " + notToKeep.getName());
			return;
		}
		undergoingMerge.add(toKeep.getPrimaryId());
		undergoingMerge.add(notToKeep.getPrimaryId());
		NameLayerPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(NameLayerPlugin.getInstance(),
				() -> {
					groupManagerDao.mergeGroup(toKeep, notToKeep);
					NameLayerPlugin.getInstance().getServer().getScheduler().runTask(NameLayerPlugin.getInstance(),
							() -> {
								PostGroupMergeEvent postEvent = new PostGroupMergeEvent(toKeep, notToKeep);
								Bukkit.getPluginManager().callEvent(postEvent);
								undergoingMerge.remove(toKeep.getPrimaryId());
								undergoingMerge.remove(notToKeep.getPrimaryId());
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
	public GroupLink linkGroups(Group originating, GroupRank originatingType, Group target, GroupRank targetType) {
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
