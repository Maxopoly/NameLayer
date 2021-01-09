package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.GroupRankHandler;
import vg.civcraft.mc.namelayer.core.requests.JoinGroup;

public class RabbitJoinGroup extends RabbitGroupAction {

	
	private String submittedPassword;

	public RabbitJoinGroup(UUID executor, Group group, String submittedPassword) {
		super(executor, group.getName());
		this.submittedPassword = submittedPassword;
	}

	@Override
	public void handleReply(JSONObject reply, boolean success) {
		Group group = getGroup();
		if (success) {
			int rankID = reply.getInt("targetRank");
			GroupRankHandler handler = group.getGroupRankHandler();
			GroupRank targetRank = handler.getRank(rankID);
			sendMessage(String.format("%sYou have been added to %s%s as a %s%s", ChatColor.GREEN, group.getColoredName(),
				ChatColor.GREEN, ChatColor.YELLOW, targetRank.getName()));
				return;
		}
		JoinGroup.FailureReason reason = JoinGroup.FailureReason.valueOf(reply.getString("reason"));
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
	
	@Override
	public String getIdentifier() {
		return JoinGroup.REQUEST_ID;
	}

}
