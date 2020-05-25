package vg.civcraft.mc.namelayer.group.log.impl;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import vg.civcraft.mc.civmodcore.api.ItemAPI;
import vg.civcraft.mc.namelayer.group.log.LoggedGroupActionPersistence;
import vg.civcraft.mc.namelayer.group.log.abstr.MemberRankChange;

public class DeleteRank extends MemberRankChange {
	
	public static final String ID = "DELETE_RANK";

	public DeleteRank(long time, UUID player, String rank) {
		super(time, player, rank);
	}
	
	public DeleteRank(LoggedGroupActionPersistence persist) {
		this(persist.getTimeStamp(), persist.getPlayer(), persist.getRank());
	}

	@Override
	public ItemStack getGUIRepresentation() {
		ItemStack is = new ItemStack(Material.RED_TERRACOTTA);
		ItemAPI.setDisplayName(is, String.format("%s%s%s deleted the rank %s%s", ChatColor.GOLD, getPlayerName(),
				ChatColor.GREEN, ChatColor.YELLOW, rank));
		enrichItem(is);
		ItemAPI.addLore(is, String.format("%sRank deleted: %s%s", ChatColor.GOLD, ChatColor.AQUA, rank));
		return is;
	}

	@Override
	public String getChatRepresentation() {
		return String.format("%s%s%s deleted rank %s%s", ChatColor.GOLD, getPlayerName(),
				ChatColor.GREEN, ChatColor.YELLOW, rank);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}
	
}
