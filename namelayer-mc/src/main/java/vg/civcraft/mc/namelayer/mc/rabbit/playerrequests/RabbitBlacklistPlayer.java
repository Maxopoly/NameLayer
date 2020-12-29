package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.requests.BlacklistPlayer;
import vg.civcraft.mc.namelayer.mc.util.MsgUtils;

public class RabbitBlacklistPlayer extends RabbitGroupAction {
	
	private String targetPlayer;
	private GroupRank rank;

	public RabbitBlacklistPlayer(UUID executor, Group group, String targetPlayer, GroupRank rank) {
		super(executor, group.getName());
		this.targetPlayer = targetPlayer;
		this.rank = rank;
	}

	@Override
	public void handleReply(JSONObject reply, boolean success) {
		Group group = getGroup();
		if (success) {
			sendMessage(String.format("%s%s %shas been blacklisted as %s%s%s in %s", ChatColor.YELLOW,
				this.targetPlayer, ChatColor.GREEN, ChatColor.YELLOW, rank.getName(),
				ChatColor.YELLOW, group.getColoredName()));
		}
		BlacklistPlayer.FailureReason reason = BlacklistPlayer.FailureReason.valueOf(reply.getString("reason"));
		switch (reason) {
		case GROUP_DOES_NOT_EXIST:
			groupDoesNotExistMessage();
			return;
		case PLAYER_DOES_NOT_EXIST:
			playerDoesNotExistMessage(this.targetPlayer);
			return;
		case RANK_DOES_NOT_EXIST:
		case NO_PERMISSION:
			MsgUtils.sendRankNotExistMsg(executor, group.getName(), rank.getName());
			return;
		case NOT_BLACKLISTED_RANK:
			sendMessage(String.format("%s%s%s is not a blacklist rank in %s", ChatColor.YELLOW, rank.getName(),
					ChatColor.RED, getGroup().getColoredName()));
			return;	
		default:
			break;
			
		}
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("target_player", targetPlayer);
		json.put("rank_id", rank.getId());
	}

	@Override
	public String getIdentifier() {
		return BlacklistPlayer.REQUEST_ID;
	}

}
