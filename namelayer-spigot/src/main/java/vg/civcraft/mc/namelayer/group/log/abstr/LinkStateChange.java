package vg.civcraft.mc.namelayer.group.log.abstr;

import java.util.UUID;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import vg.civcraft.mc.namelayer.group.log.LoggedGroupActionPersistence;

public abstract class LinkStateChange extends LoggedGroupAction {

	protected static final JsonParser jsonParser = new JsonParser();
	protected final String ownRankLinked;
	protected final String otherGroup;
	protected final String otherGroupRank;
	protected final boolean isSelfOrigin;
	
	public LinkStateChange(long time, UUID player, String ownRankLinked, String otherGroup, String otherGroupRank, boolean isSelfOrigin) {
		super(time, player);
		Preconditions.checkNotNull(ownRankLinked, "Own rank may not be null");
		Preconditions.checkNotNull(otherGroup, "Other group may not be null");
		Preconditions.checkNotNull(otherGroupRank, "Other group rank may not be null");
		this.ownRankLinked = ownRankLinked;
		this.otherGroup = otherGroup;
		this.otherGroupRank = otherGroup;
		this.isSelfOrigin = isSelfOrigin;
	}
	
	public String getOwnRankLinked() {
		return ownRankLinked;
	}
	
	public String getOtherGroup() {
		return otherGroup;
	}
	
	public String getRankLinkedOtherGroup() {
		return otherGroupRank;
	}
	
	public boolean isSelfOrigin() {
		return isSelfOrigin;
	}
	
	protected static String extractOtherRank(String extra) {
		JsonObject json = (JsonObject) jsonParser.parse(extra);
		return json.get("otherRank").getAsString();
	}
	
	protected static boolean extractIsOrigin(String extra) {
		JsonObject json = (JsonObject) jsonParser.parse(extra);
		return json.get("isOrigin").getAsBoolean();
	}

	@Override
	public LoggedGroupActionPersistence getPersistence() {
		JsonObject extra = new JsonObject();
		extra.addProperty("otherRank", otherGroupRank);
		extra.addProperty("isOrigin", isSelfOrigin);
		return new LoggedGroupActionPersistence(time, player, ownRankLinked, otherGroup, extra.toString());
	}
}
