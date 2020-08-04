package vg.civcraft.mc.namelayer.gui;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import vg.civcraft.mc.civmodcore.api.ItemAPI;
import vg.civcraft.mc.civmodcore.chatDialog.Dialog;
import vg.civcraft.mc.civmodcore.inventorygui.Clickable;
import vg.civcraft.mc.civmodcore.inventorygui.ClickableInventory;
import vg.civcraft.mc.civmodcore.inventorygui.DecorationStack;
import vg.civcraft.mc.civmodcore.inventorygui.IClickable;
import vg.civcraft.mc.civmodcore.inventorygui.LClickable;
import vg.civcraft.mc.namelayer.GroupAPI;
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
	

	private Clickable getPasswordClickable() {
		Clickable c;
		ItemStack is = new ItemStack(Material.OAK_SIGN);
		ItemAPI.setDisplayName(is, ChatColor.GOLD + "Add or change password");
		if (groupManager.hasAccess(group, player.getUniqueId(), PermissionType.getPermission("PASSWORD"))) {
			String pass = group.getPassword();
			if (pass == null) {
				ItemAPI.addLore(is, ChatColor.AQUA + "This group doesn't have a password currently");
			} else {
				ItemAPI.addLore(is, ChatColor.AQUA + "The current password is: " + ChatColor.YELLOW + pass);
			}
			c = new Clickable(is) {

				@Override
				public void clicked(final Player p) {
					if (groupManager.hasAccess(group, p.getUniqueId(), PermissionType.getPermission("PASSWORD"))) {
						p.sendMessage(ChatColor.GOLD + "Enter the new password for " + group.getName()
								+ ". Enter \" delete\" to remove an existing password or \"cancel\" to exit this prompt");
						ClickableInventory.forceCloseInventory(p);
						new Dialog(p, NameLayerPlugin.getInstance()) {

							@Override
							public List<String> onTabComplete(String wordCompleted, String[] fullMessage) {
								return Collections.emptyList();
							}

							@Override
							public void onReply(String[] message) {
								if (message.length == 0) {
									p.sendMessage(ChatColor.RED + "You entered nothing, no password was set");
									return;
								}
								if (message.length > 1) {
									p.sendMessage(ChatColor.RED + "Your password may not contain spaces");
									return;
								}
								String newPassword = message[0];
								if (newPassword.equals("cancel")) {
									p.sendMessage(ChatColor.GREEN + "Left password unchanged");
									return;
								}
								if (newPassword.equals("delete")) {
									group.setPassword(null);
									p.sendMessage(ChatColor.GREEN + "Removed the password from the group");
									NameLayerPlugin.log(Level.INFO, p.getName() + " removed password " + " for group "
											+ group.getName() + " via the gui");
								} else {
									NameLayerPlugin.log(Level.INFO, p.getName() + " set password to " + newPassword
											+ " for group " + group.getName() + " via the gui");
									group.setPassword(newPassword);
									p.sendMessage(
											ChatColor.GREEN + "Set new password: " + ChatColor.YELLOW + newPassword);
								}
								showScreen();
							}
						};
					} else {
						p.sendMessage(ChatColor.RED + "You lost permission to do this");
						showScreen();
					}
				}
			};
		} else {
			ItemAPI.addLore(is, ChatColor.RED + "You don't have permission to do this");
			c = new DecorationStack(is);
		}
		return c;
	}
	
	private IClickable getPermOptionClickable() {
		ItemStack permStack = new ItemStack(Material.OAK_FENCE_GATE);
		ItemAPI.setDisplayName(permStack, ChatColor.GOLD + "View and manage group permissions");
		IClickable permClickable;
		permClickable = new LClickable(permStack, p -> {
			if (GroupAPI.hasPermission(player, group, permMan.getListPerms())) {
				PermissionManageGUI pmgui = new PermissionManageGUI(group, player, MainGroupGUI.this);
				pmgui.showScreen();
			} else {
				showScreen();
			}
		});
		return permClickable;
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
