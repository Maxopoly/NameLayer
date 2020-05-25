package vg.civcraft.mc.namelayer.group.log.impl;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import vg.civcraft.mc.civmodcore.api.ItemAPI;
import vg.civcraft.mc.namelayer.group.log.LoggedGroupActionPersistence;
import vg.civcraft.mc.namelayer.group.log.abstr.PermissionEdit;

public class RemovePermission extends PermissionEdit {
	
	public static final String ID = "REMOVE_PERMISSION";

	public RemovePermission(long time, UUID player, String rank, String permission) {
		super(time, player, rank, permission);
	}
	
	public RemovePermission(LoggedGroupActionPersistence persist) {
		this(persist.getTimeStamp(), persist.getPlayer(), persist.getRank(), persist.getName());
	}

	@Override
	public ItemStack getGUIRepresentation() {
		ItemStack is = new ItemStack(Material.OAK_SIGN);
		ItemAPI.setDisplayName(is, String.format("%s%s%s removed perm from %s%s", ChatColor.GOLD, getPlayerName(),
				ChatColor.GREEN, ChatColor.GOLD, rank));
		enrichItem(is);
		ItemAPI.addLore(is, String.format("%sRank: %s%s", ChatColor.GOLD, ChatColor.AQUA, rank),
				String.format("%sPermission removed: %s%s", ChatColor.GOLD, ChatColor.AQUA, permission));
		return is;
	}

	@Override
	public String getChatRepresentation() {
		return String.format("%s%s%s removed perm %s%s%s from %s%s", ChatColor.GOLD, getPlayerName(),
				ChatColor.GREEN, ChatColor.GOLD, permission, ChatColor.GREEN, ChatColor.YELLOW, rank);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

}
