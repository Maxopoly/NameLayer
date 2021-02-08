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
import vg.civcraft.mc.namelayer.core.log.impl.RemoveLink;
import vg.civcraft.mc.namelayer.core.requests.UnblacklistPlayer;
import vg.civcraft.mc.namelayer.core.requests.UnlinkGroups;

public class UnlinkGroupsHandler extends GroupRequestHandler {

	@Override
	public void handle(String ticket, ConnectedServer sendingServer, JSONObject data, UUID executor, Group group) {
		if (group == null) {
			sendReject(ticket, UnlinkGroups.REPLY_ID, sendingServer,
					UnlinkGroups.FailureReason.ORIGINAL_GROUP_DOES_NOT_EXIST);
			return;
		}
		String targetGroupName = data.getString("targetGroup");
		Group targetGroup = getGroupTracker().getGroup(targetGroupName);
		if (targetGroup == null) {
			sendReject(ticket, UnlinkGroups.REPLY_ID, sendingServer,
					UnlinkGroups.FailureReason.TARGET_GROUP_DOES_NOT_EXIST);
			return;
		}
		// Acquire locks in id order to avoid deadlocks
		Group lowerID = targetGroup.getPrimaryId() < group.getPrimaryId() ? targetGroup : group;
		Group upperID = targetGroup.getPrimaryId() < group.getPrimaryId() ? group : targetGroup;
		synchronized (lowerID) {
			synchronized (upperID) {
				if (group.equals(targetGroup)) {
					sendReject(ticket, UnlinkGroups.REPLY_ID, sendingServer,
							UnlinkGroups.FailureReason.CANNOT_UNLINK_SELF);
					return;
				}
				PermissionType permNeeded = getGroupTracker().getPermissionTracker().getPermission(NameLayerPermissions.LINK_GROUP);
				if (!getGroupTracker().hasAccess(group, executor, permNeeded)) {
					Map<String, Object> repValues = new HashMap<>();
					repValues.put("missing_perm", permNeeded.getName());
					sendReject(ticket, UnlinkGroups.REPLY_ID, sendingServer,
							UnlinkGroups.FailureReason.NO_PERMISSION_ORIGINAL_GROUP, repValues);
					return;
				}
				if (!getGroupTracker().hasAccess(targetGroup, executor, permNeeded)) {
					Map<String, Object> repValues = new HashMap<>();
					repValues.put("missing_perm", permNeeded.getName());
					sendReject(ticket, UnlinkGroups.REPLY_ID, sendingServer,
							UnlinkGroups.FailureReason.NO_PERMISSION_TARGET_GROUP, repValues);
					return;
				}
				GroupRank originalRank = group.getGroupRankHandler().getRank(data.getInt("originatingRank"));
				if (originalRank == null) {
					sendReject(ticket, UnlinkGroups.REPLY_ID, sendingServer,
							UnlinkGroups.FailureReason.ORIGINAL_RANK_DOES_NOT_EXIST);
					return;
				}
				GroupRank targetRank = group.getGroupRankHandler().getRank(data.getInt("targetRank"));
				if (targetRank == null) {
					sendReject(ticket, UnlinkGroups.REPLY_ID, sendingServer,
							UnlinkGroups.FailureReason.TARGET_RANK_DOES_NOT_EXIST);
					return;
				}
				GroupLink foundLink = null;
				for (GroupLink link : group.getOutgoingLinks()) {
					if (!link.getOriginatingRank().equals(originalRank)) {
						continue;
					}
					if (!link.getTargetGroup().equals(targetGroup)) {
						continue;
					}
					if (!link.getTargetRank().equals(targetRank)) {
						continue;
					}
					foundLink = link;
					break;
				}
				if (foundLink == null) {
					sendReject(ticket, UnlinkGroups.REPLY_ID, sendingServer,
							UnlinkGroups.FailureReason.NO_LINKS_FOUND);
					return;
				}
				getGroupTracker().deleteGroupLink(foundLink);
				getGroupTracker().addLogEntry(group, new RemoveLink(System.currentTimeMillis(), executor,
						originalRank.getName(), targetGroupName, targetRank.getName(), true));
				getGroupTracker().addLogEntry(targetGroup, new RemoveLink(System.currentTimeMillis(), executor,
						targetRank.getName(), group.getName(), originalRank.getName(), false));
				sendAccept(ticket, UnblacklistPlayer.REPLY_ID, sendingServer);
			}
		}
	}

	@Override
	public String getIdentifier() {
		return UnlinkGroups.REQUEST_ID;
	}

}
