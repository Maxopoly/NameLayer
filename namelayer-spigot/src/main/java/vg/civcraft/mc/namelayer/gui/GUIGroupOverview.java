package vg.civcraft.mc.namelayer.gui;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import vg.civcraft.mc.civmodcore.api.ItemAPI;
import vg.civcraft.mc.civmodcore.api.MaterialAPI;
import vg.civcraft.mc.civmodcore.chatDialog.Dialog;
import vg.civcraft.mc.civmodcore.inventorygui.Clickable;
import vg.civcraft.mc.civmodcore.inventorygui.ClickableInventory;
import vg.civcraft.mc.civmodcore.inventorygui.DecorationStack;
import vg.civcraft.mc.civmodcore.inventorygui.IClickable;
import vg.civcraft.mc.civmodcore.inventorygui.components.ComponableInventory;
import vg.civcraft.mc.civmodcore.inventorygui.components.InventoryComponent;
import vg.civcraft.mc.civmodcore.inventorygui.components.Scrollbar;
import vg.civcraft.mc.civmodcore.inventorygui.components.SlotPredicates;
import vg.civcraft.mc.civmodcore.inventorygui.components.StaticDisplaySection;
import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.group.GroupManager;
import vg.civcraft.mc.namelayer.gui.folder.FolderElement;
import vg.civcraft.mc.namelayer.gui.folder.GroupEntry;
import vg.civcraft.mc.namelayer.gui.folder.GroupFolder;
import vg.civcraft.mc.namelayer.permission.GroupRank;
import vg.civcraft.mc.namelayer.permission.PermissionType;

public class GUIGroupOverview {

	private ComponableInventory inventory;
	private Scrollbar contentComponent;
	private Player player;
	private GroupManager groupManager;
	private GroupFolder currentFolder;
	private static final String topMostFolderName = "$parent";

	public GUIGroupOverview(Player player) {
		groupManager = NameLayerPlugin.getInstance().getGroupManager();
		this.player = player;
	}

	private void reconstructInventory() {
		if (inventory == null) {
			inventory = new ComponableInventory(ChatColor.GOLD + "Your groups", 6, player);
			this.currentFolder = constructFolderHierarchy();
			inventory.addComponent(getBottomBar(), SlotPredicates.offsetRectangle(1, 9, 5, 0));
		} else {
			inventory.removeComponent(contentComponent);
		}
		List<IClickable> clicks = new ArrayList<>();
		for (FolderElement folder : currentFolder.getContent()) {
			clicks.add(folder.getGUIEntry(this, this.player));
		}
		this.contentComponent = new Scrollbar(clicks, 45);
		inventory.addComponent(contentComponent, SlotPredicates.rows(5));
	}

	private InventoryComponent getBottomBar() {
		StaticDisplaySection bar = new StaticDisplaySection(9);
		bar.set(getCreateGroupClickable(), 0);
		// TODO create folder
		//TODO join group via password
		//TODO move item to folder
		//TODO close ui
		//TODO default group?
		//TODO autoaccept?
		return null;
	}

	public void showScreen() {
		inventory.show();
	}

	private GroupFolder constructFolderHierarchy() {
		// TODO load these
		Map<String, List<String>> folderToFolderMapping = new HashMap<>();
		Map<String, List<String>> folderToGroupMapping = new HashMap<>();
		return constructFolder(topMostFolderName, folderToFolderMapping, folderToGroupMapping, null);
	}

	private static GroupFolder constructFolder(String name, Map<String, List<String>> folderToFolderMapping,
			Map<String, List<String>> folderToGroupMapping, GroupFolder parent) {
		GroupFolder folder = new GroupFolder(parent);
		for (String folderName : folderToFolderMapping.get(topMostFolderName)) {
			folder.addElement(constructFolder(folderName, folderToFolderMapping, folderToGroupMapping, folder));
		}
		for (String groupName : folderToGroupMapping.get(topMostFolderName)) {
			folder.addElement(new GroupEntry(folder, groupName));
		}
		return folder;
	}

	private Clickable getCreateGroupClickable() {
		ItemStack is = new ItemStack(Material.APPLE);
		ItemAPI.setDisplayName(is, ChatColor.GOLD + "Create group");
		return new Clickable(is) {

			@Override
			public void clicked(final Player p) {
				p.sendMessage(ChatColor.YELLOW + "Enter the name of your new group or \"cancel\" to exit this prompt");
				ClickableInventory.forceCloseInventory(p);
				new Dialog(p, NameLayerPlugin.getInstance()) {

					@Override
					public List<String> onTabComplete(String wordCompleted, String[] fullMessage) {
						return Collections.emptyList();
					}

					@Override
					public void onReply(String[] message) {
						if (message.length > 1) {
							p.sendMessage(ChatColor.RED + "Group names may not contain spaces");
							showScreen();
							return;
						}
						String groupName = message[0];
						if (groupName.isEmpty()) {
							p.sendMessage(ChatColor.RED + "You didn't enter anything!");
							showScreen();
							return;
						}
						if ("cancel".equals(groupName)) {
							showScreen();
							return;
						}
						NameLayerPlugin.getInstance().getGroupInteractionManager().createGroup(p.getUniqueId(),
								groupName, g -> {
									//TODO add to current folder
									showScreen();
								}, p::sendMessage);
					}
				};

			}
		};
	}

	private Clickable getJoinGroupClickable() {
		ItemStack is = new ItemStack(Material.CHEST);
		ItemAPI.setDisplayName(is, ChatColor.GOLD + "Join password protected group");
		Clickable c = new Clickable(is) {

			@Override
			public void clicked(final Player p) {
				p.sendMessage(ChatColor.YELLOW + "Enter the name of the group or \"cancel\" to leave this prompt");
				ClickableInventory.forceCloseInventory(p);
				new Dialog(p, NameLayerPlugin.getInstance()) {

					@Override
					public List<String> onTabComplete(String wordCompleted, String[] fullMessage) {
						return Collections.emptyList();
					}

					@Override
					public void onReply(String[] message) {
						if (message.length > 1) {
							p.sendMessage(ChatColor.RED + "Group names can't contain spaces");
							showScreen();
							return;
						}
						String groupName = message[0];
						if (groupName.equals("cancel")) {
							showScreen();
							return;
						}
						final Group g = groupManager.getGroup(groupName);
						if (g == null) {
							p.sendMessage(ChatColor.RED + "This group doesn't exist");
							showScreen();
							return;
						}
						if (g.isMember(p.getUniqueId())) {
							p.sendMessage(ChatColor.RED + "You are already a member of this group");
							showScreen();
							return;
						}
						p.sendMessage(ChatColor.YELLOW + "Enter the group password");
						Dialog passDia = new Dialog(p, NameLayerPlugin.getInstance()) {

							@Override
							public List<String> onTabComplete(String wordCompleted, String[] fullMessage) {
								return Collections.emptyList();
							}

							@Override
							public void onReply(String[] message) {
								if (g.getPassword() == null || !g.getPassword().equals(message[0])) {
									p.sendMessage(ChatColor.RED + "Wrong password");
									showScreen();
								} else {
									Group gro = ensureFreshGroup(g);
									GroupPermission groupPerm = groupManager.getPermissionforGroup(gro);
									GroupRank pType = groupPerm
											.getFirstWithPerm(PermissionType.getPermission("JOIN_PASSWORD"));
									if (pType == null) {
										p.sendMessage(ChatColor.RED
												+ "Someone derped. This group does not have the specified permission to let you join, sorry.");
										showScreen();
										return;
									}
									if (NameLayerPlugin.getBlackList().isBlacklisted(gro, p.getUniqueId())) {
										p.sendMessage(ChatColor.RED
												+ "You can not join a group you have been blacklisted from");
										showScreen();
										return;
									}

									NameLayerPlugin.log(Level.INFO, p.getName() + " joined with password "
											+ " to group " + g.getName() + " via the gui");
									gro.addMember(p.getUniqueId(), pType);
									p.sendMessage(
											ChatColor.GREEN + "You have successfully been added to " + gro.getName());
									showScreen();
								}

							}
						};
					}
				};

			}
		};
		return c;
	}

	private static Map<Integer, ItemStack> idToItem;

	public static ItemStack getHashedItem(int seed) {
		if (idToItem == null) {
			idToItem = new HashMap<>();
			int counter = 0;
			for (Material mat : Tag.WOOL.getValues()) {
				idToItem.put(counter++, new ItemStack(mat));
			}
			for (Material mat : Tag.ITEMS_BANNERS.getValues()) {
				idToItem.put(counter++, new ItemStack(mat));
			}
			for (Material mat : Tag.BEDS.getValues()) {
				idToItem.put(counter++, new ItemStack(mat));
			}
			for (Material mat : Tag.CARPETS.getValues()) {
				idToItem.put(counter++, new ItemStack(mat));
			}
			for (Material mat : Tag.ITEMS_MUSIC_DISCS.getValues()) {
				idToItem.put(counter++, new ItemStack(mat));
			}
			// glass
			for (Material mat : Tag.IMPERMEABLE.getValues()) {
				idToItem.put(counter++, new ItemStack(mat));
			}
		}
		return idToItem.get(seed % idToItem.size()).clone();
	}
}
