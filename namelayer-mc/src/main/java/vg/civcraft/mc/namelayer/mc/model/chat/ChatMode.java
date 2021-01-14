package vg.civcraft.mc.namelayer.mc.model.chat;

import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.playersettings.impl.StringSetting;

public interface ChatMode {
	
	public enum Modes { LOCAL, GROUP, PM}
	
	String getInfoText();
	
	void processInput(String text, Player player);
	
	void setInternalStorage(Player player, StringSetting modeSetting, StringSetting valueSetting);

}
