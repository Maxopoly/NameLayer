package vg.civcraft.mc.namelayer.group.log.impl;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import vg.civcraft.mc.civmodcore.api.ItemAPI;
import vg.civcraft.mc.namelayer.group.log.LoggedGroupActionPersistence;
import vg.civcraft.mc.namelayer.group.log.abstr.OtherMemberRankChange;

public class RemoveMember extends OtherMemberRankChange {
	
	public static final String ID = "REMOVE_MEMBER";

	public RemoveMember(long time, UUID player, String rank, UUID affectedPlayer) {
		super(time, player, rank, affectedPlayer);
	}
	
	public RemoveMember(LoggedGroupActionPersistence persist) {
		this(persist.getTimeStamp(), persist.getPlayer(), persist.getRank(), UUID.fromString(persist.getName()));
	}

	@Override
	public ItemStack getGUIRepresentation() {
		ItemStack is = new ItemStack(Material.OAK_SIGN);
		ItemAPI.setDisplayName(is, String.format("%s%s%s kicked %s%s", ChatColor.GOLD, getPlayerName(), ChatColor.GREEN,
				ChatColor.GOLD, getAffectedPlayerName()));
		enrichItem(is);
		ItemAPI.addLore(is,
				String.format("%Kicked player: %s%s", ChatColor.GOLD, ChatColor.AQUA, getAffectedPlayerName()),
				String.format("%sRank: %s%s", ChatColor.GOLD, ChatColor.AQUA, rank));
		return is;
	}

	@Override
	public String getChatRepresentation() {
		return String.format("%s%s%s kicked %s%s%s %s(%s)", ChatColor.GOLD, getPlayerName(), ChatColor.GREEN,
				ChatColor.GOLD, getAffectedPlayerName(), ChatColor.GREEN, ChatColor.YELLOW, rank);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

}
