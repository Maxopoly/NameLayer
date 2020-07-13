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
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.StringUtil;

import vg.civcraft.mc.civmodcore.api.ItemAPI;
import vg.civcraft.mc.civmodcore.chatDialog.Dialog;
import vg.civcraft.mc.civmodcore.inventorygui.Clickable;
import vg.civcraft.mc.civmodcore.inventorygui.ClickableInventory;
import vg.civcraft.mc.civmodcore.inventorygui.DecorationStack;
import vg.civcraft.mc.civmodcore.inventorygui.IClickable;
import vg.civcraft.mc.civmodcore.inventorygui.LClickable;
import vg.civcraft.mc.civmodcore.inventorygui.components.ComponableInventory;
import vg.civcraft.mc.civmodcore.inventorygui.components.Scrollbar;
import vg.civcraft.mc.civmodcore.inventorygui.components.SlotPredicates;
import vg.civcraft.mc.civmodcore.inventorygui.components.StaticDisplaySection;
import vg.civcraft.mc.namelayer.GroupAPI;
import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.events.PromotePlayerEvent;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.listeners.PlayerListener;
import vg.civcraft.mc.namelayer.permission.GroupRank;
import vg.civcraft.mc.namelayer.permission.GroupRankHandler;
import vg.civcraft.mc.namelayer.permission.NameLayerPermissionManager;
import vg.civcraft.mc.namelayer.permission.PermissionType;

public class MainGroupGUI {

	private final Group group;
	private final Player player;

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

	public MainGroupGUI(Player player, Group group) {
		this.group = group;
		this.player = player;
		this.permMan = NameLayerPlugin.getInstance().getNLPermissionManager();
		ranksShown = new HashSet<>();
		ranksViewable = new HashSet<>();
		showInheritedMembers = false;
		showInvites = true;
		showMembers = true;
		showBlacklisted = false;
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
		ci.setSlot(getLeaveGroupClickable(), 3);
		ci.setSlot(getInfoStack(), 4);
		ci.setSlot(getDefaultGroupStack(), 5);
		// edit ranks 6
		ci.setSlot(getAdminStuffClickable(), 7);
		// return to super 8
		return bottomBar;
	}

	private IClickable getVisibilityMenuClickable() {
		return new LClickable(Material.LECTERN, ChatColor.GOLD + "Filter and adjust sorting", p -> {
			StaticDisplaySection toggles = new StaticDisplaySection(9);
			toggles.set(
					constructToggle(showInheritedMembers, 1, "Show inherited members", b -> showInheritedMembers = b),
					1);
			toggles.set(
					constructToggle(showInheritedMembers, 3, "Show invites", b -> showInheritedMembers = b),
					3);
			toggles.set(
					constructToggle(showInheritedMembers, 5, "Show members", b -> showInheritedMembers = b),
					5);
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

	/**
	 * Called when the icon representing a member in the middle of the gui is
	 * clicked, this opens up a detailed view where you can select what to do
	 * (promoting/removing)
	 *
	 * @param uuid the UUID to show the inventory to
	 */
	public void showDetail(final UUID uuid) {
		ClickableInventory ci = new ClickableInventory(27, group.getName());
		String playerName = NameAPI.getCurrentName(uuid);

		ItemStack info = new ItemStack(Material.PAPER);
		ItemAPI.setDisplayName(info, ChatColor.GOLD + playerName);
		String rankName = getRankName(uuid);
		ItemAPI.addLore(info, ChatColor.GOLD + "Current rank: " + rankName);
		ci.setSlot(new DecorationStack(info), 4);

		Clickable memberClick = setupDetailSlot(Material.LEATHER_CHESTPLATE, uuid, GroupRank.MEMBERS);
		ci.setSlot(memberClick, 10);
		Clickable modClick = setupDetailSlot(modMat(), uuid, GroupRank.MODS);
		ci.setSlot(modClick, 12);
		Clickable adminClick = setupDetailSlot(Material.IRON_CHESTPLATE, uuid, GroupRank.ADMINS);
		ci.setSlot(adminClick, 14);
		Clickable ownerClick = setupDetailSlot(Material.DIAMOND_CHESTPLATE, uuid, GroupRank.OWNER);
		ci.setSlot(ownerClick, 16);

		ItemStack backToOverview = goBackStack();
		ItemAPI.setDisplayName(backToOverview, ChatColor.GOLD + "Back to overview");
		ci.setSlot(new Clickable(backToOverview) {

			@Override
			public void clicked(Player arg0) {
				showScreen();
			}
		}, 22);
		ci.showInventory(player);
	}

	/**
	 * Used by the gui that allows selecting an action for a specific member to
	 * easily construct the clickables needed
	 */
	private Clickable setupDetailSlot(Material slotMaterial, final UUID toChange, final GroupRank pType) {
		final GroupRank rank = group.getCurrentRank(toChange);
		ItemStack mod = new ItemStack(slotMaterial);
		ItemMeta im = mod.getItemMeta();
		im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		mod.setItemMeta(im);
		Clickable modClick;
		if (rank == pType) {
			ItemAPI.setDisplayName(mod, ChatColor.GOLD + "Remove this player");
			if (!groupManager.hasAccess(group, player.getUniqueId(), getAccordingPermission(pType))) {
				ItemAPI.addLore(mod, ChatColor.RED + "You dont have permission to do this");
				modClick = new DecorationStack(mod);
			} else {
				modClick = new Clickable(mod) {

					@Override
					public void clicked(Player arg0) {
						if (groupManager.hasAccess(group, player.getUniqueId(),
								getAccordingPermission(group.getCurrentRank(toChange)))) {
							removeMember(toChange);
							showScreen();
						}
					}
				};
			}
		} else {
			ItemAPI.setDisplayName(mod, ChatColor.GOLD + demoteOrPromote(group.getRank(toChange), pType, true)
					+ " this player to " + GroupRank.getNiceRankName(pType));
			if (!groupManager.hasAccess(group, player.getUniqueId(), getAccordingPermission(pType))) {
				ItemAPI.addLore(mod, ChatColor.RED + "You dont have permission to do this");
				modClick = new DecorationStack(mod);
			} else {
				modClick = new Clickable(mod) {

					@Override
					public void clicked(Player arg0) {
						changePlayerRank(toChange, pType);
						showDetail(toChange);
					}

				};
			}
		}
		return modClick;
	}

	private void removeMember(UUID toRemove) {
		if (groupManager.hasAccess(group, player.getUniqueId(),
				getAccordingPermission(group.getCurrentRank(toRemove)))) {
			if (!group.isMember(toRemove)) {
				player.sendMessage(ChatColor.RED + "This player is no longer on the group and can't be removed");
				return;
			}
			if (group.isOwner(toRemove)) {
				player.sendMessage(ChatColor.RED + "This player owns the group and can't be removed");
			}
			NameLayerPlugin.log(Level.INFO, player.getName() + " kicked " + NameAPI.getCurrentName(toRemove) + " from "
					+ group.getName() + " via the gui");
			group.removeMember(toRemove);
			player.sendMessage(ChatColor.GREEN + NameAPI.getCurrentName(toRemove) + " has been removed from the group");
		} else {
			player.sendMessage(ChatColor.RED + "You have lost permission to remove this player");
		}
	}

	private void changePlayerRank(UUID toChange, GroupRank newRank) {
		if (groupManager.hasAccess(group, player.getUniqueId(),
				getAccordingPermission(group.getCurrentRank(toChange)))) {
			if (!group.isMember(toChange)) {
				player.sendMessage(ChatColor.RED + "This player is no longer on the group and can't be "
						+ demoteOrPromote(group.getCurrentRank(toChange), newRank, false) + "d");
				return;
			}
			if (group.isOwner(toChange)) {
				player.sendMessage(ChatColor.RED + "This player owns the group and can't be demoted");
			}
			OfflinePlayer prom = Bukkit.getOfflinePlayer(toChange);
			NameLayerPlugin.log(Level.INFO,
					player.getName() + " changed player rank for " + NameAPI.getCurrentName(toChange) + " from "
							+ group.getCurrentRank(toChange).toString() + " to " + newRank.toString() + " for group "
							+ group.getName() + " via the gui");
			if (prom.isOnline()) {
				Player oProm = (Player) prom;
				PromotePlayerEvent event = new PromotePlayerEvent(oProm, group, group.getCurrentRank(toChange),
						newRank);
				Bukkit.getPluginManager().callEvent(event);
				if (event.isCancelled()) {
					player.sendMessage(ChatColor.RED + "Could not change player rank, you should complain about this");
					return;
				}
				group.removeMember(toChange);
				group.addMember(toChange, newRank);
				oProm.sendMessage(ChatColor.GREEN + "You have been promoted to " + getRankName(toChange)
						+ " in (Group) " + group.getName());
			} else {
				// player is offline change their perms
				group.removeMember(toChange);
				group.addMember(toChange, newRank);
			}
			player.sendMessage(ChatColor.GREEN + NameAPI.getCurrentName(toChange) + " has been "
					+ demoteOrPromote(group.getCurrentRank(toChange), newRank, false) + "d to "
					+ getRankName(toChange));
		} else {
			player.sendMessage(ChatColor.RED + "You have lost permission to remove this player");
		}
	}

	private Clickable createBlacklistToggle() {
		ItemStack is = MenuUtils.toggleButton(showBlacklist, ChatColor.GOLD + "Show blacklisted players",
				groupManager.hasAccess(group, player.getUniqueId(), PermissionType.getPermission("GROUPSTATS")));
		Clickable c;
		if (groupManager.hasAccess(group, player.getUniqueId(), PermissionType.getPermission("GROUPSTATS"))) {
			c = new Clickable(is) {

				@Override
				public void clicked(Player arg0) {
					if (!showBlacklist) {
						// currently showing members, so save state
						savedToggleState = new boolean[5];
						savedToggleState[0] = showInvites;
						savedToggleState[1] = showMembers;
						savedToggleState[2] = showMods;
						savedToggleState[3] = showAdmins;
						savedToggleState[4] = showOwners;
						showInvites = false;
						showMembers = false;
						showMods = false;
						showAdmins = false;
						showOwners = false;
						showBlacklist = true;
					} else {
						// load state
						showInvites = savedToggleState[0];
						showMembers = savedToggleState[1];
						showMods = savedToggleState[2];
						showAdmins = savedToggleState[3];
						showOwners = savedToggleState[4];
						showBlacklist = false;
					}
					showScreen();
				}
			};
		} else {
			c = new DecorationStack(is);
		}
		return c;
	}

	private Clickable createInheritedMemberToggle() {
		boolean canToggle = groupManager.hasAccess(group, player.getUniqueId(),
				PermissionType.getPermission("GROUPSTATS"));
		ItemStack is = MenuUtils.toggleButton(showInheritedMembers, ChatColor.GOLD + "Show inherited members",
				canToggle);
		Clickable c;
		if (canToggle) {
			c = new Clickable(is) {

				@Override
				public void clicked(Player p) {
					showInheritedMembers = !showInheritedMembers;
					showScreen();

				}
			};
		} else {
			c = new DecorationStack(is);
		}
		return c;
	}

	private Clickable createInviteToggle() {
		ItemStack is = MenuUtils.toggleButton(showInvites, ChatColor.GOLD + "Show invited players", true);
		return new Clickable(is) {

			@Override
			public void clicked(Player arg0) {
				showInvites = !showInvites;
				showScreen();
			}
		};
	}

	private Clickable getAddBlackListClickable() {
		Clickable c;
		ItemStack is = blacklistStack();
		ItemAPI.setDisplayName(is, ChatColor.GOLD + "Add player to blacklist");
		if (groupManager.hasAccess(group, player.getUniqueId(), PermissionType.getPermission("BLACKLIST"))) {
			c = new Clickable(is) {

				@Override
				public void clicked(final Player p) {
					p.sendMessage(ChatColor.GOLD
							+ "Enter the name of the player to blacklist or \"cancel\" to exit this prompt");
					ClickableInventory.forceCloseInventory(p);
					new Dialog(p, NameLayerPlugin.getInstance()) {

						@Override
						public List<String> onTabComplete(String word, String[] msg) {
							List<String> players = Bukkit.getOnlinePlayers().stream()
									.filter(p -> !group.isMember(p.getUniqueId())).map(Player::getName)
									.collect(Collectors.toList());
							players.add("cancel");

							return StringUtil.copyPartialMatches(word, players, new ArrayList<>());
						}

						@Override
						public void onReply(String[] message) {
							if (message[0].equalsIgnoreCase("cancel")) {
								showScreen();
								return;
							}
							if (groupManager.hasAccess(group, p.getUniqueId(),
									PermissionType.getPermission("BLACKLIST"))) {
								for (String playerName : message) {
									UUID blackUUID = NameAPI.getUUID(playerName);
									if (blackUUID == null) {
										p.sendMessage(ChatColor.RED + playerName + " doesn't exist");
										continue;
									}
									if (group.isMember(blackUUID)) {
										p.sendMessage(ChatColor.RED + NameAPI.getCurrentName(blackUUID)
												+ " is currently a member of this group and can't be blacklisted");
										continue;
									}
									BlackList bl = NameLayerPlugin.getBlackList();
									if (bl.isBlacklisted(group, blackUUID)) {
										p.sendMessage(ChatColor.RED + NameAPI.getCurrentName(blackUUID)
												+ " is already blacklisted");
										continue;
									}
									NameLayerPlugin.log(Level.INFO,
											p.getName() + " blacklisted " + NameAPI.getCurrentName(blackUUID)
													+ " for group " + group.getName() + " via the gui");
									bl.addBlacklistMember(group, blackUUID, true);
									p.sendMessage(ChatColor.GREEN + NameAPI.getCurrentName(blackUUID)
											+ " was successfully blacklisted");
								}
							} else {
								p.sendMessage(ChatColor.RED + "You lost permission to do this");
							}
							showScreen();
						}
					};

				}
			};
		} else {
			ItemAPI.addLore(is, ChatColor.RED + "You don't have permission to do this");
			c = new DecorationStack(is);
		}
		return c;
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

	private IClickable getInvitePlayerClickable() {
		ItemStack inviteStack = new ItemStack(Material.COOKIE);
		ItemAPI.setDisplayName(inviteStack, ChatColor.GOLD + "Invite new member");
		return new LClickable(inviteStack, p -> {
			new InvitationGUI(group, player, MainGroupGUI.this);
		});
	}

	private Clickable getDefaultGroupStack() {
		Clickable c;
		ItemStack is = defaultStack();
		ItemAPI.setDisplayName(is, ChatColor.GOLD + "Default group");
		final String defGroup = groupManager.getDefaultGroup(player.getUniqueId());
		if (defGroup != null && defGroup.equals(group.getName())) {
			ItemAPI.addLore(is, ChatColor.AQUA + "This group is your current default group");
			c = new DecorationStack(is);
		} else {
			ItemAPI.addLore(is, ChatColor.AQUA + "Click to make this group your default group");
			if (defGroup != null) {
				ItemAPI.addLore(is, ChatColor.BLUE + "Your current default group is : " + defGroup);
			}
			c = new Clickable(is) {

				@Override
				public void clicked(Player p) {
					NameLayerPlugin.log(Level.INFO,
							p.getName() + " set default group to " + group.getName() + " via the gui");
					if (defGroup == null) {
						group.setDefaultGroup(p.getUniqueId());
						p.sendMessage(ChatColor.GREEN + "You have set your default group to " + group.getName());
					} else {
						group.changeDefaultGroup(p.getUniqueId());
						p.sendMessage(ChatColor.GREEN + "You changed your default group from " + defGroup + " to "
								+ group.getName());
					}
					showScreen();
				}
			};
		}
		return c;
	}

	private Clickable getSuperMenuClickable() {
		ItemStack is = new ItemStack(Material.DIAMOND);
		ItemAPI.setDisplayName(is, ChatColor.GOLD + "Return to overview for all your groups");
		return new Clickable(is) {

			@Override
			public void clicked(Player p) {
				GUIGroupOverview gui = new GUIGroupOverview(p);
				gui.showScreen();
			}
		};
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
		Clickable c;
		ItemStack is = new ItemStack(Material.IRON_DOOR);
		ItemAPI.setDisplayName(is, ChatColor.GOLD + "Leave group");
		if (group.isOwner(player.getUniqueId())) {
			ItemAPI.addLore(is, ChatColor.RED + "You cant leave this group,", ChatColor.RED + "because you own it");
			c = new DecorationStack(is);
		} else {
			c = new Clickable(is) {

				@Override
				public void clicked(Player p) {
					ClickableInventory confirmInv = new ClickableInventory(27, group.getName());
					ItemStack info = new ItemStack(Material.PAPER);
					ItemAPI.setDisplayName(info, ChatColor.GOLD + "Leave group");
					ItemAPI.addLore(info, ChatColor.RED + "Are you sure that you want to",
							ChatColor.RED + "leave this group? You can not undo this!");
					ItemStack yes = yesStack();
					ItemAPI.setDisplayName(yes, ChatColor.GOLD + "Yes, leave " + group.getName());
					ItemStack no = noStack();
					ItemAPI.setDisplayName(no, ChatColor.GOLD + "No, stay in " + group.getName());
					confirmInv.setSlot(new Clickable(yes) {

						@Override
						public void clicked(Player p) {
							if (!group.isMember(p.getUniqueId())) {
								p.sendMessage(ChatColor.RED + "You are not a member of this group.");
								showScreen();
								return;
							}
							if (group.isDisciplined()) {
								p.sendMessage(ChatColor.RED + "This group is disciplined.");
								showScreen();
								return;
							}
							NameLayerPlugin.log(Level.INFO, p.getName() + " left " + group.getName() + " via the gui");
							group.removeMember(p.getUniqueId());
							p.sendMessage(ChatColor.GREEN + "You have left " + group.getName());
						}
					}, 11);
					confirmInv.setSlot(new Clickable(no) {

						@Override
						public void clicked(Player p) {
							showScreen();
						}
					}, 15);
					confirmInv.setSlot(new DecorationStack(info), 4);
					confirmInv.showInventory(p);
				}
			};
		}
		return c;
	}

	private Clickable getInfoStack() {
		Clickable c;
		ItemStack is = new ItemStack(Material.PAPER);
		ItemAPI.setDisplayName(is, ChatColor.GOLD + "Stats for " + group.getName());
		ItemAPI.addLore(is, ChatColor.DARK_AQUA + "Your current rank: " + ChatColor.YELLOW
				+ GroupRank.getNiceRankName(group.getRank(player.getUniqueId())));
		boolean hasGroupStatsPerm = groupManager.hasAccess(group, player.getUniqueId(),
				PermissionType.getPermission("GROUPSTATS"));
		if (groupManager.hasAccess(group, player.getUniqueId(), PermissionType.getPermission("MEMBERS"))
				|| hasGroupStatsPerm) {
			ItemAPI.addLore(is,
					ChatColor.AQUA + String.valueOf(group.getAllMembers(GroupRank.MEMBERS).size()) + " members");
		}
		if (groupManager.hasAccess(group, player.getUniqueId(), PermissionType.getPermission("MODS"))
				|| hasGroupStatsPerm) {
			ItemAPI.addLore(is, ChatColor.AQUA + String.valueOf(group.getAllMembers(GroupRank.MODS).size()) + " mods");
		}
		if (groupManager.hasAccess(group, player.getUniqueId(), PermissionType.getPermission("ADMINS"))
				|| hasGroupStatsPerm) {
			ItemAPI.addLore(is,
					ChatColor.AQUA + String.valueOf(group.getAllMembers(GroupRank.ADMINS).size()) + " admins");
		}
		if (groupManager.hasAccess(group, player.getUniqueId(), PermissionType.getPermission("OWNER"))
				|| hasGroupStatsPerm) {
			ItemAPI.addLore(is,
					ChatColor.AQUA + String.valueOf(group.getAllMembers(GroupRank.OWNER).size()) + " owner");
		}
		if (hasGroupStatsPerm) {
			ItemAPI.addLore(is,
					ChatColor.DARK_AQUA + String.valueOf(group.getAllMembers().size()) + " total group members");
			ItemAPI.addLore(is, ChatColor.DARK_AQUA + "Group owner: " + ChatColor.YELLOW
					+ NameAPI.getCurrentName(group.getOwner()));
		}
		c = new DecorationStack(is);
		return c;
	}
}
