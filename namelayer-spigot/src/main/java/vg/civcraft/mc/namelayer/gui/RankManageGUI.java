package vg.civcraft.mc.namelayer.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import vg.civcraft.mc.civmodcore.api.ItemAPI;
import vg.civcraft.mc.civmodcore.inventorygui.IClickable;
import vg.civcraft.mc.civmodcore.inventorygui.LClickable;
import vg.civcraft.mc.civmodcore.inventorygui.components.ComponableInventory;
import vg.civcraft.mc.civmodcore.inventorygui.components.ContentAligners;
import vg.civcraft.mc.civmodcore.inventorygui.components.Scrollbar;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.permission.GroupRank;
import vg.civcraft.mc.namelayer.permission.GroupRankHandler;

public class RankManageGUI extends NameLayerGroupGUI {

	private ComponableInventory inventory;
	private MainGroupGUI parent;
	
	public RankManageGUI(Group g, Player p, MainGroupGUI parent) {
		super(g, p);
		this.parent = parent;
		this.inventory = parent.getInventory();
	}
	
	public void showScreen() {
		List<IClickable> content = new ArrayList<>();
		GroupRankHandler rankHandler = group.getGroupRankHandler();
		for (GroupRank rank : rankHandler.getAllRanks()) {
			ItemStack is = GUIGroupOverview.getHashedItem(rank.getName().hashCode());
			ItemAPI.setDisplayName(is, ChatColor.GOLD + rank.getName());
			if (rank.getParent() != null) {
				ItemAPI.addLore(is, String.format("%sParent rank: %s", ChatColor.GOLD, rank.getParent().getName()));
			}
			else {
				ItemAPI.addLore(is, ChatColor.GOLD + "Owner rank with all permissions");
			}
			if (rank == rankHandler.getDefaultNonMemberRank()) {
				ItemAPI.addLore(is, ChatColor.AQUA + "The rank anyone not explicitly a member", ChatColor.AQUA + "  implicitly gets");
			}
			if (rank == rankHandler.getDefaultInvitationRank()) {
				ItemAPI.addLore(is, ChatColor.AQUA + "The rank invitations are for if no rank is specified");
			}
			if (rankHandler.isBlacklistedRank(rank)) {
				ItemAPI.addLore(is, ChatColor.DARK_AQUA + "Blacklist rank");
			}
			content.add(new LClickable(is, p -> detailEdit(rank)));
		}
		Scrollbar rankSection = new Scrollbar(content, 45, 45, ContentAligners.getCenteredInOrder(content.size(), 45));
		inventory.clear();
		inventory.addComponent(rankSection, i -> true);
	}
	
	private void detailEdit(GroupRank rank) {
		//add child
		//make default invitiation?
		//rename
		//delete
		//perms?
	}

}
