package vg.civcraft.mc.namelayer.mc.model.chat;

import org.bukkit.entity.Player;

public interface ChatMode {
	
	String getInfoText();
	
	void processInput(String text, Player player);

}
