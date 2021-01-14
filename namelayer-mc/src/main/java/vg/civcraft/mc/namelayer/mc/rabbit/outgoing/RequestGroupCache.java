package vg.civcraft.mc.namelayer.mc.rabbit.outgoing;

import org.json.JSONObject;

import com.github.maxopoly.artemis.ArtemisPlugin;
import com.github.maxopoly.zeus.rabbit.RabbitMessage;

import vg.civcraft.mc.namelayer.core.requests.GroupModifications;

public class RequestGroupCache extends RabbitMessage {
	
	private int groupID;

	public RequestGroupCache(int groupID) {
		super(ArtemisPlugin.getInstance().getTransactionIdManager().pullNewTicket());
		this.groupID = groupID;
	}

	@Override
	protected void enrichJson(JSONObject json) {
		json.put("group_id", groupID);
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.GET_GROUP;
	}

}
