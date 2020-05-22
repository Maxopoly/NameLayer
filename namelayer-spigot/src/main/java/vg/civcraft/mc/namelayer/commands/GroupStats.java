package vg.civcraft.mc.namelayer.commands;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.group.GroupLink;
import vg.civcraft.mc.namelayer.permission.PlayerType;
import vg.civcraft.mc.namelayer.permission.PlayerTypeHandler;

public class GroupStats extends AbstractGroupCommand {

	@Override
	public boolean execute(Player player, Group group, String[] args) {
		if (!permsCheck(group, player, NameLayerPlugin.getInstance().getNLPermissionManager().getGroupStats())) {
			return true;
		}
		player.sendMessage(ChatColor.GREEN + "Generating information, this may take a moment...");
		Bukkit.getScheduler().runTaskAsynchronously(NameLayerPlugin.getInstance(), () -> {
			String s = buildGroupInformation(group);
			Bukkit.getScheduler().runTask(NameLayerPlugin.getInstance(), () -> {
				player.sendMessage(s);
			});
		});
		return true;
	}

	private static String buildGroupInformation(Group group) {
		StringBuilder sb = new StringBuilder();
		sb.append(ChatColor.GREEN.toString() + ChatColor.BOLD + " --- " + group.getColoredName() + ChatColor.GREEN
				+ ChatColor.BOLD + " --- " + '\n');
		PlayerTypeHandler typeHandler = group.getPlayerTypeHandler();
		for (PlayerType type : typeHandler.getAllTypes()) {
			List<UUID> ofThatType = group.getAllTrackedByType(type);
			sb.append(ChatColor.GREEN);
			sb.append(type);
			if (type.getParent() != null) {
				sb.append(" (child of ");
				sb.append(type.getParent().getName());
				sb.append(")");
			}
			sb.append(" has ");
			sb.append(ofThatType.size());
			sb.append(" players: \n");
			sb.append(ChatColor.YELLOW);
			for (String name : fromUUIDCollectionToNames(ofThatType)) {
				sb.append(" - ");
				sb.append(name);
				sb.append('\n');
			}
			sb.append("\nInherited players:\n");
			for (GroupLink link : group.getIncomingLinks()) {
				if (link.getTargetType() != type) {
					continue;
				}
				printInheritedMembersRecursive(sb, link.getOriginatingGroup(), link.getOriginatingType());
			}
			sb.append("\n\n");
		}
		if (!group.getIncomingLinks().isEmpty()) {
			sb.append("Incoming group links are: \n");
			for (GroupLink link : group.getIncomingLinks()) {
				sb.append(" - Linking ");
				sb.append(link.getOriginatingType().getName());
				sb.append(" in ");
				sb.append(link.getOriginatingGroup().getColoredName());
				sb.append(ChatColor.YELLOW);
				sb.append(" to ");
				sb.append(link.getTargetType().getName());
				sb.append('\n');
			}
		}
		if (!group.getOutgoingLinks().isEmpty()) {
			sb.append("Outgoing group links are: \n");
			for (GroupLink link : group.getIncomingLinks()) {
				sb.append(" - Linking ");
				sb.append(link.getOriginatingType().getName());
				sb.append(" to ");
				sb.append(link.getTargetType().getName());
				sb.append(" in ");
				sb.append(link.getTargetGroup().getColoredName());
				sb.append(ChatColor.YELLOW);
				sb.append('\n');
			}
		}
		sb.append('\n');
		Map<UUID, PlayerType> invites = group.dumpInvites();
		if (invites.isEmpty()) {
			sb.append("There are no pending invites\n");
		}
		else {
			sb.append("Pending invites:\n");
			for(Entry<UUID, PlayerType> entry : invites.entrySet()) {
			sb.append(" - ");
			sb.append(NameAPI.getCurrentName(entry.getKey()));
			sb.append(" (");
			sb.append(entry.getValue().getName());
			sb.append(")\n");
			}
		}
		return sb.toString();
	}

	private static void printInheritedMembersRecursive(StringBuilder sb, Group group, PlayerType type) {
		for (GroupLink link : group.getIncomingLinks()) {
			if (!type.isEqualOrAbove(link.getTargetType())) {
				continue;
			}
			printInheritedMembersRecursive(sb, link.getOriginatingGroup(), link.getOriginatingType());
		}
		for (UUID player : group.getAllTrackedByType(type)) {
			sb.append(" - ");
			sb.append(NameAPI.getCurrentName(player));
			sb.append(" as ");
			sb.append(type.getName());
			sb.append(" from ");
			sb.append(group.getColoredName());
			sb.append(ChatColor.YELLOW);
		}
		for (PlayerType parent : type.getAllParents()) {
			for (UUID player : group.getAllTrackedByType(parent)) {
				sb.append(" - ");
				sb.append(NameAPI.getCurrentName(player));
				sb.append(" as ");
				sb.append(parent.getName());
				sb.append(" from ");
				sb.append(group.getColoredName());
				sb.append(ChatColor.YELLOW);
			}
		}
	}

	private static Collection<String> fromUUIDCollectionToNames(Collection<UUID> uuids) {
		return uuids.stream().map(NameAPI::getCurrentName).collect(Collectors.toList());
	}

	@Override
	public List<String> tabCompleteFurther(Player player, String[] args) {
		return Collections.emptyList();
	}
}
