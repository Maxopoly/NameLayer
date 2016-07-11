package vg.civcraft.mc.namelayer.groupRequests;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import vg.civcraft.mc.civmodcore.interfaces.CustomEvent;
import vg.civcraft.mc.mercury.MercuryAPI;
import vg.civcraft.mc.namelayer.GroupManager;
import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.command.commands.InvitePlayer;
import vg.civcraft.mc.namelayer.events.GroupMembershipInvitationEvent;
import vg.civcraft.mc.namelayer.events.GroupPromotePlayerEvent;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.listeners.PlayerListener;
import vg.civcraft.mc.namelayer.misc.MercuryManager;
import vg.civcraft.mc.namelayer.permission.PlayerType;
import vg.civcraft.mc.namelayer.permission.PlayerTypeHandler;

/**
 * Different methods of input (gui, commands, external webtools etc.) may want
 * to request specific changes to groups
 *
 */
public class PlayerInteractionHandler {

    private static GroupManager gm = NameAPI.getGroupManager();

    public static RequestResult invitePlayer(UUID requestor,
	    String groupName, String inviteeName, String playerTypeName) {
	if (groupName == null) {
	    return RequestResultFactory.groupNameWasNull();
	}
	final boolean isAdmin = requestor == null
		|| (Bukkit.getPlayer(requestor) != null && Bukkit.getPlayer(
			requestor).hasPermission("namelayer.admin"));
	final Group group = GroupManager.getGroup(groupName);
	if (group == null) {
	    return RequestResultFactory.groupDoesNotExist(groupName);
	}
	if (!isAdmin && group.isDisciplined()) {
	    return RequestResultFactory.groupIsDisciplined();
	}
	final UUID targetAccount = NameAPI.getUUID(inviteeName);
	if (targetAccount == null) {
	    return RequestResultFactory.playerDoesNotExist(inviteeName);
	}
	if (group.isMember(targetAccount)) {
	    return new RequestResult(NameAPI.getCurrentName(targetAccount)
		    + " is already a member of " + group.getName(), false);
	}
	PlayerTypeHandler handler = group.getPlayerTypeHandler();
	final PlayerType pType = playerTypeName != null ? handler
		.getType(playerTypeName) : handler.getDefaultInvitationType();
	if (pType == null) {
	    return RequestResultFactory.playerTypeDoesNotExist(playerTypeName,
		    group.getName());
	}
	if (pType.isBlacklistType()
		|| pType.equals(handler.getDefaultNonMemberType())) {
	    return new RequestResult(
		    "You can't invite to player types, which aren't explicit member types",
		    false);
	}
	// Perform access check
	if (!isAdmin
		&& !gm.hasAccess(group, requestor, pType.getEditPermission())) {
	    return RequestResultFactory.playerDoesNotHavePermission();
	}
	PlayerType currentInviteeType = group.getPlayerType(targetAccount);
	StringBuilder result = new StringBuilder();
	if (!currentInviteeType.equals(handler.getDefaultNonMemberType())) {
	    if (!isAdmin
		    && !gm.hasAccess(group, requestor,
			    currentInviteeType.getEditPermission())) {
		return RequestResultFactory.playerDoesNotHavePermission();
	    }
	    if (callEvent(new GroupPromotePlayerEvent(requestor, group,
		    currentInviteeType, handler.getDefaultNonMemberType()))) {
		return RequestResultFactory.playerActionNotAllowed();
	    }
	    if (callEvent(new GroupMembershipInvitationEvent(group.getName(),
		    pType, targetAccount, requestor))) {
		return RequestResultFactory.playerActionNotAllowed();
	    }
	    result.append(NameAPI.getCurrentName(targetAccount)
		    + " had a blacklisted player type assigned for this group. To allow inviting him, "
		    + "he was removed from that type. \n");
	    group.removeTracked(targetAccount);
	} else {
	    if (callEvent(new GroupMembershipInvitationEvent(group.getName(),
		    pType, targetAccount, requestor))) {
		return RequestResultFactory.playerActionNotAllowed();
	    }
	}

	InvitePlayer.sendInvitation(group, pType, targetAccount, requestor, true);
	MercuryManager.addInvitation(group, pType, targetAccount, requestor);
	result.append(NameAPI.getCurrentName(targetAccount) + " was invited as " + pType.getName() + " to " + group.getName());
	return new RequestResult(result.toString(), true);
    }
    
    public static RequestResult acceptInvitation(UUID player, String groupName) {
	if (groupName == null) {
	    return RequestResultFactory.groupNameWasNull();
	}
	Group group = GroupManager.getGroup(groupName);
	if (group == null) {
	    return RequestResultFactory.groupDoesNotExist(groupName);
	}
	PlayerType type = group.getInvite(player);
	if (type == null){
		return new RequestResult("You were not invited to " + group.getName(), false);
	}
	if (group.isDisciplined()){
		return RequestResultFactory.groupIsDisciplined();
	}
	if (group.isMember(player)){
	    group.removeInvite(player, true);
		return new RequestResult("You are already a member of " + group.getName() + ", so you can't join again", false);
	}
	group.removeInvite(player, true);
	group.addTracked(player, type);
	PlayerListener.removeNotification(player, group);
	
	MercuryManager.removeInvitation(group, player);
	return new RequestResult("You have successfully been added to " + group.getName() + " as " +  type.getName(), true);
    }

    private static boolean callEvent(CustomEvent e) {
	Bukkit.getPluginManager().callEvent(e);
	return e.isCancelled();
    }
}
