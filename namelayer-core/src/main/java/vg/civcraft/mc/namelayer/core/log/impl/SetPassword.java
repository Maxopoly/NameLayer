package vg.civcraft.mc.namelayer.core.log.impl;

import java.util.UUID;

import vg.civcraft.mc.namelayer.core.log.abstr.LoggedGroupActionPersistence;
import vg.civcraft.mc.namelayer.core.log.abstr.PropertyChange;

public class SetPassword extends PropertyChange {

	public static final String ID = "SET_PASSWORD";
	
	public SetPassword(long time, UUID player, String oldValue, String newValue) {
		super(time, player, oldValue, newValue);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}
	
	public static SetPassword load(LoggedGroupActionPersistence persist) {
		return new SetPassword(persist.getTimeStamp(), persist.getPlayer(), persist.getRank(), persist.getName());
	}

}
