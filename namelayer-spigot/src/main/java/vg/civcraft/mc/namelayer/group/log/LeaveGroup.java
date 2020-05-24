package vg.civcraft.mc.namelayer.group.log;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import vg.civcraft.mc.civmodcore.api.ItemAPI;

public class LeaveGroup extends MemberRankChange {

	public LeaveGroup(long time, UUID player, String rank) {
		super(time, player, rank);
	}

	@Override
	public ItemStack getGUIRepresentation() {
		ItemStack is = new ItemStack(Material.OAK_DOOR);
		ItemAPI.setDisplayName(is, String.format("%s%s%s left %s(%s)", ChatColor.GOLD,
				getPlayerName(), ChatColor.GREEN, ChatColor.YELLOW, rank));
		enrichItem(is);
		return is;
	}

	@Override
	public String getChatRepresentation() {
		return String.format("%s%s%s left %s(%s)", ChatColor.GOLD, getPlayerName(), ChatColor.GREEN, ChatColor.YELLOW, rank);
	}

}
