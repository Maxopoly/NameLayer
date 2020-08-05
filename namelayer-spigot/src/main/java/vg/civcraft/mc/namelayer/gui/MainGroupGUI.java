package vg.civcraft.mc.namelayer.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import vg.civcraft.mc.civmodcore.api.ItemAPI;
import vg.civcraft.mc.civmodcore.inventorygui.Clickable;
import vg.civcraft.mc.civmodcore.inventorygui.DecorationStack;
import vg.civcraft.mc.civmodcore.inventorygui.IClickable;
import vg.civcraft.mc.civmodcore.inventorygui.LClickable;
import vg.civcraft.mc.civmodcore.inventorygui.components.ComponableInventory;
import vg.civcraft.mc.civmodcore.inventorygui.components.ComponableSection;
import vg.civcraft.mc.civmodcore.inventorygui.components.Scrollbar;
import vg.civcraft.mc.civmodcore.inventorygui.components.SlotPredicates;
import vg.civcraft.mc.civmodcore.inventorygui.components.StaticDisplaySection;
import vg.civcraft.mc.civmodcore.inventorygui.components.impl.CommonGUIs;
import vg.civcraft.mc.namelayer.GroupAPI;
import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.group.GroupInteractionManager;
import vg.civcraft.mc.namelayer.misc.PlayerGroupSetting;
import vg.civcraft.mc.namelayer.permission.GroupRank;
import vg.civcraft.mc.namelayer.permission.GroupRankHandler;
import vg.civcraft.mc.namelayer.permission.NameLayerPermissionManager;

public class MainGroupGUI {

	private final Group group;
	private final Player player;
	private final GUIGroupOverview parent;

	private ComponableInventory inventory;
	private Scrollbar contentComponent;
	private StaticDisplaySection bottomBar;
	private boolean showInheritedMembers;
	private boolean showInvites;
	private boolean showMembers;
	private boolean showBlacklisted;
	private Set<GroupRank> ranksShown;
	private Set<GroupRank> ranksViewable;
	private NameLayerPermissionManager permMan;

	public MainGroupGUI(GUIGroupOverview parent, Player player, Group group) {
		this.group = group;
		this.player = player;
		this.parent = parent;
		this.permMan = NameLayerPlugin.getInstance().getNLPermissionManager();
		ranksShown = new HashSet<>();
		ranksViewable = new HashSet<>();
		showInheritedMembers = false;
		showInvites = true;
		showMembers = true;
		showBlacklisted = false;
	}
	
	ComponableInventory getInventory() {
		return inventory;
	}

	/**
	 * Shows the main gui overview for a specific group based on the properties of
	 * this class
	 */
	public void showScreen() {
		if (inventory == null) {
			inventory = new ComponableInventory(ChatColor.GOLD + group.getName(), 6, player);
			inventory.addComponent(getBottomBar(), SlotPredicates.offsetRectangle(1, 9, 5, 0));
		} else {
			inventory.removeComponent(contentComponent);
		}
		List<IClickable> clicks = constructContent();
		this.contentComponent = new Scrollbar(clicks, 45);
		inventory.addComponent(contentComponent, SlotPredicates.rows(5));
	}

	private StaticDisplaySection getBottomBar() {
		bottomBar = new StaticDisplaySection(9);
		bottomBar.set(getInvitePlayerClickable(), 0);
		bottomBar.set(getAddBlackListClickable(), 1);
		bottomBar.set(getVisibilityMenuClickable(), 2);
		bottomBar.set(getLeaveGroupClickable(), 3);
		bottomBar.set(getInfoStack(), 4);
		bottomBar.set(getDefaultGroupClickable(), 5);
		// edit ranks 6
		bottomBar.set(getAdminStuffClickable(), 7);
		bottomBar.set(getSuperMenuClickable(), 8);
		return bottomBar;
	}

	private IClickable getVisibilityMenuClickable() {
		return new LClickable(Material.LECTERN, ChatColor.GOLD + "Filter and adjust sorting", p -> {
			StaticDisplaySection toggles = new StaticDisplaySection(9);
			toggles.set(
					constructToggle(showInheritedMembers, 1, "Show inherited members", b -> showInheritedMembers = b),
					1);
			toggles.set(constructToggle(showInheritedMembers, 3, "Show invites", b -> showInheritedMembers = b), 3);
			toggles.set(constructToggle(showInheritedMembers, 5, "Show members", b -> showInheritedMembers = b), 5);
			toggles.set(
					constructToggle(showInheritedMembers, 7, "Show blacklisted players", b -> showInheritedMembers = b),
					7);
		});
	}

	private IClickable constructToggle(boolean state, int slot, String name, Consumer<Boolean> setter) {
		return new LClickable((state ? Material.GREEN_DYE : Material.RED_DYE), ChatColor.GOLD + name, p -> {
			setter.accept(!state);
			bottomBar.set(constructToggle(!state, slot, name, setter), slot);
		});
	}

	private List<IClickable> constructContent() {
		List<IClickable> result = new ArrayList<>();
		GroupRankHandler rankHandler = group.getGroupRankHandler();
		List<GroupRank> ranks = new ArrayList<>(ranksShown);
		boolean sortByRank = NameLayerPlugin.getInstance().getSettingsManager().getGUISortSetting().getValue(player);
		Map<GroupRank, List<UUID>> invertedInviteList = null;
		if (showInvites) {
			invertedInviteList = new HashMap<>();
			for (Entry<UUID, GroupRank> entry : group.getAllInvites().entrySet()) {
				List<UUID> existingInvites = invertedInviteList.computeIfAbsent(entry.getValue(),
						s -> new ArrayList<>());
				existingInvites.add(entry.getKey());
			}
		}
		if (sortByRank) {
			Collections.sort(ranks, (r1, r2) -> r1.getName().compareTo(r2.getName()));
		}
		for (GroupRank rank : ranks) {
			if (rank == rankHandler.getDefaultNonMemberRank()) {
				continue;
			}
			if (!showBlacklisted && rankHandler.isBlacklistedRank(rank)) {
				continue;
			}
			if (!showMembers && rankHandler.isMemberRank(rank)) {
				continue;
			}
			boolean canModify = GroupAPI.hasPermission(player, group, rank.getRemovalPermissionType());
			List<IClickable> tempList = new ArrayList<>();
			for (UUID uuid : group.getAllTrackedByType(rank)) {
				ItemStack is = getSkullFor(uuid);
				ItemAPI.addLore(is,
						String.format("%sRank: %s%s", ChatColor.DARK_AQUA, ChatColor.DARK_GRAY, rank.getName()));
				IClickable click;
				if (canModify) {
					ItemAPI.addLore(is, "", ChatColor.AQUA + "Click to modify");
					click = new LClickable(is, p -> handlePlayerClick(rank, uuid));
				} else {
					click = new DecorationStack(is);
				}
				tempList.add(click);
			}
			if (showInvites) {
				List<UUID> invitees = invertedInviteList.get(rank);
				if (invitees != null) {
					for (UUID uuid : invitees) {
						ItemStack is = getSkullFor(uuid);
						ItemAPI.addLore(is, ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "Pending invite", "",
								String.format("%sRank: %s%s", ChatColor.DARK_AQUA, ChatColor.DARK_GRAY,
										rank.getName()));
						ItemAPI.addGlow(is);
						IClickable click;
						if (canModify) {
							ItemAPI.addLore(is, "", ChatColor.AQUA + "Click to modify");
							click = new LClickable(is, p -> handleInviteClick(rank, uuid));
						} else {
							click = new DecorationStack(is);
						}
						tempList.add(click);
					}
				}
			}
			if (!sortByRank) {
				// sort by member name, which is included in the items name
				Collections.sort(tempList, (i1, i2) -> i1.getItemStack().getItemMeta().getDisplayName()
						.compareTo(i2.getItemStack().getItemMeta().getDisplayName()));
			}
			result.addAll(tempList);
		}
		return result;
	}

	private void handlePlayerClick(GroupRank rank, UUID player) {

	}

	private void handleInviteClick(GroupRank rank, UUID player) {

	}

	private static ItemStack getSkullFor(UUID uuid) {
		ItemStack is = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta skullMeta = (SkullMeta) is.getItemMeta();
		skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
		skullMeta.setDisplayName(ChatColor.GOLD + NameAPI.getCurrentName(uuid));
		is.setItemMeta(skullMeta);
		return is;
	}

	private Clickable getAddBlackListClickable() {
		ItemStack inviteStack = new ItemStack(Material.COOKIE);
		ItemAPI.setDisplayName(inviteStack, ChatColor.GOLD + "Add player to blacklist");
		return new LClickable(inviteStack, p -> new InvitationGUI(group, player, MainGroupGUI.this, false));
	}

	private IClickable getInvitePlayerClickable() {
		ItemStack inviteStack = new ItemStack(Material.COOKIE);
		ItemAPI.setDisplayName(inviteStack, ChatColor.GOLD + "Invite new member");
		return new LClickable(inviteStack, p -> {
			new InvitationGUI(group, player, MainGroupGUI.this, false);
		});
	}

	private IClickable getDefaultGroupClickable() {
		PlayerGroupSetting defGroupSetting = NameLayerPlugin.getInstance().getSettingsManager().getDefaultGroup();
		ItemStack is = new ItemStack(Material.BRICKS);
		ItemAPI.setDisplayName(is, ChatColor.GOLD + "Default group");
		Group defGroup = defGroupSetting.getGroup(player);
		if (defGroup != null && defGroup.equals(group)) {
			ItemAPI.addLore(is, ChatColor.AQUA + "This group is your current default group");
			return new DecorationStack(is);
		} else {
			ItemAPI.addLore(is, String.format("%sClick to make %s%s your default group", ChatColor.AQUA,
					group.getColoredName(), ChatColor.AQUA));
			if (defGroup != null) {
				ItemAPI.addLore(is,
						String.format("%sYour current default group: %s", ChatColor.BLUE, defGroup.getColoredName()));
			}
			return new LClickable(is, p -> {
				NameLayerPlugin.getInstance().getLogger().log(Level.INFO,
						p.getName() + " set default group to " + group.getName() + " via the gui");
				if (defGroup == null) {
					p.sendMessage(String.format("%sYou have set your default group to %s", ChatColor.GREEN,
							group.getColoredName()));
				} else {
					p.sendMessage(String.format("%sYou have changed your default group from %s%s to %s",
							ChatColor.GREEN, defGroup.getColoredName(), ChatColor.GREEN, group.getColoredName()));
				}
				defGroupSetting.setGroup(player, group);
				showScreen();
			});
		}
	}

	private IClickable getSuperMenuClickable() {
		return new LClickable(Material.DIAMOND, ChatColor.GOLD + "Return to overview for all your groups", p -> {
			showParent();
		});
	}

	public void showParent() {
		if (parent != null) {
			parent.showScreen();
			return;
		}
		GUIGroupOverview gui = new GUIGroupOverview(player, inventory);
		gui.showScreen();
	}

	private Clickable getAdminStuffClickable() {
		ItemStack is = new ItemStack(Material.DIAMOND);
		ItemAPI.setDisplayName(is, ChatColor.GOLD + "Owner functions");
		return new Clickable(is) {

			@Override
			public void clicked(Player p) {
				AdminFunctionsGUI subGui = new AdminFunctionsGUI(p, group, MainGroupGUI.this);
				subGui.showScreen();
			}
		};
	}

	/**
	 * Constructs the icon used in the gui for leaving a group
	 */
	private Clickable getLeaveGroupClickable() {
		GroupInteractionManager interMan = NameLayerPlugin.getInstance().getGroupInteractionManager();
		return new LClickable(Material.IRON_DOOR, ChatColor.GOLD + "Leave group", p -> {
			ComponableSection yesNoSec = CommonGUIs.genConfirmationGUI(6, 9, () -> {
				interMan.leaveGroup(p.getUniqueId(), group.getName(), p::sendMessage);
				showParent();
			}, String.format("%sYes, leave %s", ChatColor.GREEN, group.getColoredName()), () -> {
				showScreen();
			}, String.format("%sNo, do not leave %s", ChatColor.RED, group.getColoredName()));

		});
	}

	private IClickable getInfoStack() {
		ItemStack is = new ItemStack(Material.PAPER);
		ItemAPI.setDisplayName(is, ChatColor.GOLD + "Stats for " + group.getColoredName());
		ItemAPI.addLore(is, ChatColor.DARK_AQUA + "Your current rank: " + ChatColor.YELLOW
				+ group.getRank(player.getUniqueId()).getName());
		GroupRankHandler rankHandler = group.getGroupRankHandler();
		for (GroupRank rank : group.getGroupRankHandler().getAllRanks()) {
			if (rank == rankHandler.getDefaultNonMemberRank()) {
				continue;
			}
			if (!GroupAPI.hasPermission(player, group, rank.getListPermissionType())) {
				continue;
			}
			int count = group.getAllTrackedByType(rank).size();
			if (count == 0) {
				continue;
			}
			ItemAPI.addLore(is, String.format("%s%s %s", ChatColor.AQUA, count, rank.getName()));
		}

		if (GroupAPI.hasPermission(player, group, permMan.getGroupStats())) {
			ItemAPI.addLore(is,
					String.format("%s%s members in total", ChatColor.DARK_AQUA, group.getAllMembers().size()));
		}
		return new DecorationStack(is);
	}
}
