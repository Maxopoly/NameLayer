package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;

import org.json.JSONObject;

import net.md_5.bungee.api.ChatColor;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.requests.DeleteRank;

public class RabbitDeleteRank extends RabbitGroupAction {

	private GroupRank rankToDelete;
	
	public RabbitDeleteRank(UUID executor, Group group, GroupRank rankToDelete) {
		super(executor, group.getName());
		this.rankToDelete = rankToDelete;
	}

	@Override
	public void handleReply(JSONObject reply, boolean success) {
		Group group = getGroup();
		if (success) {
			sendMessage(String.format("%sThe rank %s%s%s in %s%s has been deleted", ChatColor.GREEN, ChatColor.GOLD,
				rankToDelete.getName(), ChatColor.GREEN, group.getColoredName(), ChatColor.GREEN));
			return;
		}
		DeleteRank.FailureReason reason = DeleteRank.FailureReason.valueOf(reply.getString("reason"));
		switch (reason) {
		case GROUP_DOES_NOT_EXIST:
			groupDoesNotExistMessage();
			return;	
		case NO_PERMISSION:
			String missingPerm = reply.optString("missing_perm", null);
			noPermissionMessage(missingPerm);
			return;
		case RANK_DOES_NOT_EXIST:
			sendMessage(String.format("%s%s rank does not exist", rankToDelete.getName(), ChatColor.RED));
			return;
		case RANK_HAS_CHILDREN:
			sendMessage(String.format("%sThe rank %s%s%s has children and can not be deleted before its children are deleted",
					ChatColor.RED, ChatColor.YELLOW, rankToDelete.getName(), ChatColor.RED));
			return;
		case LAST_REMAINING_RANK:
			sendMessage(String.format("%sYou can not delete the rank %s%s%s of the group %s%s, because it is the last remaining rank",
					ChatColor.RED, ChatColor.GOLD, rankToDelete.getName(), ChatColor.RED, group.getName(),
					ChatColor.RED));
			return;
		case DEFAULT_NON_MEMBER_RANK:
			sendMessage(String.format("%sYou can not delete the default type for non-members", ChatColor.RED));
			return;	
		case STILL_HAS_MEMBERS:
			int amountOfMembers = reply.getInt("amountOfMembers");
			sendMessage(String.format("%sThe rank %s%s%s still has %d members and can not be deleted until all of them have been removed",
					ChatColor.RED, ChatColor.YELLOW, rankToDelete.getName(), ChatColor.RED, amountOfMembers));
			return;
		case HAS_INCOMING_LINKS:
			sendMessage(String.format("%sThe rank %s%s%s has an incoming link and can not be deleted until that link has been removed",
						ChatColor.RED, ChatColor.YELLOW, rankToDelete.getName(), ChatColor.RED));
			return;
		case HAS_OUTGOING_LINKS:
			sendMessage(String.format("%sThe rank %s%s%s has an outgoing link and can not be deleted until that link has been removed",
						ChatColor.RED, ChatColor.YELLOW, rankToDelete.getName(), ChatColor.RED));
			return;		
		default:
			break;
		}
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("rankToDelete", rankToDelete.getId());	
	}

	@Override
	public String getIdentifier() {
		return DeleteRank.REQUEST_ID;
	}

}
