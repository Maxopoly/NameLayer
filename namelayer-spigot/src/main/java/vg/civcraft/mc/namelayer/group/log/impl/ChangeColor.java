package vg.civcraft.mc.namelayer.group.log.impl;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import vg.civcraft.mc.civmodcore.api.ItemAPI;
import vg.civcraft.mc.namelayer.group.log.LoggedGroupActionPersistence;
import vg.civcraft.mc.namelayer.group.log.abstr.PropertyChange;

public class ChangeColor extends PropertyChange {
	
	public static final String ID = "CHANGE_COLOR";

	public ChangeColor(long time, UUID player, String oldValue, String newValue) {
		super(time, player, oldValue, newValue);
	}
	
	public ChangeColor(LoggedGroupActionPersistence persist) {
		this(persist.getTimeStamp(), persist.getPlayer(), persist.getRank(), persist.getName());
	}

	@Override
	public ItemStack getGUIRepresentation() {
		ItemStack is = new ItemStack(Material.OAK_SIGN);
		ItemAPI.setDisplayName(is,
				String.format("%s%s%s changed the group color to %s", ChatColor.GOLD, getPlayerName(), ChatColor.GREEN, newValue));
		enrichItem(is);
		ItemAPI.addLore(is, String.format("%sOld color: %s%s", ChatColor.GOLD, ChatColor.AQUA, oldValue),
				String.format("%sNew color: %s%s", ChatColor.GOLD, ChatColor.AQUA, newValue));
		return is;
	}

	@Override
	public String getChatRepresentation() {
		return String.format("%s%s%s changed group color from %s%s to %s", ChatColor.GOLD, getPlayerName(), ChatColor.GREEN,
				oldValue, ChatColor.GREEN, newValue);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

}
