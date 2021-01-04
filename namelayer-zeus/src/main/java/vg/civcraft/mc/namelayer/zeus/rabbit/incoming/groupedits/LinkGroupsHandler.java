package vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.servers.ConnectedServer;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupLink;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.NameLayerPermissions;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.core.requests.LinkGroups;

public class LinkGroupsHandler extends GroupRequestHandler {

	@Override
	public void handle(String ticket, ConnectedServer sendingServer, JSONObject data, UUID executor, Group group) {
		if (group == null) {
			sendReject(ticket, LinkGroups.REPLY_ID, sendingServer, LinkGroups.FailureReason.ORIGINAL_GROUP_DOES_NOT_EXIST);
			return;
		}
		Group targetGroup = getGroupTracker().getGroup(data.getString("targetGroup"));
		if (targetGroup == null) {
			sendReject(ticket, LinkGroups.REPLY_ID, sendingServer, LinkGroups.FailureReason.TARGET_GROUP_DOES_NOT_EXIST);
			return;
		}
		synchronized (group) {
			if (group.equals(targetGroup)) {
				sendReject(ticket, LinkGroups.REPLY_ID, sendingServer, LinkGroups.FailureReason.CANNOT_LINK_TO_SELF);
				return;
			}
			PermissionType permNeeded = getGroupTracker().getPermissionTracker().getPermission(NameLayerPermissions.LINK_GROUP);
			if (!getGroupTracker().hasAccess(group, executor, permNeeded)) {
				Map<String, Object> repValues = new HashMap<>();
				repValues.put("missing_perm", NameLayerPermissions.LINK_GROUP);
				sendReject(ticket, LinkGroups.REPLY_ID, sendingServer, LinkGroups.FailureReason.NO_PERMISSION_ORIG_GROUP, repValues);
				return;
			}
			if (!getGroupTracker().hasAccess(targetGroup, executor, permNeeded)) {
				Map<String, Object> repValues = new HashMap<>();
				repValues.put("missing_perm", NameLayerPermissions.LINK_GROUP);
				sendReject(ticket, LinkGroups.REPLY_ID, sendingServer, LinkGroups.FailureReason.NO_PERMISSION_TARGET_GROUP, repValues);
				return;
			}
			GroupRank origRank = group.getGroupRankHandler().getRank(data.getString("originatingRank"));
			GroupRank tarRank = group.getGroupRankHandler().getRank(data.getString("targetRank"));
			if (origRank == null) {
				sendReject(ticket, LinkGroups.REPLY_ID, sendingServer, LinkGroups.FailureReason.ORIGINAL_GROUP_RANK_DOES_NOT_EXIST);
				return;
			}
			if (tarRank == null) {
				sendReject(ticket, LinkGroups.REPLY_ID, sendingServer, LinkGroups.FailureReason.TARGET_GROUP_RANK_DOES_NOT_EXIST);
				return;
			}
			GroupLink link = getGroupTracker().linkGroups(group, origRank, targetGroup, tarRank);
			if (link == null) {
				sendReject(ticket, LinkGroups.REPLY_ID, sendingServer, LinkGroups.FailureReason.ATTEMPTED_GROUP_CYCLING);
				return;
			}
			sendAccept(ticket, LinkGroups.REPLY_ID, sendingServer);
		}
	}

	@Override
	public String getIdentifier() {
		return LinkGroups.REQUEST_ID;
	}
	
}
