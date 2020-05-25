package vg.civcraft.mc.namelayer.group.log.impl;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import vg.civcraft.mc.civmodcore.api.ItemAPI;
import vg.civcraft.mc.namelayer.group.log.LoggedGroupActionPersistence;
import vg.civcraft.mc.namelayer.group.log.abstr.PropertyChange;

public class ChangeRankName extends PropertyChange {
	
	public static final String ID = "RENAME_RANK";

	public ChangeRankName(long time, UUID player, String oldValue, String newValue) {
		super(time, player, oldValue, newValue);
	}
	
	public ChangeRankName(LoggedGroupActionPersistence persist) {
		this(persist.getTimeStamp(), persist.getPlayer(), persist.getRank(), persist.getName());
	}

	@Override
	public ItemStack getGUIRepresentation() {
		ItemStack is = new ItemStack(Material.OAK_SIGN);
		ItemAPI.setDisplayName(is,
				String.format("%s%s%s changed a ranks name", ChatColor.GOLD, getPlayerName(), ChatColor.GREEN));
		enrichItem(is);
		ItemAPI.addLore(is, String.format("%sOld name: %s%s", ChatColor.GOLD, ChatColor.AQUA, oldValue),
				String.format("%sNew name: %s%s", ChatColor.GOLD, ChatColor.AQUA, newValue));
		return is;
	}

	@Override
	public String getChatRepresentation() {
		return String.format("%s%s%s changed a rank name from %s to %s", ChatColor.GOLD, getPlayerName(), ChatColor.GREEN,
				oldValue, newValue);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

}
