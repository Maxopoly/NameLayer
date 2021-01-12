package vg.civcraft.mc.namelayer.mc.rabbit.playerrequests;

import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.artemis.rabbit.MCRabbitMessage;
import com.github.maxopoly.zeus.model.ZeusLocation;

import vg.civcraft.mc.namelayer.core.requests.SendLocalMessage;

public class RabbitSendLocalChatMessage extends MCRabbitMessage {
	
	private UUID sender;
	private ZeusLocation location;
	private double range;
	private String message;

	public RabbitSendLocalChatMessage(UUID sender, ZeusLocation location, double range, String message) {
		this.sender = sender;
		this.location = location;
		this.range = range;
		this.message = message;
	}

	@Override
	protected void enrichJson(JSONObject json) {
		json.put("sender", sender);
		JSONObject loc = new JSONObject();
		location.writeToJson(loc);
		json.put("loc", loc);
		json.put("range", range);
		json.put("message", message);
	}

	@Override
	public String getIdentifier() {
		return SendLocalMessage.ID;
	}


}
