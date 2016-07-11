package vg.civcraft.mc.namelayer.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import vg.civcraft.mc.civmodcore.inventorygui.Clickable;
import vg.civcraft.mc.civmodcore.inventorygui.ClickableInventory;
import vg.civcraft.mc.civmodcore.inventorygui.DecorationStack;
import vg.civcraft.mc.civmodcore.itemHandling.ISUtils;
import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.permission.PermissionType;
import vg.civcraft.mc.namelayer.permission.PlayerType;

public class PermissionManageGUI extends AbstractGroupGUI {

	private MainGroupGUI parent;
	private int currentPage;

	public PermissionManageGUI(Group g, Player p, MainGroupGUI parentGUI) {
		super(g, p);
		this.parent = parentGUI;
		currentPage = 0;
	}

	public void showScreen() {
		ClickableInventory ci = new ClickableInventory(36, g.getName());
		if (!validGroup()) {
			return;
		}
		//dye blacklisted clickable black
		List <PlayerType> types = g.getPlayerTypeHandler().getAllTypes();
		for(int i = 0; i< types.size() ; i++) {
			ci.setSlot(produceSelectionClickable(types.get(i)), i);
		}
		
		
		ItemStack backStack = new ItemStack(Material.ARROW);
		ISUtils.setName(backStack, ChatColor.GOLD
				+ "Go back to member management");
		ci.setSlot(new Clickable(backStack) {

			@Override
			public void clicked(Player arg0) {
				parent.showScreen();
			}
		}, 27);
		ci.showInventory(p);
	}

	private Clickable produceSelectionClickable(final PlayerType pType) {
		ItemStack is = MenuUtils.getPlayerTypeStack(pType.getId());
		ItemMeta im = is.getItemMeta();
		im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		is.setItemMeta(im);
		Clickable c;
		ISUtils.setName(is, ChatColor.GOLD + "View and edit permissions for "
				+ pType.getName());
		if (!gm.hasAccess(g, p.getUniqueId(),
				PermissionType.getPermission("LIST_PERMS"))) {
			ISUtils.addLore(is, ChatColor.RED + "You are not allowed to list",
					ChatColor.RED + "permissions for this group");
			c = new DecorationStack(is);
		} else {
			c = new Clickable(is) {

				@Override
				public void clicked(Player arg0) {
					showPermissionEditing(pType);
				}
			};
		}
		return c;
	}

	private void showPermissionEditing(final PlayerType pType) {
		if (!validGroup()) {
			return;
		}
		if (!gm.hasAccess(g, p.getUniqueId(),
				PermissionType.getPermission("LIST_PERMS"))) {
			p.sendMessage(ChatColor.RED
					+ "You are not allowed to list permissions for this group");
			showScreen();
			return;
		}
		ClickableInventory ci = new ClickableInventory(54, g.getName());
		final List<Clickable> clicks = new ArrayList<Clickable>();
		boolean canEdit = gm.hasAccess(g, p.getUniqueId(),
				PermissionType.getPermission("PERMS"));
		for (final PermissionType perm : PermissionType.getAllPermissions()) {
			ItemStack is;
			Clickable c;
			final boolean hasPerm = pType.hasPermission(perm);
			if (hasPerm) {
				is = new ItemStack(Material.INK_SACK, 1, (short) 10); // green
																		// dye
				ISUtils.addLore(
						is,
						ChatColor.DARK_AQUA
								+ pType.getName()
								+ " currently have", ChatColor.DARK_AQUA
								+ "this permission");
			} else {
				is = new ItemStack(Material.INK_SACK, 1, (short) 1); // red dye
				ISUtils.addLore(
						is,
						ChatColor.DARK_AQUA
								+ pType.getName()
								+ " currently don't have", ChatColor.DARK_AQUA
								+ "this permission");
			}
			ISUtils.setName(is, perm.getName());
			String desc = perm.getDescription();
			if (desc != null) {
				ISUtils.addLore(is, ChatColor.GREEN + desc);
			}
			if (canEdit) {
				ISUtils.addLore(is, ChatColor.AQUA + "Click to toggle");
				c = new Clickable(is) {

					@Override
					public void clicked(Player arg0) {
						if (hasPerm == pType.hasPermission(perm)) { // recheck
							if (gm.hasAccess(g, p.getUniqueId(),
									PermissionType.getPermission("PERMS"))) {
								NameLayerPlugin.log(Level.INFO, p.getName()
										+ (hasPerm ? " removed " : " added ")
										+ "the permission " + perm.getName()
										+ "for player type" + pType.getName()
										+ " for " + g.getName() + "via gui");
								if (hasPerm) {
									pType.removePermission(perm, true);
								} else {
									pType.addPermission(perm, true);
								}
								checkRecacheGroup();
							}
						} else {
							p.sendMessage(ChatColor.RED
									+ "Something changed while you were modifying permissions, so cancelled the process");
						}
						showPermissionEditing(pType);
					}
				};
			} else {
				c = new DecorationStack(is);
			}
			clicks.add(c);
		}

		if (clicks.size() < 45 * currentPage) {
			currentPage--;
			showScreen();
		}
		for (int i = 45 * currentPage; i < 45 * (currentPage + 1)
				&& i < clicks.size(); i++) {
			ci.setSlot(clicks.get(i), i - (45 * currentPage));
		}

		if (currentPage > 0) {
			ItemStack back = new ItemStack(Material.ARROW);
			ISUtils.setName(back, ChatColor.GOLD + "Go to previous page");
			Clickable baCl = new Clickable(back) {

				@Override
				public void clicked(Player arg0) {
					if (currentPage > 0) {
						currentPage--;
					}
					showScreen();
				}
			};
			ci.setSlot(baCl, 45);
		}
		// next button
		if ((45 * (currentPage + 1)) <= clicks.size()) {
			ItemStack forward = new ItemStack(Material.ARROW);
			ISUtils.setName(forward, ChatColor.GOLD + "Go to next page");
			Clickable forCl = new Clickable(forward) {

				@Override
				public void clicked(Player arg0) {
					if ((45 * (currentPage + 1)) <= clicks.size()) {
						currentPage++;
					}
					showScreen();
				}
			};
			ci.setSlot(forCl, 53);
		}

		ItemStack backToOverview = new ItemStack(Material.WOOD_DOOR);
		ISUtils.setName(backToOverview, ChatColor.GOLD + "Go back");
		ci.setSlot(new Clickable(backToOverview) {

			@Override
			public void clicked(Player arg0) {
				showScreen();
			}
		}, 49);
		ci.showInventory(p);
	}

}
