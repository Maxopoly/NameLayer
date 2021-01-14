package vg.civcraft.mc.namelayer.zeus.rabbit.groupchanges;

import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.model.ZeusLocation;
import com.github.maxopoly.zeus.rabbit.RabbitMessage;

import vg.civcraft.mc.namelayer.core.requests.GroupModifications;

public class LocalChatMessageMessage extends RabbitMessage {
	
	private UUID sender;
	private ZeusLocation location;
	private double range;
	private String message;

	public LocalChatMessageMessage(String transactionID, UUID sender, ZeusLocation location, double range, String message) {
		super(transactionID);
		this.sender = sender;
		this.location = location;
		this.range = range;
		this.message = message;
	}

	@Override
	protected void enrichJson(JSONObject json) {
		json.put("player", sender);
		JSONObject loc = new JSONObject();
		location.writeToJson(loc);
		json.put("loc", loc);
		json.put("range", range);
		json.put("message", message);
	}

	@Override
	public String getIdentifier() {
		return GroupModifications.SEND_LOCAL_MESSAGE;
	}

}
