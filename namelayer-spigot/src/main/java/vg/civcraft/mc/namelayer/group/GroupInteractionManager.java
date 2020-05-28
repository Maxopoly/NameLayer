package vg.civcraft.mc.namelayer.group;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;

import org.bukkit.ChatColor;

import vg.civcraft.mc.namelayer.GroupAPI;
import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.group.log.impl.AcceptInvitation;
import vg.civcraft.mc.namelayer.group.log.impl.AddLink;
import vg.civcraft.mc.namelayer.group.log.impl.AddPermission;
import vg.civcraft.mc.namelayer.group.log.impl.BlacklistPlayer;
import vg.civcraft.mc.namelayer.group.log.impl.ChangeGroupName;
import vg.civcraft.mc.namelayer.group.log.impl.ChangeMemberRank;
import vg.civcraft.mc.namelayer.group.log.impl.ChangeRankName;
import vg.civcraft.mc.namelayer.group.log.impl.CreateGroup;
import vg.civcraft.mc.namelayer.group.log.impl.CreateRank;
import vg.civcraft.mc.namelayer.group.log.impl.DeleteRank;
import vg.civcraft.mc.namelayer.group.log.impl.InviteMember;
import vg.civcraft.mc.namelayer.group.log.impl.JoinGroup;
import vg.civcraft.mc.namelayer.group.log.impl.LeaveGroup;
import vg.civcraft.mc.namelayer.group.log.impl.MergeGroup;
import vg.civcraft.mc.namelayer.group.log.impl.RejectInvite;
import vg.civcraft.mc.namelayer.group.log.impl.RemoveLink;
import vg.civcraft.mc.namelayer.group.log.impl.RemoveMember;
import vg.civcraft.mc.namelayer.group.log.impl.RemovePermission;
import vg.civcraft.mc.namelayer.group.log.impl.RevokeInvite;
import vg.civcraft.mc.namelayer.group.log.impl.SetPassword;
import vg.civcraft.mc.namelayer.group.meta.GroupMetaDataView;
import vg.civcraft.mc.namelayer.listeners.PlayerListener;
import vg.civcraft.mc.namelayer.misc.NameLayerSettingManager;
import vg.civcraft.mc.namelayer.permission.GroupRank;
import vg.civcraft.mc.namelayer.permission.GroupRankHandler;
import vg.civcraft.mc.namelayer.permission.NameLayerPermissionManager;
import vg.civcraft.mc.namelayer.permission.PermissionType;

public class GroupInteractionManager {

	private GroupManager groupMan;
	private NameLayerPermissionManager permManager;
	private NameLayerSettingManager settingManager;
	private GroupMetaDataView<NameLayerMetaData> metaDataManager;

	public GroupInteractionManager(GroupManager groupManager, NameLayerPermissionManager permissionManager,
			NameLayerSettingManager settingsManager, GroupMetaDataView<NameLayerMetaData> metaDataManager) {
		this.groupMan = groupManager;
		this.permManager = permissionManager;
		this.settingManager = settingsManager;
		this.metaDataManager = metaDataManager;
	}

	public boolean acceptInvite(UUID executor, String groupName, Consumer<String> callback) {
		Group group = getGroup(groupName, callback);
		if (group == null) {
			return false;
		}
		GroupRank type = group.getInvite(executor);
		if (type == null) {
			callback.accept(String.format("%sYou were not invited to %s", ChatColor.RED, group.getColoredName()));
			return false;
		}
		if (group.isMember(executor)) {
			callback.accept(ChatColor.RED + "You are already a member or blacklisted you cannot join again.");
			groupMan.deleteInvite(group, executor);
			return false;
		}
		groupMan.addPlayerToGroup(group, executor, type, true);
		groupMan.deleteInvite(group, executor);
		group.getActionLog().addAction(new AcceptInvitation(System.currentTimeMillis(), executor, type.getName()),
				true);
		PlayerListener.removeNotification(executor, group);
		callback.accept(String.format("%sYou have been added to %s%s as a %s%s", ChatColor.GREEN,
				group.getColoredName(), ChatColor.GREEN, ChatColor.YELLOW, type.getName()));
		return true;
	}

	public boolean blacklistPlayer(UUID executor, String groupName, String targetPlayer, String rank,
			Consumer<String> callback) {
		Group group = getGroup(groupName, callback);
		if (group == null) {
			return false;
		}
		UUID toAdd = getPlayer(targetPlayer, callback);
		if (toAdd == null) {
			return false;
		}
		GroupRankHandler handler = group.getGroupRankHandler();
		GroupRank targetType = rank != null ? handler.getType(rank) : handler.getDefaultInvitationType();
		// this is designed to not reveal any names of player types to the outside
		if (targetType == null) {
			callback.accept(
					ChatColor.RED + "The rank you entered did not exist or you do not permission to blacklist on it");
			return false;
		}
		PermissionType permRequired = targetType.getInvitePermissionType();
		if (!groupMan.hasAccess(group, executor, permRequired)) {
			callback.accept(
					ChatColor.RED + "The rank you entered did not exist or you do not permission to blacklist on it");
			return false;
		}
		if (!handler.isBlackListedType(targetType)) {
			reply(callback, "%s%s%s is not a blacklist rank in %s", ChatColor.YELLOW, targetType.getName(),
					ChatColor.RED, group.getColoredName());
			return false;
		}
		GroupRank currentRank = group.getRank(toAdd);
		if (currentRank != handler.getDefaultNonMemberType()) {
			if (!groupMan.hasAccess(group, executor, currentRank.getRemovalPermissionType())) {
				callback.accept(String.format(
						"%sThe player %s%s%s is already tracked for %s%s and you do not have permission to remove them from their current rank",
						ChatColor.RED, ChatColor.YELLOW, NameAPI.getCurrentName(toAdd), ChatColor.RED,
						group.getColoredName(), ChatColor.RED));
				return false;
			}
			callback.accept(String.format("%s%s%s had their rank changed from %s%s%s to %s%s%s in %s", ChatColor.YELLOW,
					NameAPI.getCurrentName(toAdd), ChatColor.GREEN, ChatColor.GOLD, currentRank.getName(),
					ChatColor.GREEN, ChatColor.GOLD, targetType.getName(), ChatColor.GREEN, group.getColoredName()));
			group.getActionLog().addAction(new ChangeMemberRank(System.currentTimeMillis(), executor,
					targetType.getName(), toAdd, currentRank.getName()), true);
			groupMan.updatePlayerRankInGroup(group, toAdd, targetType);
			return true;
		}
		callback.accept(String.format("%s%s %shas been blacklisted as %s%s%s in %s", ChatColor.YELLOW,
				NameAPI.getCurrentName(toAdd), ChatColor.GREEN, ChatColor.YELLOW, targetType.getName(),
				ChatColor.YELLOW, group.getColoredName()));
		group.getActionLog().addAction(
				new BlacklistPlayer(System.currentTimeMillis(), executor, targetType.getName(), toAdd), true);
		groupMan.addPlayerToGroup(group, toAdd, targetType, true);
		return true;
	}

	public boolean createGroup(UUID executor, String groupName, Consumer<String> callback) {
		// enforce regulations on the name
		if (!isConformName(groupName, callback)) {
			return false;
		}
		Group existing = groupMan.getGroup(groupName);
		if (existing != null) {
			callback.accept(String.format("%sThe group %s%s already exists", ChatColor.RED, existing.getColoredName(),
					ChatColor.RED));
			return false;
		}
		NameLayerPlugin.getInstance().getGroupManager().createGroupAsync(groupName, executor, g -> {
			if (g == null) {
				callback.accept(ChatColor.RED + "The group name is already taken or creation failed.");
				return;
			}
			callback.accept(String.format("%sThe group %s was successfully created", ChatColor.GREEN, g.getName()));
			NameLayerPlugin.getInstance().getLogger().log(Level.INFO,
					"Group " + g.getName() + " was created by " + executor);
			g.getActionLog().addAction(new CreateGroup(System.currentTimeMillis(), executor, g.getName()), true);
		});
		return true;
	}

	public boolean createRank(UUID executor, String groupName, String name, String parentName,
			Consumer<String> callback) {
		Group group = getGroup(groupName, callback);
		if (group == null) {
			return false;
		}
		if (!checkPermission(group, executor, permManager.getCreatePlayerType(), callback)) {
			return false;
		}
		GroupRankHandler typeHandler = group.getGroupRankHandler();
		GroupRank parent = typeHandler.getType(parentName);
		if (parent == null) {
			reply(callback, "%sThe rank %s%s%s does not exist", ChatColor.RED, ChatColor.YELLOW, parentName,
					ChatColor.RED);
			return false;
		}
		if (!isConformName(name, callback)) {
			return false;
		}
		if (typeHandler.getType(name) != null) {
			reply(callback, "%sA rank named %s%s%s already exists", ChatColor.RED, ChatColor.YELLOW, parentName,
					ChatColor.RED);
			return false;
		}
		int id = typeHandler.getUnusedId();
		if (id == -1) {
			reply(callback,
					"%sYou have reached the maximum amount of ranks (%d). You'll have to delete some before creating new ones",
					ChatColor.RED, GroupRankHandler.MAXIMUM_TYPE_COUNT);
			return false;
		}
		GroupRank added = new GroupRank(name, id, parent, group);
		reply(callback, "%sSuccessfully added %s%s%s as sub rank of %s%s", ChatColor.GREEN, ChatColor.GOLD, name,
				ChatColor.GREEN, ChatColor.YELLOW, parent.getName());
		typeHandler.createNewType(added);
		group.getActionLog().addAction(new CreateRank(System.currentTimeMillis(), executor, name, parent.getName()),
				true);
		return true;
	}

	public boolean deleteGroup(UUID executor, String groupName, Consumer<String> callback) {
		Group group = getGroup(groupName, callback);
		if (group == null) {
			return false;
		}
		if (!checkPermission(group, executor, permManager.getDeleteGroup(), callback)) {
			return false;
		}
		groupMan.deleteGroup(group);
		callback.accept(String.format("%s%s has been deleted", ChatColor.GREEN, group.getName()));
		return true;
	}

	public boolean deleteRank(UUID executor, String groupName, String rankName, Consumer<String> callback) {
		Group group = getGroup(groupName, callback);
		if (group == null) {
			return false;
		}
		if (!checkPermission(group, executor, permManager.getDeletePlayerType(), callback)) {
			return false;
		}
		GroupRank rank = getPlayerType(group, rankName, callback);
		if (rank == null) {
			return false;
		}
		if (!rank.getChildren(false).isEmpty()) {
			reply(callback, "%sThe rank %s%s%s has children and can not be deleted before its children are deleted",
					ChatColor.RED, ChatColor.YELLOW, rank.getName(), ChatColor.RED);
			return false;
		}
		GroupRankHandler rankHandler = group.getGroupRankHandler();
		if (rank == rankHandler.getDefaultNonMemberType()) {
			reply(callback, "%sYou can not delete the default type for non-members", ChatColor.RED);
			return false;
		}
		if (rank == rankHandler.getOwnerType()) {
			// can happen through inherited access
			reply(callback,
					"%sYou can not delete the rank %s%s%s of the group %s%s, because it is the last remaining rank",
					ChatColor.RED, ChatColor.GOLD, rank.getName(), ChatColor.RED, group.getColoredName(),
					ChatColor.RED);
			return false;
		}
		Set<UUID> members = group.getAllTrackedByType(rank);
		if (!members.isEmpty()) {
			reply(callback,
					"%sThe rank %s%s%s still has %d members and can not be deleted until all of them have been removed",
					ChatColor.RED, ChatColor.YELLOW, rank.getName(), ChatColor.RED, members.size());
			return false;
		}
		for (GroupLink link : group.getOutgoingLinks()) {
			if (link.getOriginatingType().equals(rank)) {
				reply(callback,
						"%sThe rank %s%s%s has an outgoing link and can not be deleted until that link has been removed",
						ChatColor.RED, ChatColor.YELLOW, rank.getName(), ChatColor.RED);
				return false;
			}
		}
		for (GroupLink link : group.getIncomingLinks()) {
			if (link.getTargetType().equals(rank)) {
				reply(callback,
						"%sThe rank %s%s%s has an incoming link and can not be deleted until that link has been removed",
						ChatColor.RED, ChatColor.YELLOW, rank.getName(), ChatColor.RED);
				return false;
			}
		}
		callback.accept(String.format("%sThe rank %s%s%s in %s%s has been deleted", ChatColor.GREEN, ChatColor.GOLD,
				rank.getName(), ChatColor.GREEN, group.getColoredName(), ChatColor.GREEN));
		rankHandler.deleteType(rank, true);
		group.getActionLog().addAction(new DeleteRank(System.currentTimeMillis(), executor, rank.getName()), true);
		return true;
	}

	public boolean inviteMember(UUID executor, String groupName, String targetPlayer, String rank,
			Consumer<String> callback) {
		Group group = getGroup(groupName, callback);
		if (group == null) {
			return false;
		}
		UUID toInvite = getPlayer(targetPlayer, callback);
		if (toInvite == null) {
			return false;
		}
		GroupRankHandler handler = group.getGroupRankHandler();
		GroupRank targetType = rank != null ? handler.getType(rank) : handler.getDefaultInvitationType();
		// this is designed to not reveal any names of player types to the outside
		if (targetType == null) {
			callback.accept(
					ChatColor.RED + "The rank you entered did not exist or you do not permission to invite to it");
			return false;
		}
		PermissionType permRequired = targetType.getInvitePermissionType();
		if (!groupMan.hasAccess(group, executor, permRequired)) {
			callback.accept(
					ChatColor.RED + "The rank you entered did not exist or you do not permission to invite to it");
			return false;
		}
		if (handler.isBlackListedType(targetType)) {
			reply(callback, "%sYou can not invite players to the blacklist rank %s%s%s in %s", ChatColor.RED,
					ChatColor.GOLD, targetType.getName(), ChatColor.RED, group.getColoredName());
			return false;
		}
		if (group.isTracked(toInvite)) {
			callback.accept(String.format(
					"%sThe player %s%s%s is already tracked for %s%s. You have to modify their rank instead of inviting them.",
					ChatColor.RED, ChatColor.YELLOW, NameAPI.getCurrentName(toInvite), ChatColor.RED,
					group.getColoredName(), ChatColor.RED));
			return false;
		}
		callback.accept(String.format("%s%s %shas been invited as %s%s%s to %s", ChatColor.YELLOW,
				NameAPI.getCurrentName(toInvite), ChatColor.GREEN, ChatColor.YELLOW, targetType.getName(),
				ChatColor.GREEN, group.getColoredName()));
		group.getActionLog().addAction(
				new InviteMember(System.currentTimeMillis(), executor, targetType.getName(), toInvite), true);
		groupMan.invitePlayer(executor, toInvite, targetType, group);
		return true;
	}

	public boolean joinGroup(UUID executor, String groupName, String submittedPassword, Consumer<String> callback) {
		Group group = getGroup(groupName, callback);
		if (group == null) {
			return false;
		}
		String password = NameLayerPlugin.getInstance().getNameLayerMeta().getMetaData(group).getPassword();
		if (password == null) {
			reply(callback, "%s%s does not have a password set and can thus not be joined with one",
					group.getColoredName(), ChatColor.RED);
			return false;
		}
		if (!password.equals(submittedPassword)) {
			callback.accept(ChatColor.RED + "Wrong password");
			return false;
		}
		if (group.isTracked(executor)) {
			reply(callback, "%sYou either already are a member or blacklisted on %s", ChatColor.RED,
					group.getColoredName());
			return false;
		}
		GroupRank targetType = group.getGroupRankHandler().getDefaultPasswordJoinType();
		groupMan.addPlayerToGroup(group, executor, targetType, true);
		group.getActionLog().addAction(new JoinGroup(System.currentTimeMillis(), executor, targetType.getName()), true);
		callback.accept(String.format("%You have been added to %s%s as a %s%s", ChatColor.GREEN, group.getColoredName(),
				ChatColor.GREEN, ChatColor.YELLOW, targetType.getName()));
		return true;
	}

	public Map<GroupRank, Set<UUID>> getMemberList(UUID executor, String groupName, Consumer<String> callback) {
		Group group = getGroup(groupName, callback);
		if (group == null) {
			return null;
		}
		Map<GroupRank, Set<UUID>> ranks = new HashMap<>();
		GroupRankHandler rankHandler = group.getGroupRankHandler();
		boolean showAll = groupMan.hasAccess(group, executor, permManager.getGroupStats());
		for (GroupRank rank : rankHandler.getAllTypes()) {
			if (rank == rankHandler.getDefaultNonMemberType()) {
				continue;
			}
			if (showAll || groupMan.hasAccess(group, executor, rank.getListPermissionType())) {
				ranks.put(rank, group.getAllTrackedByType(rank));
			}
		}
		return ranks;
	}

	public boolean linkGroups(UUID executor, String originatingGroupName, String originatingRankName,
			String targetGroupName, String targetRankName, Consumer<String> callback) {
		Group originatingGroup = getGroup(originatingGroupName, callback);
		if (originatingGroup == null) {
			return false;
		}
		Group targetGroup = getGroup(targetGroupName, callback);
		if (targetGroup == null) {
			return false;
		}
		if (originatingGroup.equals(targetGroup)) {
			callback.accept(ChatColor.RED + "You can not link a group to itself");
			return false;
		}
		if (!checkPermission(originatingGroup, executor, permManager.getLinkGroup(), callback)) {
			return false;
		}
		if (!checkPermission(targetGroup, executor, permManager.getLinkGroup(), callback)) {
			return false;
		}
		GroupRank originatingRank = getPlayerType(originatingGroup, originatingRankName, callback);
		if (originatingRank == null) {
			return false;
		}
		GroupRank targetRank = getPlayerType(targetGroup, targetRankName, callback);
		if (targetRank == null) {
			return false;
		}
		GroupLink link = groupMan.linkGroups(originatingGroup, originatingRank, targetGroup, targetRank);
		if (link == null) {
			callback.accept(ChatColor.RED + "Link could not be created, because it would create a cycle");
			return false;
		}
		originatingGroup.getActionLog().addAction(new AddLink(System.currentTimeMillis(), executor,
				originatingRank.getName(), targetGroup.getName(), targetRank.getName(), true), true);
		targetGroup.getActionLog().addAction(new AddLink(System.currentTimeMillis(), executor, targetRank.getName(),
				originatingGroup.getName(), originatingRank.getName(), false), true);
		reply(callback, "%sSuccessfully linked %s%s%s in %s%s to %s%s%s in %s", ChatColor.GREEN, ChatColor.GOLD,
				originatingRank.getName(), ChatColor.GREEN, originatingGroup.getColoredName(), ChatColor.GREEN,
				ChatColor.YELLOW, targetRank.getName(), ChatColor.GREEN, targetGroup.getColoredName());
		return true;
	}

	public boolean leaveGroup(UUID executor, String groupName, Consumer<String> callback) {
		Group group = getGroup(groupName, callback);
		if (group == null) {
			return false;
		}
		GroupRank rank = group.getRank(executor);
		if (!group.getGroupRankHandler().isMemberType(rank)) {
			reply(callback, "%sYou are not a member of %s", ChatColor.RED, group.getColoredName());
			return false;
		}
		// ensure the last owner can't leave
		if (rank.getParent() == null && group.getAllTrackedByType(rank).size() == 1) {
			reply(callback,
					"%sYou are the last remaining %s%s%s of %s%s. You can not leave until you have added another %s%s",
					ChatColor.RED, ChatColor.YELLOW, rank.getName(), ChatColor.RED, group.getColoredName(),
					ChatColor.RED, ChatColor.YELLOW, rank.getName());
			return false;
		}
		groupMan.removePlayerFromGroup(group, executor);
		group.getActionLog().addAction(new LeaveGroup(System.currentTimeMillis(), executor, rank.getName()), true);
		reply(callback, "%sYou have left %s", ChatColor.GREEN, group.getColoredName());
		return true;
	}

	public boolean mergeGroups(UUID executor, String groupToKeep, String groupToRemove, Consumer<String> callback) {
		Group groupKept = getGroup(groupToKeep, callback);
		if (groupKept == null) {
			return false;
		}
		Group groupRemoved = getGroup(groupToRemove, callback);
		if (groupRemoved == null) {
			return false;
		}
		if (groupRemoved.equals(groupKept)) {
			callback.accept(ChatColor.RED + "You can not merge a group into itself");
			return false;
		}
		if (!checkPermission(groupKept, executor, permManager.getMergeGroup(), callback)) {
			return false;
		}
		if (!checkPermission(groupRemoved, executor, permManager.getMergeGroup(), callback)) {
			return false;
		}
		if (!groupRemoved.getIncomingLinks().isEmpty() || !groupRemoved.getOutgoingLinks().isEmpty()) {
			reply(callback, "%s%s has active links, you need to remove them before merging",
					groupRemoved.getColoredName(), ChatColor.RED);
			return false;
		}
		groupMan.mergeGroup(groupKept, groupRemoved);
		groupKept.getActionLog().addAction(new MergeGroup(System.currentTimeMillis(), executor, groupRemoved.getName()),
				true);
		reply(callback, "%s%s%s was merged into %s", ChatColor.WHITE, groupRemoved.getName(), ChatColor.GREEN,
				groupKept.getColoredName());
		return true;
	}

	public boolean setPassword(UUID executor, String groupName, String password, Consumer<String> callback) {
		Group group = getGroup(groupName, callback);
		if (group == null) {
			return false;
		}
		if (!checkPermission(group, executor, permManager.getPassword(), callback)) {
			return false;
		}
		NameLayerMetaData meta = metaDataManager.getMetaData(group);
		group.getActionLog()
				.addAction(new SetPassword(System.currentTimeMillis(), executor, meta.getPassword(), password), true);
		meta.setPassword(password);
		reply(callback, "%sThe password for %s%s has been updated", ChatColor.GREEN, group.getColoredName(),
				ChatColor.GREEN);
		return true;
	}

	public boolean promotePlayer(UUID executor, String groupName, String playerName, String targetRank,
			Consumer<String> callback) {
		Group group = getGroup(groupName, callback);
		if (group == null) {
			return false;
		}
		UUID toPromote = getPlayer(playerName, callback);
		if (toPromote == null) {
			return false;
		}
		if (toPromote.equals(executor)) {
			callback.accept(ChatColor.RED + "You can not change your own rank");
			return false;
		}
		GroupRankHandler handler = group.getGroupRankHandler();
		GroupRank targetType = handler.getType(targetRank);
		// this is designed to not reveal any names of player types to the outside
		if (targetType == null) {
			callback.accept(ChatColor.RED
					+ "The player type you entered did not exist or you do not permission to promote to it");
			return false;
		}
		if (!GroupAPI.hasPermission(executor, group, targetType.getInvitePermissionType())) {
			callback.accept(ChatColor.RED
					+ "The player type you entered did not exist or you do not permission to promote to it");
			return false;
		}
		GroupRank currentRank = group.getRank(toPromote);
		if (currentRank == handler.getDefaultNonMemberType()) {
			reply(callback, "%s%s%s is not a member of the group or you do not have permission to modify their rank",
					ChatColor.YELLOW, NameAPI.getCurrentName(toPromote), ChatColor.RED);
			return false;
		}
		if (!groupMan.hasAccess(group, executor, currentRank.getRemovalPermissionType())) {
			reply(callback, "%s%s%s is not a member of the group or you do not have permission to modify their rank",
					ChatColor.YELLOW, NameAPI.getCurrentName(toPromote), ChatColor.RED);
			return false;
		}
		if (currentRank.equals(targetType)) {
			reply(callback, "%s%s%s already has the rank %s%s%s in %s", ChatColor.YELLOW,
					NameAPI.getCurrentName(toPromote), ChatColor.RED, ChatColor.GOLD, currentRank.getName(),
					ChatColor.RED, group.getColoredName());
			return false;
		}
		if (handler.isBlackListedType(currentRank) && !handler.isBlackListedType(targetType)) {
			reply(callback,
					"%s%s%s currently has a blacklisted rank and can not be promoted to a member rank. "
							+ "Remove them from the blacklist rank and invite them first.",
					ChatColor.YELLOW, NameAPI.getCurrentName(toPromote), ChatColor.RED);
			return false;
		}
		group.getActionLog().addAction(new ChangeMemberRank(System.currentTimeMillis(), executor, targetType.getName(),
				toPromote, currentRank.getName()), true);
		groupMan.updatePlayerRankInGroup(group, toPromote, targetType);
		reply(callback, "Changed rank of %s%s%s from %s%s%s to %s%s%s in %s", ChatColor.GREEN, ChatColor.YELLOW,
				NameAPI.getCurrentName(toPromote), ChatColor.GREEN, ChatColor.YELLOW, currentRank.getName(),
				ChatColor.GREEN, ChatColor.YELLOW, targetType.getName(), ChatColor.GREEN, group.getColoredName());
		return true;
	}

	public boolean rejectInvite(UUID executor, String groupName, Consumer<String> callback) {
		Group group = getGroup(groupName, callback);
		if (group == null) {
			return false;
		}
		GroupRank rankInvitedTo = group.getInvite(executor);
		if (rankInvitedTo == null) {
			reply(callback, "%sYou have not been invited to %s", ChatColor.RED, group.getColoredName());
			return false;
		}
		groupMan.deleteInvite(group, executor);
		PlayerListener.removeNotification(executor, group);
		group.getActionLog().addAction(new RejectInvite(System.currentTimeMillis(), executor, rankInvitedTo.getName()),
				true);
		reply(callback, "%sYou rejected the invite to %s%s as %s%s", ChatColor.GREEN, group.getColoredName(),
				ChatColor.GREEN, ChatColor.GOLD, rankInvitedTo.getName());
		return true;
	}

	public boolean revokeInvite(UUID executor, String groupName, String playerName, Consumer<String> callback) {
		Group group = getGroup(groupName, callback);
		if (group == null) {
			return false;
		}
		UUID invitedPlayer = getPlayer(playerName, callback);
		if (invitedPlayer == null) {
			return false;
		}
		GroupRank rankInvitedTo = group.getInvite(invitedPlayer);
		if (rankInvitedTo == null) {
			reply(callback, "%s%s%s has not been invited to %s%s or you do not have permission to revoke their invite",
					ChatColor.YELLOW, NameAPI.getCurrentName(invitedPlayer), ChatColor.RED, group.getColoredName(),
					ChatColor.RED);
			return false;
		}
		if (!groupMan.hasAccess(group, executor, rankInvitedTo.getInvitePermissionType())) {
			reply(callback, "%s%s%s has not been invited to %s%s or you do not have permission to revoke their invite",
					ChatColor.YELLOW, NameAPI.getCurrentName(invitedPlayer), ChatColor.RED, group.getColoredName(),
					ChatColor.RED);
			return false;
		}
		groupMan.deleteInvite(group, invitedPlayer);
		PlayerListener.removeNotification(invitedPlayer, group);
		group.getActionLog().addAction(
				new RevokeInvite(System.currentTimeMillis(), executor, rankInvitedTo.getName(), invitedPlayer), true);
		reply(callback, "%sRevoked an invite to %s%s as %s%s%s from %s%s", ChatColor.GREEN, group.getColoredName(),
				ChatColor.GREEN, ChatColor.GOLD, rankInvitedTo.getName(), ChatColor.GREEN, ChatColor.YELLOW,
				NameAPI.getCurrentName(invitedPlayer));
		return true;
	}

	public boolean unlinkGroups(UUID executor, String originatingGroupName, String originatingRankName,
			String targetGroupName, String targetRankName, Consumer<String> callback) {
		Group originatingGroup = getGroup(originatingGroupName, callback);
		if (originatingGroup == null) {
			return false;
		}
		Group targetGroup = getGroup(targetGroupName, callback);
		if (targetGroup == null) {
			return false;
		}
		if (originatingGroup.equals(targetGroup)) {
			callback.accept(ChatColor.RED + "You can not unlink a group from itself");
			return false;
		}
		if (!checkPermission(originatingGroup, executor, permManager.getLinkGroup(), callback)) {
			return false;
		}
		if (!checkPermission(targetGroup, executor, permManager.getLinkGroup(), callback)) {
			return false;
		}
		GroupRank originatingRank = getPlayerType(originatingGroup, originatingRankName, callback);
		if (originatingRank == null) {
			return false;
		}
		GroupRank targetRank = getPlayerType(targetGroup, targetRankName, callback);
		if (targetRank == null) {
			return false;
		}
		GroupLink foundLink = null;
		for (GroupLink link : originatingGroup.getOutgoingLinks()) {
			if (!link.getOriginatingType().equals(originatingRank)) {
				continue;
			}
			if (!link.getTargetGroup().equals(targetGroup)) {
				continue;
			}
			if (!link.getTargetType().equals(targetRank)) {
				continue;
			}
			foundLink = link;
			break;
		}
		if (foundLink == null) {
			reply(callback, "%sNo link from %s%s%s in %s%s to %s%s%s in %s%s exists", ChatColor.RED, ChatColor.GOLD,
					originatingRank.getName(), ChatColor.RED, originatingGroup.getColoredName(), ChatColor.RED,
					ChatColor.GOLD, targetRank.getName(), ChatColor.GOLD, targetGroup.getColoredName(), ChatColor.RED);
			return false;
		}
		groupMan.deleteGroupLink(foundLink);
		originatingGroup.getActionLog().addAction(new RemoveLink(System.currentTimeMillis(), executor,
				originatingRank.getName(), targetGroup.getName(), targetRank.getName(), true), true);
		targetGroup.getActionLog().addAction(new RemoveLink(System.currentTimeMillis(), executor, targetRank.getName(),
				originatingGroup.getName(), originatingRank.getName(), false), true);
		reply(callback, "%sSuccessfully unlinked %s%s%s in %s%s from %s%s%s in %s", ChatColor.GREEN, ChatColor.GOLD,
				originatingRank.getName(), ChatColor.GREEN, originatingGroup.getColoredName(), ChatColor.GREEN,
				ChatColor.YELLOW, targetRank.getName(), ChatColor.GREEN, targetGroup.getColoredName());
		return true;
	}

	public boolean editPermission(UUID executor, String groupName, boolean adding, String rankName,
			String permissionName, Consumer<String> callback) {
		Group group = getGroup(groupName, callback);
		if (group == null) {
			return false;
		}
		if (!checkPermission(group, executor, permManager.getModifyPerms(), callback)) {
			return false;
		}
		GroupRank rank = getPlayerType(group, rankName, callback);
		if (rank == null) {
			return false;
		}
		PermissionType permission = PermissionType.getPermission(permissionName);
		if (permission == null) {
			reply(callback, "%sThe permission %s%s%s does not exist", ChatColor.RED, ChatColor.YELLOW, rankName,
					ChatColor.RED);
			return false;
		}
		if (adding) {
			if (rank.hasPermission(permission)) {
				reply(callback, "%sThe rank %s%s%s of %s%s already has the permission %s%s", ChatColor.RED,
						ChatColor.YELLOW, rank.getName(), ChatColor.RED, group.getColoredName(), ChatColor.RED,
						ChatColor.YELLOW, permission.getName());
				return false;
			}
			group.getActionLog().addAction(
					new AddPermission(System.currentTimeMillis(), executor, rank.getName(), permission.getName()),
					true);
			rank.addPermission(permission, true);
			reply(callback, "%sAdded the permission %s%s%s to %s%s%s for %s", ChatColor.GREEN, ChatColor.YELLOW,
					permission.getName(), ChatColor.GREEN, ChatColor.YELLOW, rank.getName(), ChatColor.GREEN,
					group.getColoredName());
		} else {
			if (!rank.hasPermission(permission)) {
				reply(callback, "%sThe rank %s%s%s of %s%s does not have the permission %s%s", ChatColor.RED,
						ChatColor.YELLOW, rank.getName(), ChatColor.RED, group.getColoredName(), ChatColor.RED,
						ChatColor.YELLOW, permission.getName());
				return false;
			}
			group.getActionLog().addAction(
					new RemovePermission(System.currentTimeMillis(), executor, rank.getName(), permission.getName()),
					true);
			rank.removePermission(permission, true);
			reply(callback, "%sRemoved the permission %s%s%s from %s%s%s for %s", ChatColor.GREEN, ChatColor.YELLOW,
					permission.getName(), ChatColor.GREEN, ChatColor.YELLOW, rank.getName(), ChatColor.GREEN,
					group.getColoredName());
		}
		return true;
	}

	public boolean removeMember(UUID executor, String groupName, String playerName, Consumer<String> callback) {
		Group group = getGroup(groupName, callback);
		if (group == null) {
			return false;
		}
		UUID toKick = getPlayer(playerName, callback);
		if (toKick == null) {
			return false;
		}
		if (toKick.equals(executor)) {
			callback.accept(ChatColor.RED + "You can not kick yourself from a group, leave it instead");
			return false;
		}
		// this is designed to not reveal any names of player types or current member
		// status to the outside
		GroupRankHandler handler = group.getGroupRankHandler();
		GroupRank currentRank = group.getRank(toKick);
		if (currentRank == handler.getDefaultNonMemberType()) {
			callback.accept(ChatColor.RED
					+ "The player is not a member of the group or you do not have sufficient permission to demote them");
			return false;
		}
		PermissionType permRequired = currentRank.getRemovalPermissionType();
		if (!GroupAPI.hasPermission(executor, group, permRequired)) {
			callback.accept(ChatColor.RED
					+ "The player is not a member of the group or you do not have sufficient permission to demote them");
			return false;
		}
		group.getActionLog()
				.addAction(new RemoveMember(System.currentTimeMillis(), executor, currentRank.getName(), toKick), true);
		groupMan.removePlayerFromGroup(group, toKick);
		reply(callback, "%s%s%s with the rank %s%s%s was kicked from %s", ChatColor.YELLOW,
				NameAPI.getCurrentName(toKick), ChatColor.GREEN, ChatColor.YELLOW, currentRank.getName(),
				ChatColor.GREEN, group.getColoredName());
		return true;
	}

	public boolean renameGroup(UUID executor, String oldName, String newName, Consumer<String> callback) {
		Group group = getGroup(oldName, callback);
		if (group == null) {
			return false;
		}
		if (!checkPermission(group, executor, permManager.getRenameGroup(), callback)) {
			return false;
		}
		oldName = group.getName(); // proper capitalization
		if (newName.equals(oldName)) {
			reply(callback, "%sYou can not rename a group to the exact same name", ChatColor.RED);
			return false;
		}
		if (!newName.equalsIgnoreCase(oldName) && groupMan.getGroup(newName) != null) {
			// allow changing capitalization, but not overwriting another one
			reply(callback, "%sA group with the name %s%s%s already exists", ChatColor.RED, ChatColor.YELLOW, newName,
					ChatColor.RED);
			return false;
		}
		groupMan.renameGroup(group, newName);
		NameLayerMetaData meta = metaDataManager.getMetaData(group);
		group.getActionLog().addAction(new ChangeGroupName(System.currentTimeMillis(), executor, oldName, newName),
				true);
		reply(callback, "%sThe group %s%s%s was renamed to %s", ChatColor.GREEN, meta.getChatColor(), oldName,
				ChatColor.GREEN, group.getColoredName());
		return true;
	}

	public boolean renameRank(UUID executor, String groupName, String oldName, String newName,
			Consumer<String> callback) {
		Group group = getGroup(groupName, callback);
		if (group == null) {
			return false;
		}
		if (!checkPermission(group, executor, permManager.getRenamePlayerType(), callback)) {
			return false;
		}
		if (!isConformName(newName, callback)) {
			return false;
		}
		GroupRankHandler typeHandler = group.getGroupRankHandler();
		GroupRank rank = getPlayerType(group, oldName, callback);
		if (rank == null) {
			return false;
		}
		oldName = rank.getName(); // proper capitalization
		if (newName.equals(oldName)) {
			reply(callback, "%sYou can not rename a rank to the exact same name", ChatColor.RED);
			return false;
		}
		if (!newName.equalsIgnoreCase(oldName) && typeHandler.getType(newName) != null) {
			// allow changing capitalization, but not overwriting another one
			reply(callback, "%sA rank with the name %s%s%s already exists", ChatColor.RED, ChatColor.YELLOW, newName,
					ChatColor.RED);
			return false;
		}
		typeHandler.renameType(rank, newName, true);
		reply(callback, "%sThe rank %s%s%s in %s%s was renamed to %s%s", ChatColor.GREEN, ChatColor.YELLOW, oldName,
				ChatColor.GREEN, group.getColoredName(), ChatColor.GREEN, ChatColor.GOLD, newName);
		group.getActionLog().addAction(new ChangeRankName(System.currentTimeMillis(), executor, oldName, newName),
				true);
		return true;
	}

	private static void reply(Consumer<String> callback, String toFormat, Object... args) {
		callback.accept(String.format(toFormat, args));
	}

	private static boolean isConformName(String name, Consumer<String> toReplyTo) {
		if (name.length() > 32) {
			toReplyTo.accept(ChatColor.RED + "The name is not allowed to contain more than 32 characters");
			return false;
		}
		Charset latin1 = StandardCharsets.ISO_8859_1;
		boolean invalidChars = false;
		if (!latin1.newEncoder().canEncode(name)) {
			invalidChars = true;
		}

		for (char c : name.toCharArray()) {
			if (Character.isISOControl(c)) {
				invalidChars = true;
			}
		}
		if (invalidChars) {
			toReplyTo.accept(ChatColor.RED + "You used characters, which are not allowed");
			return false;
		}
		return true;
	}

	private Group getGroup(String name, Consumer<String> callback) {
		Group group = groupMan.getGroup(name);
		if (group == null) {
			callback.accept(String.format("%sThe group %s does not exist", ChatColor.RED, name));
		}
		return group;
	}

	private static GroupRank getPlayerType(Group group, String rankName, Consumer<String> callback) {
		GroupRank rank = group.getGroupRankHandler().getType(rankName);
		if (rank == null) {
			reply(callback, "%sThe rank %s%s%s does not exist for the group %s", ChatColor.RED, ChatColor.YELLOW,
					rankName, ChatColor.RED, group.getColoredName());
		}
		return rank;
	}

	private static UUID getPlayer(String playerName, Consumer<String> callback) {
		UUID uuid = NameAPI.getUUID(playerName);
		if (uuid == null) {
			callback.accept(String.format("%sThe player %s%s%s does not exist", ChatColor.RED, ChatColor.YELLOW,
					playerName, ChatColor.RED));
		}
		return uuid;
	}

	private boolean checkPermission(Group group, UUID player, PermissionType perm, Consumer<String> callback) {
		if (groupMan.hasAccess(group, player, perm)) {
			return true;
		}
		callback.accept(String.format(
				"%sTo do this you need the permission %s%s %sfor the group %s%s which you do not have", ChatColor.RED,
				ChatColor.YELLOW, perm.getName(), ChatColor.RED, group.getColoredName(), ChatColor.RED));
		return false;
	}

}
