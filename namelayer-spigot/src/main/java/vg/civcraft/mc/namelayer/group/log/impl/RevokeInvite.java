package vg.civcraft.mc.namelayer.group.log.impl;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import vg.civcraft.mc.civmodcore.api.ItemAPI;
import vg.civcraft.mc.namelayer.group.log.abstr.OtherMemberRankChange;

public class RevokeInvite extends OtherMemberRankChange {

	public RevokeInvite(long time, UUID player, String rank, UUID affectedPlayer) {
		super(time, player, rank, affectedPlayer);
	}

	@Override
	public ItemStack getGUIRepresentation() {
		ItemStack is = new ItemStack(Material.OAK_SIGN);
		ItemAPI.setDisplayName(is, String.format("%s%s%s revoked invite for %s%s", ChatColor.GOLD, getPlayerName(),
				ChatColor.GREEN, ChatColor.GOLD, getAffectedPlayerName()));
		enrichItem(is);
		ItemAPI.addLore(is, String.format("%sRank invited to: %s%s", ChatColor.GOLD, ChatColor.AQUA, rank),
				String.format("%sInvited player: %s%s", ChatColor.GOLD, ChatColor.AQUA, getAffectedPlayerName()));
		return is;
	}

	@Override
	public String getChatRepresentation() {
		return String.format("%s%s%s revoked invite for %s%s%s as %s%s", ChatColor.GOLD, getPlayerName(),
				ChatColor.GREEN, ChatColor.GOLD, getAffectedPlayerName(), ChatColor.GREEN, ChatColor.YELLOW, rank);
	}
}
