package vg.civcraft.mc.namelayer.mc.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.maxopoly.artemis.NameAPI;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.core.GroupRank;
import vg.civcraft.mc.namelayer.core.PermissionTracker;
import vg.civcraft.mc.namelayer.mc.GroupAPI;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;
import vg.civcraft.mc.namelayer.mc.util.MsgUtils;

@CivCommand(id = "nllm")
public class ListMembers extends NameLayerCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		Group group = GroupAPI.getGroup(args[0]);
		if (group == null) {
			MsgUtils.sendGroupNotExistMsg(player.getUniqueId(), args[0]);
			return true;
		}
		PermissionTracker permTracker = NameLayerPlugin.getInstance().getGroupTracker().getPermissionTracker();
		List<GroupRank> listableRanks = new ArrayList<>();
		UUID uuid = resolveUUID(sender);
		for (GroupRank rank : group.getGroupRankHandler().getAllRanks()) {
			if (rank == group.getGroupRankHandler().getDefaultNonMemberRank()) {
				continue;
			}
			if (GroupAPI.hasPermission(uuid, group, permTracker.getListPermission(rank.getId()))) {
				listableRanks.add(rank);
			}
		}
		if (listableRanks.isEmpty()) {
			sender.sendMessage(
					String.format("%sYou do not have permission to list any members of %s", ChatColor.RED, group.getColoredName()));
			return true;
		}
		NameAPI.batchPreLoadNames(group.getAllMembers(), () -> sendMsg(sender, group, listableRanks));
		return true;
	}

	private void sendMsg(CommandSender sender, Group group, List<GroupRank> listableRanks) {
		StringBuilder sb = new StringBuilder();
		sb.append("Listing viewable players for ");
		sb.append(group.getColoredName());
		sb.append(":\n");
		for (GroupRank rank : listableRanks) {
			Set<UUID> members = group.getAllTrackedByType(rank);
			if (members.isEmpty()) {
				continue;
			}
			sb.append(ChatColor.YELLOW);
			sb.append(rank.getName());
			sb.append(" has ");
			sb.append(members.size());
			sb.append(" players:\n");
			for (UUID member : members) {
				sb.append(" - ");
				sb.append(NameAPI.getName(member));
				sb.append('\n');
				sb.append('\n');
			}
		}
		MsgUtils.sendMsg(sender, sb.toString());
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if (args.length == 1) {
			return NameLayerTabCompletion.completeGroupName(args[0], (Player) sender);
		}
		return Collections.emptyList();
	}
}
