package vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges;

import org.json.JSONObject;

import com.github.maxopoly.zeus.ZeusMain;
import com.github.maxopoly.zeus.rabbit.RabbitMessage;

public abstract class GroupChangeMessage extends RabbitMessage {

	private int groupID;
	
	public GroupChangeMessage(int groupID) {
		super(ZeusMain.getInstance().getTransactionIdManager().pullNewTicket());
		this.groupID = groupID;
	}

	@Override
	protected void enrichJson(JSONObject json) {
		json.put("group_id", groupID);
		fillJson(json);
	}
	
	protected abstract void fillJson(JSONObject json);
}
