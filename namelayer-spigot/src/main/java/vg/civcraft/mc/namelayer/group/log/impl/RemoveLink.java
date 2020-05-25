package vg.civcraft.mc.namelayer.group.log.impl;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import vg.civcraft.mc.civmodcore.api.ItemAPI;
import vg.civcraft.mc.namelayer.group.log.LoggedGroupActionPersistence;
import vg.civcraft.mc.namelayer.group.log.abstr.LinkStateChange;

public class RemoveLink extends LinkStateChange {
	
	public static final String ID = "REMOVE_LINK";

	public RemoveLink(long time, UUID player, String ownRankLinked, String otherGroup, String otherGroupRank,
			boolean isSelfOrigin) {
		super(time, player, ownRankLinked, otherGroup, otherGroupRank, isSelfOrigin);
	}
	
	public RemoveLink(LoggedGroupActionPersistence persist) {
		this(persist.getTimeStamp(), persist.getPlayer(), persist.getRank(), persist.getName(),
				extractOtherRank(persist.getExtraText()), extractIsOrigin(persist.getExtraText()));
	}

	@Override
	public ItemStack getGUIRepresentation() {
		ItemStack is = new ItemStack(Material.SPECTRAL_ARROW);
		if (isSelfOrigin) {
			ItemAPI.setDisplayName(is,
					String.format("%s%s%s unlinked %s%s%s from %s%s%s in %s%s", ChatColor.GOLD, getPlayerName(),
							ChatColor.GREEN, ChatColor.GOLD, ownRankLinked, ChatColor.GREEN, ChatColor.YELLOW,
							otherGroupRank, ChatColor.GREEN, ChatColor.YELLOW, otherGroup));
			enrichItem(is);
			ItemAPI.addLore(is, String.format("%sOwn rank: %s%s", ChatColor.GOLD, ChatColor.AQUA, ownRankLinked),
					String.format("%sOther group: %s%s", ChatColor.GOLD, ChatColor.AQUA, otherGroup),
					String.format("%sOther group rank: %s%s", ChatColor.GOLD, ChatColor.AQUA, otherGroupRank),
					String.format("%Link direction: %soutbound", ChatColor.GOLD, ChatColor.AQUA));
		} else {
			ItemAPI.setDisplayName(is,
					String.format("%s%s%s unlinked %s%s%s in %s%s%s from %s%s", ChatColor.GOLD, getPlayerName(),
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
			return String.format("%s%s%s unlinked %s%s%s from %s%s%s in %s%s", ChatColor.GOLD, getPlayerName(),
					ChatColor.GREEN, ChatColor.GOLD, ownRankLinked, ChatColor.GREEN, ChatColor.YELLOW,
					otherGroupRank, ChatColor.GREEN, ChatColor.YELLOW, otherGroup);
		}
		else {
			return String.format("%s%s%s unlinked %s%s%s in %s%s%s from %s%s", ChatColor.GOLD, getPlayerName(),
					ChatColor.GREEN, ChatColor.YELLOW, otherGroupRank, ChatColor.GREEN, ChatColor.YELLOW,
					otherGroup, ChatColor.GREEN, ChatColor.GOLD, ownRankLinked);
		}
	}

	@Override
	public String getIdentifier() {
		return ID;
	}
}
