package vg.civcraft.mc.namelayer.mc.commands;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.maxopoly.artemis.NameAPI;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupLink;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.GroupRankHandler;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.mc.GroupAPI;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;

@CivCommand(id = "nlgs")
public class GroupStats extends StandaloneCommand {

	private static String buildGroupInformation(Group group) {
		StringBuilder sb = new StringBuilder();
		sb.append(ChatColor.GREEN.toString() + ChatColor.BOLD + " --- " + group.getColoredName() + ChatColor.GREEN
				+ ChatColor.BOLD + " --- " + '\n');
		GroupRankHandler typeHandler = group.getGroupRankHandler();
		for (GroupRank type : typeHandler.getAllRanks()) {
			Set<UUID> ofThatType = group.getAllTrackedByType(type);
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
				if (link.getTargetRank() != type) {
					continue;
				}
				printInheritedMembersRecursive(sb, link.getOriginatingGroup(), link.getOriginatingRank());
			}
			sb.append("\n\n");
		}
		if (!group.getIncomingLinks().isEmpty()) {
			sb.append("Incoming group links are: \n");
			for (GroupLink link : group.getIncomingLinks()) {
				sb.append(" - Linking ");
				sb.append(link.getOriginatingRank().getName());
				sb.append(" in ");
				sb.append(link.getOriginatingGroup().getColoredName());
				sb.append(ChatColor.YELLOW);
				sb.append(" to ");
				sb.append(link.getTargetRank().getName());
				sb.append('\n');
			}
		}
		if (!group.getOutgoingLinks().isEmpty()) {
			sb.append("Outgoing group links are: \n");
			for (GroupLink link : group.getIncomingLinks()) {
				sb.append(" - Linking ");
				sb.append(link.getOriginatingRank().getName());
				sb.append(" to ");
				sb.append(link.getTargetRank().getName());
				sb.append(" in ");
				sb.append(link.getTargetGroup().getColoredName());
				sb.append(ChatColor.YELLOW);
				sb.append('\n');
			}
		}
		sb.append('\n');
		Map<UUID, GroupRank> invites = group.getAllInvites();
		if (invites.isEmpty()) {
			sb.append("There are no pending invites\n");
		} else {
			sb.append("Pending invites:\n");
			for (Entry<UUID, GroupRank> entry : invites.entrySet()) {
				sb.append(" - ");
				sb.append(NameAPI.getName(entry.getKey()));
				sb.append(" (");
				sb.append(entry.getValue().getName());
				sb.append(")\n");
			}
		}
		return sb.toString();
	}

	private static void printInheritedMembersRecursive(StringBuilder sb, Group group, GroupRank type) {
		for (GroupLink link : group.getIncomingLinks()) {
			if (!type.isEqualOrAbove(link.getTargetRank())) {
				continue;
			}
			printInheritedMembersRecursive(sb, link.getOriginatingGroup(), link.getOriginatingRank());
		}
		for (UUID player : group.getAllTrackedByType(type)) {
			sb.append(" - ");
			sb.append(NameAPI.getName(player));
			sb.append(" as ");
			sb.append(type.getName());
			sb.append(" from ");
			sb.append(group.getColoredName());
			sb.append(ChatColor.YELLOW);
		}
		for (GroupRank parent : type.getAllParents()) {
			for (UUID player : group.getAllTrackedByType(parent)) {
				sb.append(" - ");
				sb.append(NameAPI.getName(player));
				sb.append(" as ");
				sb.append(parent.getName());
				sb.append(" from ");
				sb.append(group.getColoredName());
				sb.append(ChatColor.YELLOW);
			}
		}
	}

	private static Collection<String> fromUUIDCollectionToNames(Collection<UUID> uuids) {
		return uuids.stream().map(NameAPI::getName).collect(Collectors.toList());
	}

/*	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Group group = GroupAPI.getGroup(args[0]);
		if (group == null) {
			sender.sendMessage(String.format("%sThe group %s does not exist", ChatColor.RED, args[0]));
			return true;
		}
		Player player = (Player) sender;
		//TODO: Add GroupStats
		PermissionType perm;
		if (!GroupAPI.hasPermission(player, group, perm)) {
			sender.sendMessage(String.format(
					"%sTo do this you need the permission %s%s %sfor the group %s%s which you do not have",
					ChatColor.RED, ChatColor.YELLOW, perm.getName(), ChatColor.RED, group.getColoredName(),
					ChatColor.RED));
			return true;
		}
		player.sendMessage(ChatColor.GREEN + "Generating information, this may take a moment...");
		Bukkit.getScheduler().runTaskAsynchronously(NameLayerPlugin.getInstance(), () -> {
			String s = buildGroupInformation(group);
			Bukkit.getScheduler().runTask(NameLayerPlugin.getInstance(), () -> player.sendMessage(s));
		});
		return true;
	}*/

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if (args.length == 1) {
			return NameLayerTabCompletion.completeGroupName(args[0], (Player) sender);
		}
		return Collections.emptyList();
	}

	@Override
	public boolean execute(CommandSender arg0, String[] arg1) {
		// TODO Auto-generated method stub
		return false;
	}
}
