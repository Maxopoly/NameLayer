package vg.civcraft.mc.namelayer.group.log.impl;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import vg.civcraft.mc.civmodcore.api.ItemAPI;
import vg.civcraft.mc.namelayer.group.log.LoggedGroupActionPersistence;
import vg.civcraft.mc.namelayer.group.log.abstr.LoggedGroupAction;

public class MergeGroup extends LoggedGroupAction {
	
	public static final String ID = "MERGE_GROUP";

	private String groupMergedIn;
	
	public MergeGroup(long time, UUID player, String groupMergedIn) {
		super(time, player);
		this.groupMergedIn = groupMergedIn;
	}
	
	public MergeGroup(LoggedGroupActionPersistence persist) {
		this(persist.getTimeStamp(), persist.getPlayer(), persist.getRank());
	}
	
	public String getGroupMergedIn() {
		return groupMergedIn;
	}

	@Override
	public ItemStack getGUIRepresentation() {
		ItemStack is = new ItemStack(Material.BIRCH_FENCE_GATE);
		ItemAPI.setDisplayName(is, String.format("%s%s%s merged %s%s%s in", ChatColor.GOLD, getPlayerName(),
				ChatColor.GREEN, ChatColor.GOLD, groupMergedIn, ChatColor.GREEN));
		enrichItem(is);
		ItemAPI.addLore(is, String.format("%Group merged in: %s%s", ChatColor.GOLD, ChatColor.AQUA, groupMergedIn));
		return is;
	}

	@Override
	public String getChatRepresentation() {
		return String.format("%s%s%s merged %s%s%s in", ChatColor.GOLD, getPlayerName(),
				ChatColor.GREEN, ChatColor.GOLD, groupMergedIn, ChatColor.GREEN);
	}

	@Override
	public LoggedGroupActionPersistence getPersistence() {
		return new LoggedGroupActionPersistence(time, player, groupMergedIn, null, null);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

}
