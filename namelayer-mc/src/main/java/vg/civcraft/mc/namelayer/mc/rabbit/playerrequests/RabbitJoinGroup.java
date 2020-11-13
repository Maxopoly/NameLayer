package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;

public class RabbitJoinGroup extends RabbitGroupAction {
	public enum FailureReason {
		GROUP_DOES_NOT_EXIST, WRONG_PASSWORD, GROUP_HAS_NO_PASSWORD, ALREADY_MEMBER_OR_BLACKLISTED;
	}
	
	private String submittedPassword;

	public RabbitJoinGroup(UUID executor, String groupName, String submittedPassword) {
		super(executor, groupName);
		this.submittedPassword = submittedPassword;
	}

	@Override
	public void handleReply(JSONObject reply, boolean success) {
		Group group = getGroup();
		if (success) {
			String targetRank = reply.getString("targetRank");
			sendMessage(String.format("%You have been added to %s%s as a %s%s", ChatColor.GREEN, group.getColoredName(),
				ChatColor.GREEN, ChatColor.YELLOW, targetRank));
				return;
		}
		FailureReason reason = FailureReason.valueOf(reply.getString("reason"));
		switch (reason) {
		case ALREADY_MEMBER_OR_BLACKLISTED:
			sendMessage(String.format("%sYou either already are a member or blacklisted on %s", ChatColor.RED,
					group.getColoredName()));
			return;
		case GROUP_DOES_NOT_EXIST:
			groupDoesNotExistMessage();
			return;
		case GROUP_HAS_NO_PASSWORD:
			sendMessage(String.format("%s%s does not have a password set and can thus not be joined with one",
					group.getColoredName(), ChatColor.RED));
			return;
		case WRONG_PASSWORD:
			sendMessage(ChatColor.RED + "Wrong password");
			return;
		default:
			break;
			
		}
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("submittedPassword", submittedPassword);
		
	}

}
