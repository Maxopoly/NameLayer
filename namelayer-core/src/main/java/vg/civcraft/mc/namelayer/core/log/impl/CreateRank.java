package vg.civcraft.mc.namelayer.core.log.impl;

import java.util.UUID;

import com.google.common.base.Preconditions;

import vg.civcraft.mc.namelayer.core.log.abstr.MemberRankChange;

public class CreateRank extends MemberRankChange {
	
	public static final String ID = "CREATE_RANK";

	private String parent;

	public CreateRank(long time, UUID player, String rank, String parent) {
		super(time, player, rank);
		Preconditions.checkNotNull(parent);
		this.parent = parent;
	}

	public String getParentRank() {
		return parent;
	}

	@Override
	public String getIdentifier() {
		return ID;
	}
}
