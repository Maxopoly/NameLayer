package vg.civcraft.mc.namelayer.core.log.impl;

import java.util.UUID;

import vg.civcraft.mc.namelayer.core.log.abstr.PropertyChange;

public class ChangeRankName extends PropertyChange {
	
	public static final String ID = "RENAME_RANK";

	public ChangeRankName(long time, UUID player, String oldValue, String newValue) {
		super(time, player, oldValue, newValue);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

}
