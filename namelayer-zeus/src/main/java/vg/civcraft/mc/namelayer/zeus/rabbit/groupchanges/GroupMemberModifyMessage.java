package vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges;

import java.util.UUID;

import org.json.JSONObject;

public abstract class GroupMemberModifyMessage extends GroupChangeMessage {

	private UUID player;
	
	public GroupMemberModifyMessage(int groupID, UUID player) {
		super(groupID);
		this.player = player;
	}

	@Override
	protected void enrichJson(JSONObject json) {
		super.enrichJson(json);
		json.put("player", player.toString());
	}

}
