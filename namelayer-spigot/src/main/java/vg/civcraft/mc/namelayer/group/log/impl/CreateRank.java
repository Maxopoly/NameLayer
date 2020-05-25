package vg.civcraft.mc.namelayer.group.log.impl;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Preconditions;

import vg.civcraft.mc.civmodcore.api.ItemAPI;
import vg.civcraft.mc.namelayer.group.log.LoggedGroupActionPersistence;
import vg.civcraft.mc.namelayer.group.log.abstr.MemberRankChange;

public class CreateRank extends MemberRankChange {
	
	public static final String ID = "CREATE_RANK";

	private String parent;

	public CreateRank(long time, UUID player, String rank, String parent) {
		super(time, player, rank);
		Preconditions.checkNotNull(parent);
		this.parent = parent;
	}
	
	public CreateRank(LoggedGroupActionPersistence persist) {
		this(persist.getTimeStamp(), persist.getPlayer(), persist.getRank(), persist.getName());
	}

	public String getParentRank() {
		return parent;
	}

	@Override
	public ItemStack getGUIRepresentation() {
		ItemStack is = new ItemStack(Material.GREEN_TERRACOTTA);
		ItemAPI.setDisplayName(is, String.format("%s%s%s created the rank %s%s", ChatColor.GOLD, getPlayerName(),
				ChatColor.GREEN, ChatColor.YELLOW, rank));
		enrichItem(is);
		ItemAPI.addLore(is, String.format("%sRank name: %s%s", ChatColor.GOLD, ChatColor.AQUA, rank),
				String.format("%sParent rank: %s%s", ChatColor.GOLD, ChatColor.AQUA, parent));
		return is;
	}

	@Override
	public String getChatRepresentation() {
		return String.format("%s%s%s created rank %s%s%s (under %s%s)", ChatColor.GOLD, getPlayerName(),
				ChatColor.GREEN, ChatColor.YELLOW, rank, ChatColor.GREEN, ChatColor.YELLOW, parent);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}
}
