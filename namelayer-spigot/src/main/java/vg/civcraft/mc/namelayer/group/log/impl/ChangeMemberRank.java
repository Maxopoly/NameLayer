package vg.civcraft.mc.namelayer.group.log.impl;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Preconditions;

import vg.civcraft.mc.civmodcore.api.ItemAPI;
import vg.civcraft.mc.namelayer.group.log.LoggedGroupActionPersistence;
import vg.civcraft.mc.namelayer.group.log.abstr.OtherMemberRankChange;

public class ChangeMemberRank extends OtherMemberRankChange {

	private String oldRank;

	public ChangeMemberRank(long time, UUID player, String rank, UUID affectedPlayer, String oldRank) {
		super(time, player, rank, affectedPlayer);
		Preconditions.checkNotNull(oldRank, "Previous rank may not be null");
		this.oldRank = oldRank;
	}

	public String getOldRank() {
		return oldRank;
	}

	@Override
	public ItemStack getGUIRepresentation() {
		ItemStack is = new ItemStack(Material.OAK_SIGN);
		ItemAPI.setDisplayName(is, String.format("%s%s%s promoted %s%s%s to %s%s", ChatColor.GOLD, getPlayerName(),
				ChatColor.GREEN, ChatColor.GOLD, getAffectedPlayerName(), ChatColor.GREEN, ChatColor.YELLOW, rank));
		enrichItem(is);
		ItemAPI.addLore(is, String.format("%sRank: %s%s", ChatColor.GOLD, ChatColor.AQUA, rank),
				String.format("%Promoted player: %s%s", ChatColor.GOLD, ChatColor.AQUA, getAffectedPlayerName()),
				String.format("%Previous rank: %s%s", ChatColor.GOLD, ChatColor.AQUA, oldRank));
		return is;
	}

	@Override
	public String getChatRepresentation() {
		return String.format("%s%s%s promoted %s%s%s to %s%s", ChatColor.GOLD, getPlayerName(), ChatColor.GREEN,
				ChatColor.GOLD, getAffectedPlayerName(), ChatColor.GREEN, ChatColor.YELLOW, rank);
	}

	@Override
	public LoggedGroupActionPersistence getPersistence() {
		return new LoggedGroupActionPersistence(time, player, rank, affectedPlayer.toString(), oldRank);
	}
}
