package vg.civcraft.mc.namelayer.core.log.impl;

import java.util.UUID;

import vg.civcraft.mc.namelayer.core.log.abstr.LoggedGroupActionPersistence;
import vg.civcraft.mc.namelayer.core.log.abstr.PropertyChange;

public class ChangeGroupName extends PropertyChange {
	
	public static final String ID = "CHANGE_GROUP_NAME";

	public ChangeGroupName(long time, UUID player, String oldValue, String newValue) {
		super(time, player, oldValue, newValue);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}
	
	public static ChangeGroupName load(LoggedGroupActionPersistence persist) {
		return new ChangeGroupName(persist.getTimeStamp(), persist.getPlayer(), persist.getRank(), persist.getName());
	}

}
