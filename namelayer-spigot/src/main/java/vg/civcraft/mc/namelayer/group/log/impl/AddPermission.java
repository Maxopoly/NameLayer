package vg.civcraft.mc.namelayer.group.log.impl;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import vg.civcraft.mc.civmodcore.api.ItemAPI;
import vg.civcraft.mc.namelayer.group.log.abstr.PermissionEdit;

public class AddPermission extends PermissionEdit {

	public AddPermission(long time, UUID player, String rank, String permission) {
		super(time, player, rank, permission);
	} 

@Override
public ItemStack getGUIRepresentation() {
	ItemStack is = new ItemStack(Material.OAK_SIGN);
	ItemAPI.setDisplayName(is, String.format("%s%s%s added perm to %s%s", ChatColor.GOLD, getPlayerName(),
			ChatColor.GREEN, ChatColor.GOLD, rank));
	enrichItem(is);
	ItemAPI.addLore(is, String.format("%sRank: %s%s", ChatColor.GOLD, ChatColor.AQUA, rank),
			String.format("%sPermission added: %s%s", ChatColor.GOLD, ChatColor.AQUA, permission));
	return is;
}

@Override
public String getChatRepresentation() {
	return String.format("%s%s%s added perm %s%s%s to %s%s", ChatColor.GOLD, getPlayerName(),
			ChatColor.GREEN, ChatColor.GOLD, permission, ChatColor.GREEN, ChatColor.YELLOW, rank);
}
}
