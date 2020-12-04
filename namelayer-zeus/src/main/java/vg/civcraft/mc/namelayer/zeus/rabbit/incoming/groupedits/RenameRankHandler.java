package vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.github.civcraft.zeus.servers.ConnectedServer;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.GroupRankHandler;
import vg.civcraft.mc.namelayer.core.NameLayerPermissions;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.core.requests.RenameRank;

public class RenameRankHandler extends GroupRequestHandler {

	@Override
	public void handle(String ticket, ConnectedServer sendingServer, JSONObject data, UUID executor, Group group) {
		if (group == null) {
			sendReject(ticket, RenameRank.REPLY_ID, sendingServer, RenameRank.FailureReason.GROUP_DOES_NOT_EXIST);
			return;
		}
		synchronized (group) {
			PermissionType permNeeded = getGroupTracker().getPermissionTracker().getPermission(NameLayerPermissions.RENAME_RANK);
			if (!getGroupTracker().hasAccess(group, executor, permNeeded)) {
				Map<String, Object> repValues = new HashMap<>();
				repValues.put("missing_perm", permNeeded);
				sendReject(ticket, RenameRank.REPLY_ID, sendingServer, RenameRank.FailureReason.NO_PERMISSION, repValues);
				return;
			}
			String newName = data.getString("newRankName");
			if (!isConformName(newName)) {
				sendReject(ticket, RenameRank.REPLY_ID, sendingServer, RenameRank.FailureReason.BAD_NAME);
				return;
			}
			GroupRankHandler typeHandler = group.getGroupRankHandler();
			GroupRank rank = group.getRank(executor);
			if (rank == null) {
				sendReject(ticket, RenameRank.REPLY_ID, sendingServer, RenameRank.FailureReason.RANK_DOES_NOT_EXIST);
				return;
			}
			String oldName = group.getName();
			if (newName.equals(oldName)) {
				sendReject(ticket, RenameRank.REPLY_ID, sendingServer, RenameRank.FailureReason.SAME_NAME);
				return;
			}
			if (!newName.equalsIgnoreCase(oldName) && typeHandler.getRank(newName) != null) {
				sendReject(ticket, RenameRank.REPLY_ID, sendingServer, RenameRank.FailureReason.NAME_ALREADY_TAKEN);
				return;
			}
			getGroupTracker().renameRank(group, rank, newName);
			sendAccept(ticket, RenameRank.REPLY_ID, sendingServer);
		}
	}

	@Override
	public String getIdentifier() {
		return RenameRank.REQUEST_ID;
	}
	
	private static boolean isConformName(String name) {
		if (name.length() > 32) {
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
			return false;
		}
		return true;
	}	

}
