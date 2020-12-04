package vg.civcraft.mc.namelayer.core.log.abstr;

import java.util.UUID;

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

}
