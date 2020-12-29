package vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.json.JSONObject;

import com.github.civcraft.zeus.servers.ConnectedServer;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupLink;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.GroupRankHandler;
import vg.civcraft.mc.namelayer.core.NameLayerPermissions;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.core.requests.DeleteRank;

public class DeleteRankHandler extends GroupRequestHandler {

	@Override
	public void handle(String ticket, ConnectedServer sendingServer, JSONObject data, UUID executor, Group group) {
		if (group == null) {
			sendReject(ticket, DeleteRank.REPLY_ID, sendingServer, DeleteRank.FailureReason.GROUP_DOES_NOT_EXIST);
			return;
		}
		synchronized (group) {
			GroupRankHandler rankHandler = group.getGroupRankHandler();
			PermissionType perm = getGroupTracker().getPermissionTracker()
					.getPermission(NameLayerPermissions.DELETE_RANK);
			if (!getGroupTracker().hasAccess(group, executor, perm)) {
				Map<String, Object> repValues = new HashMap<>();
				repValues.put("missing_perm", perm);
				sendReject(ticket, DeleteRank.REPLY_ID, sendingServer, DeleteRank.FailureReason.NO_PERMISSION,
						repValues);
				return;
			}
			int rankToDeleteID = data.getInt("rankToDelete");
			GroupRank rankToDelete = rankHandler.getRank(rankToDeleteID);
			if (rankToDelete == null) {
				sendReject(ticket, DeleteRank.REPLY_ID, sendingServer, DeleteRank.FailureReason.RANK_DOES_NOT_EXIST);
				return;
			}
			if (!rankToDelete.getChildren(false).isEmpty()) {
				sendReject(ticket, DeleteRank.REPLY_ID, sendingServer, DeleteRank.FailureReason.RANK_HAS_CHILDREN,
						new HashMap<>());
				return;
			}
			if (rankToDelete == rankHandler.getDefaultNonMemberRank()) {
				sendReject(ticket, DeleteRank.REPLY_ID, sendingServer, DeleteRank.FailureReason.DEFAULT_NON_MEMBER_RANK,
						new HashMap<>());
				return;
			}
			if (rankToDelete == rankHandler.getOwnerRank()) {
				sendReject(ticket, DeleteRank.REPLY_ID, sendingServer, DeleteRank.FailureReason.LAST_REMAINING_RANK,
						new HashMap<>());
			}
			Set<UUID> members = group.getAllTrackedByType(rankToDelete);
			if (!members.isEmpty()) {
				sendReject(ticket, DeleteRank.REPLY_ID, sendingServer, DeleteRank.FailureReason.STILL_HAS_MEMBERS,
						new HashMap<>());
			}
			for (GroupLink link : group.getOutgoingLinks()) {
				if (link.getOriginatingRank().equals(rankToDelete)) {
					sendReject(ticket, DeleteRank.REPLY_ID, sendingServer, DeleteRank.FailureReason.HAS_OUTGOING_LINKS,
							new HashMap<>());
					return;
				}
			}
			for (GroupLink link : group.getIncomingLinks()) {
				if (link.getTargetRank().equals(rankToDelete)) {
					sendReject(ticket, DeleteRank.REPLY_ID, sendingServer, DeleteRank.FailureReason.HAS_INCOMING_LINKS,
							new HashMap<>());
					return;
				}
			}
			getGroupTracker().deleteRank(group, rankToDelete);
			sendAccept(ticket, DeleteRank.REPLY_ID, sendingServer);
		}

	}

	@Override
	public String getIdentifier() {
		return DeleteRank.REQUEST_ID;
	}

}
