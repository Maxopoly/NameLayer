package vg.civcraft.mc.namelayer.mc.model.chat;

import org.bukkit.entity.Player;

public interface ChatMode {
	
	public enum Modes { LOCAL, GROUP, PM}
	
	String getInfoText();
	
	void processInput(String text, Player player);

}
