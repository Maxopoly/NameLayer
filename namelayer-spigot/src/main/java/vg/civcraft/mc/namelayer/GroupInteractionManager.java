package vg.civcraft.mc.namelayer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;

import javax.security.auth.callback.Callback;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.locations.chunkmeta.api.APIView;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.group.NameLayerMetaData;
import vg.civcraft.mc.namelayer.group.meta.GroupMetaDataView;
import vg.civcraft.mc.namelayer.listeners.PlayerListener;
import vg.civcraft.mc.namelayer.misc.NameLayerSettingManager;
import vg.civcraft.mc.namelayer.permission.NameLayerPermissionManager;
import vg.civcraft.mc.namelayer.permission.PermissionType;
import vg.civcraft.mc.namelayer.permission.PlayerType;
import vg.civcraft.mc.namelayer.permission.PlayerTypeHandler;

public class GroupInteractionManager {

	private GroupManager groupMan;
	private NameLayerPermissionManager permManager;
	private NameLayerSettingManager settingManager;
	private GroupMetaDataView<NameLayerMetaData> metaDataManager;

	public boolean acceptInvite(UUID executor, String groupName, Consumer<String> callback) {
		Group group = getGroup(groupName, callback);
		if (group == null) {
			return false;
		}
		PlayerType type = group.getInvite(executor);
		if (type == null) {
			callback.accept(String.format("%sYou were not invited to %s", ChatColor.RED, group.getColoredName()));
			return false;
		}
		if (group.isMember(executor)) {
			callback.accept(ChatColor.RED + "You are already a member or blacklisted you cannot join again.");
			group.removeInvite(executor);
			return false;
		}
		group.addToTracking(executor, type, true);
		group.removeInvite(executor);
		PlayerListener.removeNotification(executor, group);
		callback.accept(String.format("%You have been added to %s%s as a %s%s", ChatColor.GREEN, group.getColoredName(),
				ChatColor.GREEN, ChatColor.YELLOW, type.getName()));
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
		});
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
		PlayerType targetType = group.getPlayerTypeHandler().getDefaultPasswordJoinType();
		group.addToTracking(executor, targetType, true);
		callback.accept(String.format("%You have been added to %s%s as a %s%s", ChatColor.GREEN, group.getColoredName(),
				ChatColor.GREEN, ChatColor.YELLOW, targetType.getName()));
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
		groupMan.mergeGroup(groupKept, groupRemoved);
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
		metaDataManager.getMetaData(group).setPassword(password);
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
		PlayerTypeHandler handler = group.getPlayerTypeHandler();
		PlayerType targetType = handler.getType(targetRank);
		// this is designed to not reveal any names of player types to the outside
		if (targetType == null) {
			callback.accept(ChatColor.RED
					+ "The player type you entered did not exist or you do not permission to promote to it");
			return false;
		}
		PermissionType permRequired = targetType.getInvitePermissionType();
		if (!GroupAPI.hasPermission(executor, group, permRequired)) {
			callback.accept(ChatColor.RED
					+ "The player type you entered did not exist or you do not permission to invite to it");
			return false;
		}
		PlayerType currentRank = group.getPlayerType(toPromote);
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
		if (handler.isBlackListedType(currentRank) && !handler.isBlackListedType(targetType)) {
			reply(callback,
					"%s%s%s currently has a blacklisted rank and can not be promoted to a member rank. "
							+ "Remove them from the blacklist rank and invite them first.",
					ChatColor.YELLOW, NameAPI.getCurrentName(toPromote), ChatColor.RED);
			return false;
		}
		group.updateTracking(toPromote, targetType);
		reply(callback, "Changed rank of %s%s%s from %s%s%s to %s%s%s in %s", ChatColor.GREEN, ChatColor.YELLOW,
				NameAPI.getCurrentName(toPromote), ChatColor.GREEN, ChatColor.YELLOW, currentRank.getName(),
				ChatColor.GREEN, ChatColor.YELLOW, targetType.getName(), ChatColor.GREEN, group.getColoredName());
		return true;
	}

	public boolean editPermission(UUID executor, String groupName, boolean adding, String rank, String permissionName,
			Consumer<String> callback) {
		Group group = getGroup(groupName, callback);
		if (group == null) {
			return false;
		}
		if (!checkPermission(group, executor, permManager.getModifyPerms(), callback)) {
			return false;
		}
		PlayerType type = group.getPlayerTypeHandler().getType(rank);
		if (type == null) {
			reply(callback, "%sThe rank %s%s%s does not exist for the group %s", ChatColor.RED, ChatColor.YELLOW, rank,
					ChatColor.RED, group.getColoredName());
			return false;
		}
		PermissionType permission = PermissionType.getPermission(permissionName);
		if (permission == null) {
			reply(callback, "%sThe permission %s%s%s does not exist", ChatColor.RED, ChatColor.YELLOW, rank,
					ChatColor.RED);
			return false;
		}
		if (adding) {
			if (type.hasPermission(permission)) {
				reply(callback, "%sThe rank %s%s%s of %s%s already has the permission %s%s", ChatColor.RED,
						ChatColor.YELLOW, type.getName(), ChatColor.RED, group.getColoredName(), ChatColor.RED,
						ChatColor.YELLOW, permission.getName());
				return false;
			}
			type.addPermission(permission, true);
			reply(callback, "%sAdded the permission %s%s%s to %s%s%s for %s", ChatColor.GREEN, ChatColor.YELLOW,
					permission.getName(), ChatColor.GREEN, ChatColor.YELLOW, type.getName(), ChatColor.GREEN, group.getColoredName());
		} else {
			if (!type.hasPermission(permission)) {
				reply(callback, "%sThe rank %s%s%s of %s%s does not have the permission %s%s", ChatColor.RED,
						ChatColor.YELLOW, type.getName(), ChatColor.RED, group.getColoredName(), ChatColor.RED,
						ChatColor.YELLOW, permission.getName());
				return false;
			}
			type.removePermission(permission, true);
			reply(callback, "%sRemoved the permission %s%s%s from %s%s%s for %s", ChatColor.GREEN, ChatColor.YELLOW,
					permission.getName(), ChatColor.GREEN, ChatColor.YELLOW, type.getName(), ChatColor.GREEN, group.getColoredName());
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
		// this is designed to not reveal any names of player types or current member
		// status to the outside
		PlayerTypeHandler handler = group.getPlayerTypeHandler();
		PlayerType currentRank = group.getPlayerType(toKick);
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
		group.removeFromTracking(toKick, true);
		reply(callback, "%s%s%s with the rank %s%s%s was kicked from %s", ChatColor.YELLOW,
				NameAPI.getCurrentName(toKick), ChatColor.GREEN, ChatColor.YELLOW, currentRank.getName(),
				ChatColor.GREEN, group.getColoredName());
		return true;
	}

	private static void reply(Consumer<String> callback, String toFormat, Object... args) {
		callback.accept(String.format(toFormat, args));
	}

	private static boolean isConformName(String name, Consumer<String> toReplyTo) {
		if (name.length() > 32) {
			toReplyTo.accept(ChatColor.RED + "The group name is not allowed to contain more than 32 characters");
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
		PlayerTypeHandler handler = group.getPlayerTypeHandler();
		PlayerType targetType = rank != null ? handler.getType(rank) : handler.getDefaultInvitationType();
		// this is designed to not reveal any names of player types to the outside
		if (targetType == null) {
			callback.accept(ChatColor.RED
					+ "The player type you entered did not exist or you do not permission to invite to it");
			return false;
		}
		PermissionType permRequired = targetType.getInvitePermissionType();
		if (!GroupAPI.hasPermission(executor, group, permRequired)) {
			callback.accept(ChatColor.RED
					+ "The player type you entered did not exist or you do not permission to invite to it");
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
				ChatColor.YELLOW, group.getColoredName()));
		NameLayerPlugin.getInstance().getGroupManager().invitePlayer(executor, toInvite, targetType, group);
		return true;
	}

	public boolean leaveGroup(UUID executor, String groupName, Consumer<String> callback) {
		Group group = getGroup(groupName, callback);
		if (group == null) {
			return false;
		}
		PlayerType rank = group.getPlayerType(executor);
		if (!group.getPlayerTypeHandler().isMemberType(rank)) {
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
		group.removeFromTracking(executor);
		reply(callback, "%sYou have left %s", ChatColor.GREEN, group.getColoredName());
		return true;
	}

	private Group getGroup(String name, Consumer<String> callback) {
		Group group = groupMan.getGroup(name);
		if (group == null) {
			callback.accept(String.format("%sThe group %s does not exist", ChatColor.RED, name));
		}
		return group;
	}

	private UUID getPlayer(String playerName, Consumer<String> callback) {
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
