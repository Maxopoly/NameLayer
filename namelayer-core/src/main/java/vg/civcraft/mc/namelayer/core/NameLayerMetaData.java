package vg.civcraft.mc.namelayer.core;

import java.util.UUID;

import com.google.common.base.Preconditions;

public final class NameLayerMetaData extends GroupMetaData {
	
	public static final String CREATOR_KEY = "nl_creator";
	public static final String CHAT_COLOR_KEY =  "nl_color";
	public static final String PASSWORD_KEY = "nl_password";
	public static final String CREATION_TIME_KEY = "nl_creation";
	public static final String LAST_REFRESH_KEY = "nl_last_refresh";

	public NameLayerMetaData(Group group) {
		super(group);
	}
	
	public long getLastRefresh() {
		return Long.parseLong(getRawData(LAST_REFRESH_KEY));
	}
	
	public void setLastRefresh(long unixTime) {
		setData(LAST_REFRESH_KEY, String.valueOf(unixTime));
	}
	
	public String getChatColor() {
		return getRawData(CHAT_COLOR_KEY);
	}
	
	public long getCreationTime() {
		return Long.parseLong(getRawData(CREATION_TIME_KEY));
	}
	
	public void setCreator(UUID creator) {
		setData(CREATOR_KEY, creator.toString());
	}
	
	public void setChatColor(String color) {
		Preconditions.checkNotNull(color, "Color may not be null");
		setData(CHAT_COLOR_KEY, color);
	}
	
	public void setPassword(String password) {
		setData(PASSWORD_KEY, password);
	}
	
	public String getPassword() {
		return getRawData(PASSWORD_KEY);
	}

}
