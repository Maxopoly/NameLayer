package vg.civcraft.mc.namelayer.misc;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import vg.civcraft.mc.civmodcore.playersettings.PlayerSettingAPI;
import vg.civcraft.mc.civmodcore.playersettings.gui.MenuSection;
import vg.civcraft.mc.civmodcore.playersettings.impl.BooleanSetting;
import vg.civcraft.mc.namelayer.NameLayerPlugin;

public class NameLayerSettingManager {

	private PlayerGroupSetting defaultGroup;
	private BooleanSetting autoAcceptInvites;

	public NameLayerSettingManager() {
		initSettings();
	}

	private void initSettings() {
		MenuSection menu = PlayerSettingAPI.getMainMenu().createMenuSection("NameLayer",
				"NameLayer and group related settings");
		defaultGroup = new PlayerGroupSetting(NameLayerPlugin.getInstance(), "Default group", "nlDefaultGroup",
				new ItemStack(Material.GOLDEN_HELMET),
				"The default group which will be used by commands if you don't specify one");
		PlayerSettingAPI.registerSetting(defaultGroup, menu);

		autoAcceptInvites = new BooleanSetting(NameLayerPlugin.getInstance(), false, "Auto accept group invites",
				"nlAutoAcceptInvites", "Should group invites be automatically accepted");
		PlayerSettingAPI.registerSetting(autoAcceptInvites, menu);
	}

	public PlayerGroupSetting getDefaultGroup() {
		return defaultGroup;
	}
	
	public BooleanSetting getAutoAcceptInvites() {
		return autoAcceptInvites;
	}

}
