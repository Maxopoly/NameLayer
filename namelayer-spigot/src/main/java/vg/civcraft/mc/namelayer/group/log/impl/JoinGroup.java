package vg.civcraft.mc.namelayer.group.log.impl;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import vg.civcraft.mc.civmodcore.api.ItemAPI;
import vg.civcraft.mc.namelayer.group.log.abstr.MemberRankChange;

public class JoinGroup extends MemberRankChange {

	public JoinGroup(long time, UUID player, String rank) {
		super(time, player, rank);
	}

	@Override
	public ItemStack getGUIRepresentation() {
		ItemStack is = new ItemStack(Material.PAPER);
		ItemAPI.setDisplayName(is, String.format("%s%s%s joined via password %s(%s)", ChatColor.GOLD,
				getPlayerName(), ChatColor.GREEN, ChatColor.YELLOW, rank));
		enrichItem(is);
		return is;
	}

	@Override
	public String getChatRepresentation() {
		return String.format("%s%s%s joined via password %s(%s)", ChatColor.GOLD, getPlayerName(), ChatColor.GREEN, ChatColor.YELLOW, rank);
	}

}
