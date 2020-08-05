package vg.civcraft.mc.namelayer.gui;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.api.ItemAPI;
import vg.civcraft.mc.civmodcore.chatDialog.Dialog;
import vg.civcraft.mc.civmodcore.inventorygui.ClickableInventory;
import vg.civcraft.mc.civmodcore.inventorygui.DecorationStack;
import vg.civcraft.mc.civmodcore.inventorygui.IClickable;
import vg.civcraft.mc.civmodcore.inventorygui.LClickable;
import vg.civcraft.mc.civmodcore.inventorygui.components.ComponableInventory;
import vg.civcraft.mc.civmodcore.inventorygui.components.ComponableSection;
import vg.civcraft.mc.civmodcore.inventorygui.components.StaticDisplaySection;
import vg.civcraft.mc.civmodcore.inventorygui.components.impl.CommonGUIs;
import vg.civcraft.mc.namelayer.GroupAPI;
import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.permission.NameLayerPermissionManager;
import vg.civcraft.mc.namelayer.permission.PermissionType;

public class AdminFunctionsGUI extends NameLayerGroupGUI {

	private MainGroupGUI parent;
	private ComponableInventory inventory;
	private NameLayerPermissionManager permMan;

	public AdminFunctionsGUI(Player player, Group group, MainGroupGUI parent) {
		super(group, player);
		this.parent = parent;
		this.inventory = parent.getInventory();
		this.permMan = NameLayerPlugin.getInstance().getNLPermissionManager();
	}

	private void reconstruct() {
		inventory.clear();
		StaticDisplaySection display = new StaticDisplaySection(54);
		inventory.addComponent(display, i -> true);
		display.set(getRenamingClickable(), 20);
		display.set(getChangeGroupColorClickable(), 22);
		display.set(getChangePasswordClickable(), 24);

		display.set(getPermsClickable(), 28);
		display.set(getLinkingClickable(), 30);
		display.set(getMergingClickable(), 32);
		display.set(getDeletingClickable(), 34);
	}
	
	public void showScreen() {
		inventory.show();
	}
	
	public ComponableInventory getInventory() {
		return inventory;
	}

	private IClickable getRenamingClickable() {
		return null; // TODO
	}

	private IClickable getChangeGroupColorClickable() {
		return null; // TODO
	}

	private IClickable getChangePasswordClickable() {
		return permissionWrap(new LClickable(Material.GOLD_INGOT,
				String.format("%sSet the password for %s", ChatColor.GOLD, group.getColoredName()), p -> {
					ClickableInventory.forceCloseInventory(p);
					new Dialog(p, NameLayerPlugin.getInstance()) {

						@Override
						public List<String> onTabComplete(String wordCompleted, String[] fullMessage) {
							return Collections.emptyList();
						}

						@Override
						public void onReply(String[] message) {
							interactionManager.setPassword(p.getUniqueId(), group.getName(), String.join(" ", message),
									p::sendMessage);
							showScreen();
						}
					};

				}), permMan.getPassword());
	}

	private IClickable getPermsClickable() {
		return permissionWrap(new LClickable(Material.OAK_FENCE_GATE, String.format(
				"%sView and edit permissions for %s%s", ChatColor.GOLD, group.getColoredName(), ChatColor.GOLD), p -> {
					PermissionManageGUI permGui = new PermissionManageGUI(group, player, this);
					permGui.reconstruct();
				}), permMan.getListPerms());
	}

	private IClickable getLinkingClickable() {
		return permissionWrap(new LClickable(Material.GOLD_INGOT,
				String.format("%sView existing group links and link %s%s to another group", ChatColor.GOLD,
						group.getColoredName(), ChatColor.GOLD),
				p -> {
					//TODO 
				}), permMan.getLinkGroup());
	}

	private IClickable getMergingClickable() {
		return permissionWrap(new LClickable(Material.SPONGE, String.format("%sMerge %s%s into another group",
				ChatColor.GOLD, group.getColoredName(), ChatColor.GOLD), p -> {
					MergeGUI mergeGui = new MergeGUI(group, player, this);
					mergeGui.showScreen();
				}), permMan.getMergeGroup());
	}

	private IClickable permissionWrap(IClickable click, PermissionType perm) {
		if (!GroupAPI.hasPermission(player, group, perm)) {
			ItemAPI.addLore(click.getItemStack(), ChatColor.RED + "You do not have permission to do this");
			return new DecorationStack(click.getItemStack());
		}
		return click;
	}

	private IClickable getDeletingClickable() {
		return permissionWrap(new LClickable(Material.SPONGE,
				String.format("%sDelete %s%s permanently", ChatColor.GOLD, group.getColoredName(), ChatColor.GOLD),
				p -> {
					ComponableSection confirm = CommonGUIs.genConfirmationGUI(6, 9, () -> {
						if (interactionManager.deleteGroup(player.getUniqueId(), group.getName(), p::sendMessage)) {
							parent.showParent();
						}
						else {
							parent.showScreen();
						}
					}, String.format("%s%sYes, delete %s%s%s permanently", ChatColor.RED, ChatColor.BOLD,
							group.getColoredName(), ChatColor.RED, ChatColor.BOLD), () -> {
								reconstruct();
								showScreen();
							}, ChatColor.RED + "No, go back");
					inventory.clear();
					inventory.addComponent(confirm, i -> true);
				}), permMan.getDeleteGroup());

	}

}
