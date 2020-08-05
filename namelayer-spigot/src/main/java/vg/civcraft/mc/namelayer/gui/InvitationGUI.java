package vg.civcraft.mc.namelayer.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import vg.civcraft.mc.civmodcore.api.ItemAPI;
import vg.civcraft.mc.civmodcore.chatDialog.Dialog;
import vg.civcraft.mc.civmodcore.inventorygui.IClickable;
import vg.civcraft.mc.civmodcore.inventorygui.LClickable;
import vg.civcraft.mc.civmodcore.inventorygui.components.ComponableInventory;
import vg.civcraft.mc.civmodcore.inventorygui.components.ContentAligners;
import vg.civcraft.mc.civmodcore.inventorygui.components.Scrollbar;
import vg.civcraft.mc.namelayer.GroupAPI;
import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.commands.NameLayerTabCompletion;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.permission.GroupRank;
import vg.civcraft.mc.namelayer.permission.GroupRankHandler;

public class InvitationGUI extends NameLayerGroupGUI {

	private GroupRank selectedType;
	private MainGroupGUI parent;
	private ComponableInventory inventory;
	private boolean blacklist;

	public InvitationGUI(Group g, Player p, MainGroupGUI parent, boolean blacklist) {
		super(g, p);
		this.blacklist = blacklist;
		this.parent = parent;
		this.inventory = parent.getInventory();
	}

	public void showScreen() {
		List<IClickable> content = new ArrayList<>();
		GroupRankHandler rankHandler = group.getGroupRankHandler();
		for (GroupRank rank : rankHandler.getAllRanks()) {
			if (blacklist != rankHandler.isBlacklistedRank(rank)) {
				continue;
			}
			if (!GroupAPI.hasPermission(player, group, rank.getInvitePermissionType())) {
				continue;
			}
			ItemStack is = GUIGroupOverview.getHashedItem(rank.getName().hashCode());
			ItemAPI.setDisplayName(is, ChatColor.GOLD + rank.getName());
			content.add(new LClickable(is, p -> inviteTo(rank)));
		}
		Scrollbar rankSection = new Scrollbar(content, 45, 45, ContentAligners.getCenteredInOrder(content.size(), 45));
		inventory.addComponent(rankSection, i -> true);
		inventory.show();
	}

	private void inviteTo(GroupRank rank) {
		String action = blacklist ? "blacklist" : "invite";
		player.sendMessage(String.format(
				"%sEnter the name of the player to %s as %s%s%s or \"cancel\" to exit this prompt. You may also enter the names "
						+ "of multiple players, separated with spaces to %s all of them.",
				ChatColor.GOLD, action, ChatColor.AQUA, rank.getName(), ChatColor.GOLD, action));
		new Dialog(player, NameLayerPlugin.getInstance()) {
			public void onReply(String[] message) {
				for (String s : message) {
					if (s.equalsIgnoreCase("cancel")) {
						parent.showScreen();
						return;
					}
					if (blacklist) {
						interactionManager.blacklistPlayer(player.getUniqueId(), group.getName(), s, rank.getName(),
								player::sendMessage);
					}
					else {
						interactionManager.inviteMember(player.getUniqueId(), group.getName(), s, rank.getName(),
								player::sendMessage);
					}
				}
				parent.showScreen();
			}

			public List<String> onTabComplete(String word, String[] msg) {
				return NameLayerTabCompletion.completePlayer(word);
			}

		};
	}
}
