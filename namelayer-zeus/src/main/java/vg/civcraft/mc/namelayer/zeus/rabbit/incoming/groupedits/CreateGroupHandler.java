package vg.civcraft.mc.namelayer.zeus.rabbit.incoming.groupedits;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.servers.ConnectedServer;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.requests.CreateGroup;

public class CreateGroupHandler extends GroupRequestHandler {

	@Override
	public void handle(String ticket, ConnectedServer sendingServer, JSONObject data, UUID executor, Group group) {
		if (group != null) {
			sendReject(ticket, CreateGroup.REPLY_ID, sendingServer, CreateGroup.FailureReason.GROUP_ALREADY_EXISTS);
			return;
		}
		String groupName = data.getString("group");
		if (!isConformName(groupName)) {
			sendReject(ticket, CreateGroup.REPLY_ID, sendingServer, CreateGroup.FailureReason.NAME_INVALID);
			return;
		}
		synchronized (getGroupTracker()) {
			if (getGroupTracker().createGroup(groupName, executor) == null) {
				sendReject(ticket, CreateGroup.REPLY_ID, sendingServer, CreateGroup.FailureReason.UNKNOWN_ERROR);
				return;
			}
			sendAccept(ticket, CreateGroup.REPLY_ID, sendingServer);
		}

	}

	@Override
	public String getIdentifier() {
		return CreateGroup.REPLY_ID;
	}
	
	public static boolean isConformName(String name) {
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
