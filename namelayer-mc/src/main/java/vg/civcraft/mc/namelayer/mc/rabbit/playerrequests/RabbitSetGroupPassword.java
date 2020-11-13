package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.rabbit.Group;

public class RabbitSetGroupPassword extends RabbitGroupAction {
	public enum FailureReason {
		NO_PERMISSION, NULL_PASSWORD, GROUP_DOES_NOT_EXIST;
	}
	
	private String password;

	public RabbitSetGroupPassword(UUID executor, Group group, String password) {
		super(executor, group.getName());
		this.password = password;
	}

	@Override
	public void handleReply(JSONObject reply, boolean success) {
		Group group = getGroup();
		if (success) {
			sendMessage(String.format("%sThe password for %s%s has been updated", ChatColor.GREEN, group.getColoredName(),
				ChatColor.GREEN));
			return;	
		}
		FailureReason reason = FailureReason.valueOf(reply.getString("reply"));
		switch (reason) {
		case GROUP_DOES_NOT_EXIST:
			groupDoesNotExistMessage(;
			return;
		case NO_PERMISSION:
			String missingPerm = reply.optString("missing_perm", null);
			noPermissionMessage(missingPerm);
			return;
		case NULL_PASSWORD:
			sendMessage(ChatColor.RED + "You must enter a password");
			return;
		default:
			break;
			
		}
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("password", password);
		
	}

}
