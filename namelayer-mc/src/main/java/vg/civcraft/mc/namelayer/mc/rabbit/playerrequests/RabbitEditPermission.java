package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;

import org.json.JSONObject;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;

public class RabbitEditPermission extends RabbitGroupAction {
	public enum FailureReason {
		GROUP_DOES_NOT_EXIST, NO_PERMISSION, RANK_DOES_NOT_EXIST, PERMISSION_DOES_NOT_EXIST, RANK_HAS_PERMISSION, RANK_LACKS_PERMISSION;
	}
	
	private boolean adding;
	private GroupRank rankName;
	private String permissionName;

	public RabbitEditPermission(UUID executor, Group groupName, boolean adding, GroupRank rankName, String permissionName) {
		super(executor, groupName.getName());
		this.adding = adding;
		this.rankName = rankName;
		this.permissionName = permissionName;
	}

	@Override
	public void handleReply(JSONObject reply, boolean success) {
		if (success) {
			sendMessage(String.format(""));
		}
		FailureReason reason = FailureReason.valueOf(reply.getString("reason"));
		switch (reason) {
			
		}
		
	}

	@Override
	protected void fillJson(JSONObject json) {
		json.put("adding", adding);
		json.put("rankName", rankName.getId());
		json.put("permissionName", permissionName);
	}

}
