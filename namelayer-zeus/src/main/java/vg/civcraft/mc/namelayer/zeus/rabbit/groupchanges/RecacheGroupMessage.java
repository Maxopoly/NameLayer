package vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges;

import org.json.JSONObject;

import com.github.maxopoly.zeus.ZeusMain;
import com.github.maxopoly.zeus.rabbit.RabbitMessage;

import vg.civcraft.mc.namelayer.core.Group;

public class RecacheGroupMessage extends RabbitMessage {
	
	private Group group;

	public RecacheGroupMessage(Group group) {
		super(ZeusMain.getInstance().getTransactionIdManager().pullNewTicket());
		this.group = group;
	}

	@Override
	public String getIdentifier() {
		return "nl_recache_group";
	}

	@Override
	protected void enrichJson(JSONObject json) {
		json.put("group", group.serialize());
	}

}
