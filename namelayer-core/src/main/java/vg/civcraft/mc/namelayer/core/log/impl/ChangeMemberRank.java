package vg.civcraft.mc.namelayer.core.log.impl;

import java.util.UUID;

import org.json.JSONObject;

import com.google.common.base.Preconditions;

import vg.civcraft.mc.namelayer.core.log.abstr.OtherMemberRankChange;

public class ChangeMemberRank extends OtherMemberRankChange {
	
	public static final String ID = "PROMOTE_MEMBER";

	private String oldRank;

	public ChangeMemberRank(long time, UUID player, String rank, UUID affectedPlayer, String oldRank) {
		super(time, player, rank, affectedPlayer);
		Preconditions.checkNotNull(oldRank, "Previous rank may not be null");
		this.oldRank = oldRank;
	}
	
	protected void fillJson(JSONObject json) {
		super.fillJson(json);
		json.put("old_rank", oldRank);
	}

	public String getOldRank() {
		return oldRank;
	}

	@Override
	public String getIdentifier() {
		return ID;
	}
}
