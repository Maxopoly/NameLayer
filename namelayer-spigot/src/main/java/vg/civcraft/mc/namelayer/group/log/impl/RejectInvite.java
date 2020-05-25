package vg.civcraft.mc.namelayer.group.log.impl;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import vg.civcraft.mc.civmodcore.api.ItemAPI;
import vg.civcraft.mc.namelayer.group.log.LoggedGroupActionPersistence;
import vg.civcraft.mc.namelayer.group.log.abstr.MemberRankChange;

public class RejectInvite extends MemberRankChange {
	
	public static final String ID = "REJECT_INVITE";

	public RejectInvite(long time, UUID player, String rank) {
		super(time, player, rank);
	}
	
	public RejectInvite(LoggedGroupActionPersistence persist) {
		this(persist.getTimeStamp(), persist.getPlayer(), persist.getRank());
	}

	@Override
	public ItemStack getGUIRepresentation() {
		ItemStack is = new ItemStack(Material.COBBLESTONE_WALL);
		ItemAPI.setDisplayName(is, String.format("%s%s%s rejected an invite as %s%s", ChatColor.GOLD, getPlayerName(),
				ChatColor.GREEN, ChatColor.YELLOW, rank));
		enrichItem(is);
		return is;
	}

	@Override
	public String getChatRepresentation() {
		return String.format("%s%s%s rejected an invite as %s%s", ChatColor.GOLD, getPlayerName(), ChatColor.GREEN,
				ChatColor.YELLOW, rank);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

}
