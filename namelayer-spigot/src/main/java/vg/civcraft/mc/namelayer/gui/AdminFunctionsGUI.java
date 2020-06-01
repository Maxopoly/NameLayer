package vg.civcraft.mc.namelayer.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import vg.civcraft.mc.civmodcore.api.ItemAPI;
import vg.civcraft.mc.civmodcore.chatDialog.Dialog;
import vg.civcraft.mc.civmodcore.inventorygui.Clickable;
import vg.civcraft.mc.civmodcore.inventorygui.ClickableInventory;
import vg.civcraft.mc.civmodcore.inventorygui.DecorationStack;
import vg.civcraft.mc.civmodcore.inventorygui.LClickable;
import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.permission.PermissionType;

public class AdminFunctionsGUI extends NameLayerGroupGUI {

	private MainGroupGUI parent;

	public AdminFunctionsGUI(Player player, Group group, MainGroupGUI parent) {
		super(group, player);
		this.parent = parent;
	}

	public void showScreen() {
		ClickableInventory ci = new ClickableInventory(27, g.getName());
		// linking
		ItemStack linkStack = new ItemStack(Material.GOLD_INGOT);
		ItemAPI.setDisplayName(linkStack, ChatColor.GOLD + "Link group");
		Clickable linkClick;

		ItemAPI.addLore(linkStack, ChatColor.RED + "Sorry, group linking is not a currently supported feature.");
		linkClick = new DecorationStack(linkStack);

		ci.setSlot(linkClick, 10);
		// merging
		ItemStack mergeStack = new ItemStack(Material.SPONGE);
		ItemAPI.setDisplayName(mergeStack, ChatColor.GOLD + "Merge group");
		Clickable mergeClick;
		if (groupManager.hasAccess(g, p.getUniqueId(), PermissionType.getPermission("MERGE"))) {
			mergeClick = new Clickable(mergeStack) {
				@Override
				public void clicked(Player p) {
					showMergingMenu();
				}
			};
		} else {
			ItemAPI.addLore(mergeStack, ChatColor.RED + "You don't have permission to do this");
			mergeClick = new DecorationStack(mergeStack);
		}
		ci.setSlot(mergeClick, 12);
		// deleting group
		ItemStack deletionStack = new ItemStack(Material.BARRIER);
		ItemAPI.setDisplayName(deletionStack, ChatColor.GOLD + "Delete group");
		Clickable deletionClick;
		if (groupManager.hasAccess(g, p.getUniqueId(), PermissionType.getPermission("DELETE"))) {
			deletionClick = new Clickable(deletionStack) {
				@Override
				public void clicked(Player p) {
					showDeletionMenu();
				}
			};
		} else {
			ItemAPI.addLore(deletionStack, ChatColor.RED + "You don't have permission to do this");
			deletionClick = new DecorationStack(deletionStack);
		}
		ci.setSlot(deletionClick, 16);

		// back button
		ItemStack backToOverview = new ItemStack(Material.SPECTRAL_ARROW);
		ItemAPI.setDisplayName(backToOverview, ChatColor.GOLD + "Back to overview");
		ci.setSlot(new LClickable(backToOverview, p -> parent.showScreen()), 22);
		ci.showInventory(getPlayer());
	}

	private void showMergingMenu() {
		MergeGUI mGui = new MergeGUI(g, p, this);
		mGui.showScreen();
	}

	private void showDeletionMenu() {
		ClickableInventory confirmInv = new ClickableInventory(27, group.getName());
		ItemStack info = new ItemStack(Material.PAPER);
		ItemAPI.setDisplayName(info, ChatColor.GOLD + "Delete group " + group.getColoredName());
		ItemAPI.addLore(info, ChatColor.RED + "Are you sure that you want to",
				ChatColor.RED + "delete this group? You can not undo this!");
		confirmInv.setSlot(new LClickable(Material.GREEN_DYE,ChatColor.GOLD + "Yes, delete " + group.getColoredName(), p -> 			
				interactionManager.deleteGroup(getPlayer().getUniqueId(), group.getName(), player::sendMessage)
		), 11);
		confirmInv.setSlot(new LClickable(Material.RED_DYE, ChatColor.GOLD + "No, keep " + group.getColoredName(), p -> 
				showScreen()
		), 15);
		confirmInv.setSlot(new DecorationStack(info), 4);
		confirmInv.showInventory(player);
	}

}
