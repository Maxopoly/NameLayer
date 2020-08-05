package vg.civcraft.mc.namelayer.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import vg.civcraft.mc.civmodcore.api.ItemAPI;
import vg.civcraft.mc.civmodcore.inventorygui.DecorationStack;
import vg.civcraft.mc.civmodcore.inventorygui.IClickable;
import vg.civcraft.mc.civmodcore.inventorygui.LClickable;
import vg.civcraft.mc.civmodcore.inventorygui.components.ComponableInventory;
import vg.civcraft.mc.civmodcore.inventorygui.components.ContentAligners;
import vg.civcraft.mc.civmodcore.inventorygui.components.Scrollbar;
import vg.civcraft.mc.namelayer.GroupAPI;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.permission.GroupRank;
import vg.civcraft.mc.namelayer.permission.GroupRankHandler;
import vg.civcraft.mc.namelayer.permission.PermissionType;

public class PermissionManageGUI extends NameLayerGroupGUI {

	private AdminFunctionsGUI parent;
	private ComponableInventory inventory;
	private Scrollbar rankSection;

	public PermissionManageGUI(Group g, Player p, AdminFunctionsGUI parent) {
		super(g, p);
		this.parent = parent;
		this.inventory = parent.getInventory();
	}

	public void reconstruct() {
		if (rankSection == null) {
			List<IClickable> content = new ArrayList<>();
			GroupRankHandler rankHandler = group.getGroupRankHandler();
			for (GroupRank rank : rankHandler.getAllRanks()) {
				if (rank == rankHandler.getOwnerRank()) {
					continue; // always has all perms
				}
				ItemStack is = GUIGroupOverview.getHashedItem(rank.getName().hashCode());
				ItemAPI.setDisplayName(is, ChatColor.GOLD + rank.getName());
				if (GroupAPI.hasPermission(player, group, permMan.getModifyPerms())) {
					ItemAPI.addLore(is, ChatColor.AQUA + "Click to view and edit permissions");
				} else {
					ItemAPI.addLore(is, ChatColor.AQUA + "Click to view permissions");
				}
				content.add(new LClickable(is, p -> detailEdit(rank)));
			}
			rankSection = new Scrollbar(content, 45, 45, ContentAligners.getCenteredInOrder(content.size(), 45));
		}
		inventory.clear();
		inventory.addComponent(rankSection, i -> true);
		inventory.show();
	}

	private void detailEdit(GroupRank rank) {
		List<IClickable> content = new ArrayList<>();
		List<PermissionType> perms = new ArrayList<>(PermissionType.getAllPermissions());
		Collections.sort(perms, (a, b) -> {
			int pluginComp = a.getRegisteringPlugin().compareTo(b.getRegisteringPlugin());
			if (pluginComp != 0) {
				return pluginComp;
			}
			return a.getName().compareTo(b.getName());
		});
		boolean canEdit = GroupAPI.hasPermission(player, group, permMan.getModifyPerms());
		for (PermissionType perm : perms) {
			boolean hasPerm = GroupAPI.hasPermission(player, group, perm);
			Material mat = hasPerm ? Material.GREEN_DYE : Material.RED_DYE;
			ItemStack is = new ItemStack(mat);
			if (canEdit) {
				content.add(new LClickable(is, p -> {
					interactionManager.editPermission(p.getUniqueId(), group.getName(), !hasPerm, rank.getName(),
							perm.getName(), p::sendMessage);
					detailEdit(rank);
				}));
			} else {
				content.add(new DecorationStack(is));
			}
		}
		Scrollbar scroll = new Scrollbar(content, 45, 45, ContentAligners.getLeftAligned());
		inventory.clear();
		inventory.addComponent(scroll, i -> true);
		inventory.show();
	}

}
