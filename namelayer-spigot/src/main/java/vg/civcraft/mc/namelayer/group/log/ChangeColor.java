package vg.civcraft.mc.namelayer.group.log;

import java.util.UUID;

public class ChangeColor extends LoggedGroupAction {

	private String oldColor;
	private String newColor;
	
	public ChangeColor(long time, UUID player, String oldColor, String newColor) {
		super(time, player);
		this.oldColor = oldColor;
		this.newColor = newColor;
	}
	
	public String getOldColor() {
		return oldColor;
	}
	
	public String getNewColor() {
		return newColor;
	}

}
