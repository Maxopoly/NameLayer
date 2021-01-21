package vg.civcraft.mc.namelayer.core.log.abstr;

import java.util.UUID;

import org.json.JSONObject;

public class LoggedGroupActionPersistence {
	
	private UUID player;
	private long time;
	private String rank;
	private String name;
	private String extraText;
	
	public LoggedGroupActionPersistence(long time, UUID player, String rank, String name, String extraText) {
		this.player = player;
		this.time = time;
		this.rank = rank;
		this.name = name;
		this.extraText = extraText;
	}
	
	public UUID getPlayer() {
		return player;
	}
	
	public long getTimeStamp() {
		return time;
	}
	
	
	public String getRank() {
		return rank;
	}
	
	public String getName() {
		return name;
	}
	
	public String getExtraText() {
		return extraText;
	}
	
	public JSONObject toJson() {
		JSONObject json = new JSONObject();
		json.put("player", player);
		json.put("time", time);
		json.put("rank", rank);
		json.put("name", name);
		json.put("extra", extraText);
		return json;
	}
	
	public static LoggedGroupActionPersistence fromJSON(JSONObject json) {
		UUID player = UUID.fromString(json.getString("player"));
		long time = json.getLong("time");
		String rank = json.optString("rank");
		String name = json.optString("name");
		String extra = json.optString("extra");
		return new LoggedGroupActionPersistence(time, player, rank, name, extra);		
	}

}
