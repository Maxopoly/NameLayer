package vg.civcraft.mc.namelayer.group.log.abstr;

import java.util.UUID;

import vg.civcraft.mc.namelayer.group.log.LoggedGroupActionPersistence;

public abstract class PropertyChange extends LoggedGroupAction {

	protected final String oldValue;
	protected final String newValue;
	
	public PropertyChange(long time, UUID player, String oldValue, String newValue) {
		super(time, player);
		this.newValue = newValue;
		this.oldValue = oldValue;
	}

	@Override
	public LoggedGroupActionPersistence getPersistence() {
		return new LoggedGroupActionPersistence(time, player, newValue, oldValue, null);
	}
	
	public String getOldValue() {
		return oldValue;
	}
	
	public String getNewValue() {
		return newValue;
	}
}
