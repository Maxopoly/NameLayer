package vg.civcraft.mc.namelayer.misc;

import java.util.UUID;

import vg.civcraft.mc.mercury.MercuryAPI;
import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.permission.PlayerType;

public class MercuryManager {

    private static final char separator = '|';
    private static final String channel = "namelayer";

    public static void addInvitation(Group group, PlayerType pType,
	    UUID invitee, UUID inviter) {
	sendMessage("addInvitation", group.getGroupId(), pType.getId(),
		invitee, inviter);
    }
    
    public static void removeInvitation(Group group, UUID player) {
	sendMessage("removeInvitation", group.getGroupId(), player);
    }

    private static void sendMessage(Object... messageParts) {
	if (!NameLayerPlugin.isMercuryEnabled()) {
	    return;
	}
	StringBuilder sb = new StringBuilder();
	for (int i = 0; i < messageParts.length; i++) {
	    if (messageParts[i] == null) {
		continue;
	    }
	    sb.append(messageParts[i]);
	    if (i < (messageParts.length)) {
		sb.append(separator);
	    }
	}
	MercuryAPI.sendGlobalMessage(sb.toString(), channel);
    }
}
