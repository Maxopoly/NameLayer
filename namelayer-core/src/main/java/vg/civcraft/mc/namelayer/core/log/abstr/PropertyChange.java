package vg.civcraft.mc.namelayer.core.log.abstr;

import java.util.UUID;

import org.json.JSONObject;

public abstract class PropertyChange extends LoggedGroupAction {

	protected final String oldValue;
	protected final String newValue;
	
	public PropertyChange(long time, UUID player, String oldValue, String newValue) {
		super(time, player);
		this.newValue = newValue;
		this.oldValue = oldValue;
	}
	
	protected void fillJson(JSONObject json) {
		json.put("old_value", oldValue);
		json.put("new_value", newValue);
	}
	
	public String getOldValue() {
		return oldValue;
	}
	
	public String getNewValue() {
		return newValue;
	}
}
