package vg.civcraft.mc.namelayer.group.log.impl;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import vg.civcraft.mc.civmodcore.api.ItemAPI;
import vg.civcraft.mc.namelayer.group.log.abstr.LinkStateChange;

public class AddLink extends LinkStateChange {

	public AddLink(long time, UUID player, String ownRankLinked, String otherGroup, String otherGroupRank,
			boolean isSelfOrigin) {
		super(time, player, ownRankLinked, otherGroup, otherGroupRank, isSelfOrigin);
	}

	@Override
	public ItemStack getGUIRepresentation() {
		ItemStack is = new ItemStack(Material.SPECTRAL_ARROW);
		if (isSelfOrigin) {
			ItemAPI.setDisplayName(is,
					String.format("%s%s%s linked %s%s%s to %s%s%s in %s%s", ChatColor.GOLD, getPlayerName(),
							ChatColor.GREEN, ChatColor.GOLD, ownRankLinked, ChatColor.GREEN, ChatColor.YELLOW,
							otherGroupRank, ChatColor.GREEN, ChatColor.YELLOW, otherGroup));
			enrichItem(is);
			ItemAPI.addLore(is, String.format("%sOwn rank: %s%s", ChatColor.GOLD, ChatColor.AQUA, ownRankLinked),
					String.format("%sOther group: %s%s", ChatColor.GOLD, ChatColor.AQUA, otherGroup),
					String.format("%sOther group rank: %s%s", ChatColor.GOLD, ChatColor.AQUA, otherGroupRank),
					String.format("%Link direction: %soutbound", ChatColor.GOLD, ChatColor.AQUA));
		} else {
			ItemAPI.setDisplayName(is,
					String.format("%s%s%s linked %s%s%s in %s%s%s to %s%s", ChatColor.GOLD, getPlayerName(),
							ChatColor.GREEN, ChatColor.YELLOW, otherGroupRank, ChatColor.GREEN, ChatColor.YELLOW,
							otherGroup, ChatColor.GREEN, ChatColor.GOLD, ownRankLinked));
			enrichItem(is);
			ItemAPI.addLore(is, String.format("%sOwn rank: %s%s", ChatColor.GOLD, ChatColor.AQUA, ownRankLinked),
					String.format("%sOther group: %s%s", ChatColor.GOLD, ChatColor.AQUA, otherGroup),
					String.format("%sOther group rank: %s%s", ChatColor.GOLD, ChatColor.AQUA, otherGroupRank),
					String.format("%Link direction: %sinbound", ChatColor.GOLD, ChatColor.AQUA));
		}
		return is;
	}

	@Override
	public String getChatRepresentation() {
		if (isSelfOrigin) {
			return String.format("%s%s%s linked %s%s%s to %s%s%s in %s%s", ChatColor.GOLD, getPlayerName(),
					ChatColor.GREEN, ChatColor.GOLD, ownRankLinked, ChatColor.GREEN, ChatColor.YELLOW,
					otherGroupRank, ChatColor.GREEN, ChatColor.YELLOW, otherGroup);
		}
		else {
			return String.format("%s%s%s linked %s%s%s in %s%s%s to %s%s", ChatColor.GOLD, getPlayerName(),
					ChatColor.GREEN, ChatColor.YELLOW, otherGroupRank, ChatColor.GREEN, ChatColor.YELLOW,
					otherGroup, ChatColor.GREEN, ChatColor.GOLD, ownRankLinked);
		}
	}

}
