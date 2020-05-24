package vg.civcraft.mc.namelayer.group;

import java.util.UUID;

import org.bukkit.ChatColor;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;

import vg.civcraft.mc.namelayer.group.meta.GroupMetaData;

public final class NameLayerMetaData extends GroupMetaData {
	
	private UUID creator;
	private String password;
	private ChatColor color;
	private final long creationTime;
	private long lastRefresh;
	
	private NameLayerMetaData(UUID creator, String password, ChatColor color, long creationTime, long lastRefresh) {
		super();
		this.creator = creator;
		this.password = password;
		this.color = color;
		this.creationTime = creationTime;
	}
	
	public static NameLayerMetaData createNew() {
		long now = System.currentTimeMillis();
		return new NameLayerMetaData(UUID.randomUUID(), null, ChatColor.WHITE, now, now);
	}
	
	public static NameLayerMetaData load(JsonObject json) {
		UUID creator = UUID.fromString(json.get("creator").getAsString());
		String password = null;
		if (json.has("password")) {
			password = json.get("password").getAsString();
		}
		ChatColor color = ChatColor.valueOf(json.get("color").getAsString());
		long creationTime = json.get("creation_time").getAsLong();
		long updateTime = json.get("update_time").getAsLong();
		return new NameLayerMetaData(creator, password, color, creationTime, updateTime);
	}

	@Override
	public void serialize(JsonObject json) {
		json.addProperty("creator", creator.toString());
		if (password != null) {
			json.addProperty("password", password);
		}
		json.addProperty("color", color.toString());
		json.addProperty("creation_time", creationTime);
		json.addProperty("update_time", lastRefresh);
	}
	
	public long getLastRefresh() {
		return lastRefresh;
	}
	
	public void setLastRefresh(long unixTime) {
		this.lastRefresh = unixTime;
		setDirty();
	}
	
	public ChatColor getChatColor() {
		return color;
	}
	
	public long getCreationTime() {
		return creationTime;
	}
	
	public void setCreator(UUID creator) {
		this.creator = creator;
		setDirty();
	}
	
	public void setChatColor(ChatColor color) {
		Preconditions.checkNotNull(color, "Color may not be null");
		this.color = color;
		setDirty();
	}
	
	public void setPassword(String password) {
		this.password = password;
		setDirty();
	}
	
	public String getPassword() {
		return password;
	}

}
