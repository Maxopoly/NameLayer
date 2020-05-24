package vg.civcraft.mc.namelayer.group.log;

import java.util.UUID;

import org.bukkit.inventory.ItemStack;

public class PropertyChange extends LoggedGroupAction {

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

	@Override
	public ItemStack getGUIRepresentation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getChatRepresentation() {
		// TODO Auto-generated method stub
		return null;
	}

}
