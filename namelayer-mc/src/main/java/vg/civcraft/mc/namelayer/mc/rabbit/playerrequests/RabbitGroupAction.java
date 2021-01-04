package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.artemis.rabbit.MCStandardRequest;

import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.mc.GroupAPI;
import vg.civcraft.mc.namelayer.mc.util.MsgUtils;

public abstract class RabbitGroupAction extends MCStandardRequest {

	protected final UUID executor;
	protected final String groupName;

	public RabbitGroupAction(UUID executor, String groupName) {
		this.executor = executor;
		this.groupName = groupName;
	}

	@Override
	protected void enrichJson(JSONObject json) {
		json.put("player", executor);
		json.put("group", groupName);
		fillJson(json);
	}
	
	protected Group getGroup() {
		return GroupAPI.getGroup(groupName);
	}
	
	@Override
	public void handleReply(JSONObject reply) {
		boolean worked = reply.getBoolean("success");
		handleReply(reply, worked);
	}
	
	public abstract void handleReply(JSONObject reply, boolean success);

	protected abstract void fillJson(JSONObject json);

	protected void sendMessage(String msg) {
		MsgUtils.sendMsg(executor, msg);
	}
	
	protected void groupDoesNotExistMessage() {
		MsgUtils.sendGroupNotExistMsg(executor, groupName);
	}
	
	protected void playerDoesNotExistMessage(String targetPlayer) {
		MsgUtils.sendPlayerNotExistMsg(executor, targetPlayer);
	}
	
	protected void noPermissionMessage(String permNeeded) {
		MsgUtils.sendNoPermissionMsg(executor, permNeeded, groupName);
	}

}
