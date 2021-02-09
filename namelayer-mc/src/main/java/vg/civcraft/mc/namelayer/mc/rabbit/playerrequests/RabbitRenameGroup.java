package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;
import org.bukkit.ChatColor;
import org.json.JSONObject;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.requests.RenameGroup;

public class RabbitRenameGroup extends RabbitGroupAction {

	
	private String oldName;
	private String newName;

	public RabbitRenameGroup(UUID executor, Group oldGroup, String newName) {
		super(executor, oldGroup.getName());
		this.oldName = oldGroup.getName();
		this.newName = newName;
	}

	@Override
	public void handleReply(JSONObject reply, boolean success) {
		Group group = getGroup();
		if (success) {
			sendMessage(String.format("%sThe group %s%s was renamed to %s", ChatColor.GREEN, oldName,
				ChatColor.GREEN, newName));
			return;	
		}
		RenameGroup.FailureReason reason = RenameGroup.FailureReason.valueOf(reply.getString("reason"));
		String missingPerm = reply.optString("missing_perm", null);
		switch (reason) {
		case GROUP_DOES_NOT_EXIST:
			groupDoesNotExistMessage();
			return;
		case NAME_ALREADY_TAKEN:
			sendMessage(String.format("%sA group with the name %s%s%s already exists", ChatColor.RED, ChatColor.YELLOW, newName,
					ChatColor.RED));
			return;
		case NO_PERMISSION:
			noPermissionMessage(missingPerm);
			return;
		case SAME_NAME:
			sendMessage(String.format("%sYou can not rename a group to the exact same name", ChatColor.RED));
			return;
		default:
			break;
			
		}
		
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("newName", newName);
		
	}
	
	@Override
	public String getIdentifier() {
		return RenameGroup.REQUEST_ID;
	}

}
